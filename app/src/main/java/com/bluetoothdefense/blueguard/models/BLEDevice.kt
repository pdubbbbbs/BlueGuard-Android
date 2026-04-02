package com.bluetoothdefense.blueguard.models

import kotlin.math.pow

data class BLEDevice(
  val address: String,
  var name: String? = null,
  var rssi: Int = -100,
  val firstSeen: Long = System.currentTimeMillis(),
  var lastSeen: Long = System.currentTimeMillis(),
  var advCount: Int = 0,
  var threatScore: Int = 0,
  var category: String = "Unknown",
  var manufacturer: String? = null,
) {
  val displayName: String get() = name ?: "Unknown Device"

  val distanceFeet: Double? get() {
    if (rssi == 0) return null
    val txPower = -59.0
    val n = 2.5
    val meters = 10.0.pow((txPower - rssi) / (10.0 * n))
    return meters * 3.281
  }

  val threatLevel: ThreatLevel get() = when {
    threatScore >= 80 -> ThreatLevel.DANGER
    threatScore >= 50 -> ThreatLevel.SUSPICIOUS
    threatScore >= 20 -> ThreatLevel.LOW_RISK
    else -> ThreatLevel.SAFE
  }

  val signalStrength: String get() = when {
    rssi >= -40 -> "Strong"
    rssi >= -55 -> "Good"
    rssi >= -70 -> "Fair"
    rssi >= -85 -> "Weak"
    else -> "Faint"
  }
}

enum class ThreatLevel(val label: String, val colorHex: Long) {
  SAFE("Safe", 0xFF00FF88),
  LOW_RISK("Low Risk", 0xFFFFAA00),
  SUSPICIOUS("Suspicious", 0xFFFF6600),
  DANGER("DANGER", 0xFFFF3366);
}
