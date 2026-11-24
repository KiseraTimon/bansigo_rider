// app/src/main/java/com/project/bansigo_rider/ui/screens/home/sections/HomeGeolocationSection.kt
package com.project.bansigo_rider.ui.screens.home.sections

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.config.AppConfig
import com.project.bansigo_rider.ui.theme.BansiGreen
import com.project.bansigo_rider.ui.theme.BansiGreenSubtle
import com.project.bansigo_rider.ui.theme.BorderLight
import com.project.bansigo_rider.ui.theme.RoyalNavy
import com.project.bansigo_rider.ui.theme.SurfaceCard
import com.project.bansigo_rider.ui.theme.TextMuted
import com.project.bansigo_rider.ui.theme.TextPrimary
import com.project.bansigo_rider.ui.theme.TextSecondary

@Composable
fun HomeGeolocationSection(
    currentLat: Double,
    currentLng: Double,
    isSyncingLocation: Boolean,
    onSyncLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gps_beacon_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Pulsing GPS Beacon
                Box(
                    modifier = Modifier.size(38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(BansiGreen.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(BansiGreenSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "GPS Beacon",
                            tint = BansiGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = AppConfig.SUPERMARKET_NAME,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(BansiGreenSubtle)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${AppConfig.MAX_DELIVERY_RADIUS_KM.toInt()} km Zone",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BansiGreen
                            )
                        }
                    }
                    Text(
                        text = "Migori Dispatch Dock (${String.format("%.4f", currentLat)}°, ${String.format("%.4f", currentLng)}°)",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            // Sync Button
            IconButton(
                onClick = onSyncLocation,
                enabled = !isSyncingLocation,
                modifier = Modifier.size(36.dp)
            ) {
                if (isSyncingLocation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = BansiGreen,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync GPS Telemetry",
                        tint = RoyalNavy,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
