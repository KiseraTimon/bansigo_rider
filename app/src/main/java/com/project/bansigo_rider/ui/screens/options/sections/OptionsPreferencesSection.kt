// app/src/main/java/com/project/bansigo_rider/ui/screens/options/sections/OptionsPreferencesSection.kt
package com.project.bansigo_rider.ui.screens.options.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.storage.SessionManager
import com.project.bansigo_rider.ui.theme.*

@Composable
fun OptionsPreferencesSection(
    sessionManager: SessionManager,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var soundEnabled by remember { mutableStateOf(sessionManager.isSoundEnabled()) }
    var vibrationEnabled by remember { mutableStateOf(sessionManager.isVibrationEnabled()) }
    var lowDataMode by remember { mutableStateOf(sessionManager.isLowDataMode()) }
    var autoRecenterMap by remember { mutableStateOf(sessionManager.isAutoRecenterMap()) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        OptionsSubPageTopBar(
            title = "App Preferences",
            subtitle = "Alerts, telemetry & bandwidth optimization",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alert Preferences Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Dispatch & Mission Alerts",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    PreferenceSwitchRow(
                        icon = Icons.Default.NotificationsActive,
                        title = "Sound Chime on Offer",
                        subtitle = "Audible ringing alert when an incoming dispatch offer is found",
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            sessionManager.setSoundEnabled(it)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    PreferenceSwitchRow(
                        icon = Icons.Default.Vibration,
                        title = "Vibrate on Incoming Offer",
                        subtitle = "Continuous haptic vibration pattern for pocket detection while driving",
                        checked = vibrationEnabled,
                        onCheckedChange = {
                            vibrationEnabled = it
                            sessionManager.setVibrationEnabled(it)
                        }
                    )
                }
            }

            // Connectivity & Battery Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Data & Device Efficiency",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    PreferenceSwitchRow(
                        icon = Icons.Default.BatterySaver,
                        title = "Low-Bandwidth Mode (2G / 3G)",
                        subtitle = "Compresses network telemetry payloads and disables unnecessary animations",
                        checked = lowDataMode,
                        onCheckedChange = {
                            lowDataMode = it
                            sessionManager.setLowDataMode(it)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = BorderLight, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    PreferenceSwitchRow(
                        icon = Icons.Default.Sensors,
                        title = "Auto-Recenter Navigation",
                        subtitle = "Automatically re-centers OpenStreetMap viewport on your GPS coordinate",
                        checked = autoRecenterMap,
                        onCheckedChange = {
                            autoRecenterMap = it
                            sessionManager.setAutoRecenterMap(it)
                        }
                    )
                }
            }

            // Information note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = RoyalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Preferences are saved locally on your device and applied immediately to live delivery navigation and dispatch alerts.",
                        fontSize = 12.sp,
                        color = RoyalNavy,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PreferenceSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(InputBackground, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RoyalNavy,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SurfaceLight,
                checkedTrackColor = RoyalNavy,
                uncheckedThumbColor = SurfaceLight,
                uncheckedTrackColor = BorderLight
            )
        )
    }
}
