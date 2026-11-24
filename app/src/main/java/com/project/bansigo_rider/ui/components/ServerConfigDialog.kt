// app/src/main/java/com/project/bansigo_rider/ui/components/ServerConfigDialog.kt
package com.project.bansigo_rider.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.config.AppConfig
import com.project.bansigo_rider.core.network.ApiClient
import com.project.bansigo_rider.core.storage.SessionManager
import com.project.bansigo_rider.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ServerConfigDialog(
    sessionManager: SessionManager,
    apiClient: ApiClient,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    var selectedPreset by remember {
        mutableStateOf(
            when (AppConfig.current.apiBaseUrl) {
                AppConfig.USB_DEBUG_URL -> "usb"
                AppConfig.EMULATOR_URL -> "emulator"
                AppConfig.LOCALHOST_URL -> "localhost"
                AppConfig.DEFAULT_WIFI_URL -> "wifi"
                else -> "custom"
            }
        )
    }

    var customUrl by remember { mutableStateOf(AppConfig.current.apiBaseUrl) }
    var pingStatus by remember { mutableStateOf<String?>(null) }
    var isPinging by remember { mutableStateOf(false) }
    var pingSuccess by remember { mutableStateOf<Boolean?>(null) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Dns,
                    contentDescription = null,
                    tint = RoyalNavy,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Server Configuration",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalNavy
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select or specify the target FastAPI backend environment:",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Option 1: USB Debugging Cable (127.0.0.1:8000 via adb reverse)
                PresetOption(
                    title = "USB Debugging Cable (Recommended)",
                    url = "http://127.0.0.1:8000/api (via adb reverse)",
                    selected = selectedPreset == "usb",
                    onClick = {
                        selectedPreset = "usb"
                        customUrl = AppConfig.USB_DEBUG_URL
                    }
                )

                // Option 2: Physical Device / Wi-Fi IP
                PresetOption(
                    title = "Developer Wi-Fi Host (LAN)",
                    url = AppConfig.DEFAULT_WIFI_URL,
                    selected = selectedPreset == "wifi",
                    onClick = {
                        selectedPreset = "wifi"
                        customUrl = AppConfig.DEFAULT_WIFI_URL
                    }
                )

                // Option 2: Android Emulator (10.0.2.2)
                PresetOption(
                    title = "Android Emulator (10.0.2.2)",
                    url = AppConfig.EMULATOR_URL,
                    selected = selectedPreset == "emulator",
                    onClick = {
                        selectedPreset = "emulator"
                        customUrl = AppConfig.EMULATOR_URL
                    }
                )

                // Option 3: Localhost (8000)
                PresetOption(
                    title = "Localhost (Port 8000)",
                    url = AppConfig.LOCALHOST_URL,
                    selected = selectedPreset == "localhost",
                    onClick = {
                        selectedPreset = "localhost"
                        customUrl = AppConfig.LOCALHOST_URL
                    }
                )

                // Option 4: Custom URL
                PresetOption(
                    title = "Custom IP / Hostname",
                    url = "Specify custom HTTP URL",
                    selected = selectedPreset == "custom",
                    onClick = { selectedPreset = "custom" }
                )

                if (selectedPreset == "custom") {
                    Spacer(modifier = Modifier.height(8.dp))
                    BansiTextField(
                        value = customUrl,
                        onValueChange = { customUrl = it },
                        label = "Base API URL",
                        placeholder = "http://192.168.1.xxx:8000/api"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ping Test Button
                BansiOutlineButton(
                    text = if (isPinging) "Testing Connection..." else "Test Server Ping",
                    icon = Icons.Default.NetworkCheck,
                    onClick = {
                        isPinging = true
                        pingStatus = null
                        pingSuccess = null
                        scope.launch {
                            val result = apiClient.pingServer(customUrl)
                            isPinging = false
                            if (result.isSuccess) {
                                pingSuccess = true
                                pingStatus = "Connected! Latency: ${result.getOrNull()} ms"
                            } else {
                                pingSuccess = false
                                pingStatus = "Unreachable: ${result.exceptionOrNull()?.localizedMessage ?: "Timeout"}"
                            }
                        }
                    },
                    enabled = !isPinging
                )

                if (pingStatus != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (pingSuccess == true) BansiGreenLight else BansiCrimsonLight,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (pingSuccess == true) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (pingSuccess == true) BansiGreenDark else BansiCrimsonDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = pingStatus!!,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (pingSuccess == true) BansiGreenDark else BansiCrimsonDark
                        )
                    }
                }
            }
        },
        confirmButton = {
            BansiPrimaryButton(
                text = "Save & Apply",
                onClick = {
                    sessionManager.saveCustomApiUrl(customUrl.trim())
                    onSaved()
                    onDismiss()
                },
                modifier = Modifier.width(130.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = SurfaceLight
    )
}

@Composable
private fun PresetOption(
    title: String,
    url: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = url,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}
