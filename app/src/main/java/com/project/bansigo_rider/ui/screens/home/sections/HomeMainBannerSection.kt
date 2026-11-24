// app/src/main/java/com/project/bansigo_rider/ui/screens/home/sections/HomeMainBannerSection.kt
package com.project.bansigo_rider.ui.screens.home.sections

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.ApprovalStatus
import com.project.bansigo_rider.core.models.RiderProfile
import com.project.bansigo_rider.core.models.RiderStatus
import com.project.bansigo_rider.ui.theme.BansiGreen
import com.project.bansigo_rider.ui.theme.BansiGreenDark
import com.project.bansigo_rider.ui.theme.BansiGreenLight
import com.project.bansigo_rider.ui.theme.BansiGreenSubtle
import com.project.bansigo_rider.ui.theme.BorderLight
import com.project.bansigo_rider.ui.theme.RoyalNavy
import com.project.bansigo_rider.ui.theme.RoyalNavyDark
import com.project.bansigo_rider.ui.theme.StatusAvailable
import com.project.bansigo_rider.ui.theme.StatusOffline
import com.project.bansigo_rider.ui.theme.SurfaceCard
import com.project.bansigo_rider.ui.theme.TextMuted
import com.project.bansigo_rider.ui.theme.TextPrimary
import com.project.bansigo_rider.ui.theme.TextSecondary
import java.util.Calendar

@Composable
fun HomeMainBannerSection(
    riderProfile: RiderProfile?,
    riderName: String,
    onToggleAvailability: (Boolean) -> Unit,
    isUpdatingStatus: Boolean,
    modifier: Modifier = Modifier
) {
    val isOnline = riderProfile?.status == RiderStatus.AVAILABLE

    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 4..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    val statusBgColor by animateColorAsState(
        targetValue = if (isOnline) BansiGreen else Color(0xFF6B7280),
        animationSpec = tween(350),
        label = "status_color"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = RoyalNavyDark,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RoyalNavy,
                            RoyalNavyDark
                        )
                    )
                )
                .padding(18.dp)
        ) {
            // Header Row: Greeting & Online Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$greeting,",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = riderName.ifBlank { "Delivery Partner" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // High-contrast Availability Toggle
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = if (isOnline) BansiGreen.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.15f),
                    border = BorderStroke(
                        1.5.dp,
                        if (isOnline) BansiGreen else Color.White.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.clickable(enabled = !isUpdatingStatus) {
                        onToggleAvailability(!isOnline)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isUpdatingStatus) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(statusBgColor)
                            )
                        }
                        Text(
                            text = if (isOnline) "ONLINE" else "OFFLINE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-bar with Vehicle Type and Vetting Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vehicle Chip
                val vehicleText = riderProfile?.vehicleType?.label ?: "Boda Boda"
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
                            contentDescription = "Vehicle",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = vehicleText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                // Vetting Status Chip
                val isApproved = riderProfile?.approvalStatus == ApprovalStatus.APPROVED
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isApproved) BansiGreen.copy(alpha = 0.2f) else Color(0xFFF59E0B).copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isApproved) Icons.Default.VerifiedUser else Icons.Default.Schedule,
                            contentDescription = "Vetting",
                            tint = if (isApproved) BansiGreen else Color(0xFFF59E0B),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = riderProfile?.approvalStatus?.label ?: "Vetting Pending",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isApproved) BansiGreen else Color(0xFFFBBF24)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Plate / ID preview
                riderProfile?.vehiclePlate?.let { plate ->
                    if (plate.isNotBlank()) {
                        Text(
                            text = plate,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
