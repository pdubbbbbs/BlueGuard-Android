package com.bluetoothdefense.blueguard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluetoothdefense.blueguard.ui.BluePrimary
import com.bluetoothdefense.blueguard.ui.DarkBg

@Composable
fun PermissionScreen(onRequestPermissions: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBg)
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Icon(
      Icons.Default.Shield,
      contentDescription = null,
      tint = BluePrimary,
      modifier = Modifier.size(64.dp),
    )
    Spacer(Modifier.height(16.dp))
    Text(
      "BlueGuard Security",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = Color.White,
    )
    Spacer(Modifier.height(8.dp))
    Text(
      "Hardware-Level Bluetooth Defense",
      fontSize = 14.sp,
      color = Color(0xFF6B7280),
    )
    Spacer(Modifier.height(32.dp))

    Icon(
      Icons.Default.BluetoothSearching,
      contentDescription = null,
      tint = BluePrimary.copy(alpha = 0.7f),
      modifier = Modifier.size(48.dp),
    )
    Spacer(Modifier.height(16.dp))
    Text(
      "BlueGuard needs Bluetooth and Location permissions to scan for nearby BLE devices and detect security threats in your environment.",
      fontSize = 14.sp,
      color = Color(0xFF9CA3AF),
      textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(24.dp))

    Button(
      onClick = onRequestPermissions,
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text("Grant Permissions", modifier = Modifier.padding(vertical = 4.dp))
    }
  }
}
