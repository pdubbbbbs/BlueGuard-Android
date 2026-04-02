package com.bluetoothdefense.blueguard.services

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.bluetoothdefense.blueguard.models.BLEDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BLEScanner(context: Context) {

  private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
  private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
  private var scanner: BluetoothLeScanner? = null

  private val _devices = MutableStateFlow<Map<String, BLEDevice>>(emptyMap())
  val devices: StateFlow<Map<String, BLEDevice>> = _devices.asStateFlow()

  private val _isScanning = MutableStateFlow(false)
  val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

  private val attackToolSignatures = mapOf(
    "flipper" to "Flipper Zero",
    "ubertooth" to "Ubertooth",
    "sniffle" to "Sniffle",
    "gattacker" to "GATTacker",
    "bettercap" to "Bettercap",
    "btlejack" to "BtleJack",
    "nrf sniffer" to "nRF Sniffer",
  )

  private val scanCallback = object : ScanCallback() {
    override fun onScanResult(callbackType: Int, result: ScanResult) {
      processResult(result)
    }

    override fun onBatchScanResults(results: List<ScanResult>) {
      results.forEach { processResult(it) }
    }
  }

  @SuppressLint("MissingPermission")
  private fun processResult(result: ScanResult) {
    val address = result.device.address
    val name = result.scanRecord?.deviceName ?: result.device.name
    val rssi = result.rssi
    val now = System.currentTimeMillis()

    val current = _devices.value.toMutableMap()
    val existing = current[address]

    if (existing != null) {
      current[address] = existing.copy(
        rssi = rssi,
        lastSeen = now,
        advCount = existing.advCount + 1,
        name = existing.name ?: name,
      )
    } else {
      val (category, threat) = categorize(name)
      current[address] = BLEDevice(
        address = address,
        name = name,
        rssi = rssi,
        firstSeen = now,
        lastSeen = now,
        advCount = 1,
        threatScore = threat,
        category = category,
      )
    }

    // Proximity threat for unknown close devices
    val dev = current[address]!!
    if (dev.name == null && dev.distanceFeet != null && dev.distanceFeet!! < 5) {
      current[address] = dev.copy(threatScore = maxOf(dev.threatScore, 25))
    }

    // Prune stale (120s)
    val cutoff = now - 120_000
    _devices.value = current.filter { it.value.lastSeen > cutoff }
  }

  private fun categorize(name: String?): Pair<String, Int> {
    if (name == null) return "Unknown" to 5
    val lower = name.lowercase()

    for ((sig, tool) in attackToolSignatures) {
      if (sig in lower) return "Attack Tool ($tool)" to 80
    }

    if (listOf("tile", "airtag", "smarttag", "chipolo").any { it in lower })
      return "Tracker" to 30

    return when {
      listOf("iphone", "ipad", "android", "pixel", "galaxy", "phone").any { it in lower } -> "Phone" to 0
      listOf("macbook", "laptop", "thinkpad", "surface").any { it in lower } -> "Computer" to 0
      listOf("airpod", "beats", "bose", "jbl", "headphone", "buds").any { it in lower } -> "Audio" to 0
      listOf("watch", "band", "fitbit", "garmin").any { it in lower } -> "Wearable" to 0
      listOf("tv", "roku", "chromecast", "fire stick").any { it in lower } -> "Media" to 0
      listOf("hue", "nest", "ring", "ecobee", "smart").any { it in lower } -> "Smart Home" to 0
      else -> "Identified" to 0
    }
  }

  @SuppressLint("MissingPermission")
  fun startScan() {
    if (_isScanning.value) return
    scanner = bluetoothAdapter?.bluetoothLeScanner ?: return

    val settings = ScanSettings.Builder()
      .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
      .setReportDelay(0)
      .build()

    scanner?.startScan(null, settings, scanCallback)
    _isScanning.value = true
  }

  @SuppressLint("MissingPermission")
  fun stopScan() {
    scanner?.stopScan(scanCallback)
    _isScanning.value = false
  }

  fun toggleScan() {
    if (_isScanning.value) stopScan() else startScan()
  }
}
