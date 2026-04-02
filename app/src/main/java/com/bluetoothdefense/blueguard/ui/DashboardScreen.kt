package com.bluetoothdefense.blueguard.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluetoothdefense.blueguard.models.BLEDevice
import com.bluetoothdefense.blueguard.models.ThreatLevel

@Composable
fun DashboardScreen(
  devices: Map<String, BLEDevice>,
  isScanning: Boolean,
  onToggleScan: () -> Unit,
) {
  val sortedDevices = remember(devices) {
    devices.values.sortedWith(
      compareByDescending<BLEDevice> { it.threatScore }
        .thenByDescending { it.name != null }
        .thenBy { it.firstSeen }
    )
  }

  val threatCount = devices.values.count { it.threatScore >= 50 }
  val maxThreat = devices.values.maxOfOrNull { it.threatScore } ?: 0
  val overallLevel = when {
    maxThreat >= 80 -> ThreatLevel.DANGER
    maxThreat >= 50 -> ThreatLevel.SUSPICIOUS
    maxThreat >= 20 -> ThreatLevel.LOW_RISK
    else -> ThreatLevel.SAFE
  }
  val levelColor by animateColorAsState(Color(overallLevel.colorHex), label = "level")

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBg)
  ) {
    // Header
    Surface(
      color = DarkSurface,
      tonalElevation = 4.dp,
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          Icons.Default.Shield,
          contentDescription = null,
          tint = BluePrimary,
          modifier = Modifier.size(32.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
          "BlueGuard",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = Color.White,
        )
        Spacer(Modifier.width(8.dp))
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = BluePrimary.copy(alpha = 0.2f),
        ) {
          Text(
            "PATENT PENDING",
            fontSize = 9.sp,
            color = BluePrimary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
          )
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onToggleScan) {
          Icon(
            if (isScanning) Icons.Default.Stop else Icons.Default.PlayArrow,
            contentDescription = if (isScanning) "Stop" else "Scan",
            tint = if (isScanning) RedDanger else GreenSafe,
          )
        }
      }
    }

    // Threat Meter
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp),
      shape = RoundedCornerShape(12.dp),
      color = DarkCard,
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(levelColor)
          )
          Spacer(Modifier.width(8.dp))
          Text(
            overallLevel.label,
            fontWeight = FontWeight.Bold,
            color = levelColor,
            fontSize = 18.sp,
          )
          Spacer(Modifier.weight(1f))
          Text(
            "${devices.size} devices",
            color = Color(0xFF9CA3AF),
            fontSize = 13.sp,
          )
          if (threatCount > 0) {
            Spacer(Modifier.width(12.dp))
            Text(
              "$threatCount threats",
              color = levelColor,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
            )
          }
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
          progress = { (maxThreat / 100f).coerceIn(0f, 1f) },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = levelColor,
          trackColor = Color(0xFF1E293B),
        )
      }
    }

    // Scanning indicator
    if (isScanning) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        CircularProgressIndicator(
          modifier = Modifier.size(12.dp),
          strokeWidth = 2.dp,
          color = BluePrimary,
        )
        Spacer(Modifier.width(8.dp))
        Text(
          "Scanning for BLE devices...",
          fontSize = 12.sp,
          color = Color(0xFF6B7280),
        )
      }
    }

    // Device List
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp),
      contentPadding = PaddingValues(vertical = 8.dp),
    ) {
      items(sortedDevices, key = { it.address }) { device ->
        DeviceRow(device)
      }

      if (sortedDevices.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              if (isScanning) "Scanning..." else "Tap play to start scanning",
              color = Color(0xFF6B7280),
            )
          }
        }
      }
    }
  }
}

@Composable
fun DeviceRow(device: BLEDevice) {
  val threatColor = Color(device.threatLevel.colorHex)

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = DarkCard,
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      // Threat dot
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(threatColor)
      )
      Spacer(Modifier.width(12.dp))

      // Device info
      Column(modifier = Modifier.weight(1f)) {
        Text(
          device.displayName,
          color = Color.White,
          fontWeight = FontWeight.Medium,
          fontSize = 14.sp,
          maxLines = 1,
        )
        Text(
          device.category,
          color = Color(0xFF6B7280),
          fontSize = 11.sp,
        )
      }

      // RSSI
      Column(horizontalAlignment = Alignment.End) {
        Text(
          "${device.rssi} dBm",
          color = Color(0xFF9CA3AF),
          fontSize = 12.sp,
        )
        device.distanceFeet?.let { dist ->
          if (dist < 200) {
            Text(
              "${dist.toInt()} ft",
              color = Color(0xFF6B7280),
              fontSize = 11.sp,
            )
          }
        }
      }

      Spacer(Modifier.width(12.dp))

      // Threat badge
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = threatColor.copy(alpha = 0.15f),
      ) {
        Text(
          device.threatLevel.label,
          color = threatColor,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
      }
    }
  }
}
