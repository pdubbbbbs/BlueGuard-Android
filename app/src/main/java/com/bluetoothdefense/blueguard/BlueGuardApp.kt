package com.bluetoothdefense.blueguard

import android.app.Application
import com.bluetoothdefense.blueguard.services.BLEScanner

class BlueGuardApp : Application() {
  lateinit var bleScanner: BLEScanner
    private set

  override fun onCreate() {
    super.onCreate()
    bleScanner = BLEScanner(this)
  }
}
