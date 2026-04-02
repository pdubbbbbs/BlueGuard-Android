package com.bluetoothdefense.blueguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.bluetoothdefense.blueguard.services.BLEScanner
import com.bluetoothdefense.blueguard.ui.BlueGuardTheme
import com.bluetoothdefense.blueguard.ui.DashboardScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

class MainActivity : ComponentActivity() {

  private lateinit var bleScanner: BLEScanner

  @OptIn(ExperimentalPermissionsApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    bleScanner = (application as BlueGuardApp).bleScanner

    setContent {
      BlueGuardTheme {
        val permissions = rememberMultiplePermissionsState(
          listOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
          )
        )

        if (permissions.allPermissionsGranted) {
          val devices by bleScanner.devices.collectAsState()
          val isScanning by bleScanner.isScanning.collectAsState()

          DashboardScreen(
            devices = devices,
            isScanning = isScanning,
            onToggleScan = { bleScanner.toggleScan() },
          )
        } else {
          PermissionScreen(
            onRequestPermissions = { permissions.launchMultiplePermissionRequest() }
          )
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    bleScanner.startScan()
  }

  override fun onPause() {
    super.onPause()
    bleScanner.stopScan()
  }
}
