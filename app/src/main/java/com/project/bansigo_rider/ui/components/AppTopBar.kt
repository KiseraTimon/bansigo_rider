// app/src/main/java/com/project/bansigo_rider/ui/components/AppTopBar.kt
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.ElectricMoped
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.ApprovalStatus
import com.project.bansigo_rider.core.models.RiderStatus
import com.project.bansigo_rider.ui.theme.*

@Composable
fun AppTopBar(
    title: String,
    riderStatus: RiderStatus,
    approvalStatus: ApprovalStatus,
    onToggleAvailability: (Boolean) -> Unit,
    onOpenServerConfig: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
    isRefreshing: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceLight)
            .statusBarsPadding()
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(RoyalNavy, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricMoped,
                        contentDescription = "Bansi Fleet",
                        tint = BansiGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "BansiGo",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavy
                        )
                        Box(
                            modifier = Modifier
                                .background(BansiGreenSubtle, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "FLEET",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BansiGreen
                            )
                        }
                    }
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            // Right side: Refresh, Server switcher & Availability Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onRefresh != null) {
                    IconButton(
                        onClick = onRefresh,
                        enabled = !isRefreshing,
                        modifier = Modifier.size(36.dp)
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = RoyalNavy,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh App",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                if (onOpenServerConfig != null) {
                    IconButton(
                        onClick = onOpenServerConfig,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = "Server Config",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                // Quick Online/Offline Switcher
                val isOnline = riderStatus == RiderStatus.AVAILABLE || riderStatus == RiderStatus.BUSY
                val isApproved = approvalStatus == ApprovalStatus.APPROVED

                Row(
                    modifier = Modifier
                        .background(
                            if (isOnline) BansiGreenLight else InputBackground,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                when {
                                    riderStatus == RiderStatus.BUSY -> StatusBusy
                                    riderStatus == RiderStatus.AVAILABLE -> StatusAvailable
                                    else -> StatusOffline
                                },
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOnline) "ONLINE" else "OFFLINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOnline) BansiGreenDark else TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = isOnline,
                        onCheckedChange = { checked ->
                            if (isApproved) {
                                onToggleAvailability(checked)
                            }
                        },
                        enabled = isApproved && riderStatus != RiderStatus.BUSY,
                        modifier = Modifier
                            .height(24.dp)
                            .padding(0.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SurfaceLight,
                            checkedTrackColor = BansiGreenDark,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = BorderLight
                        )
                    )
                }
            }
        }
        HorizontalDivider(color = BorderLight, thickness = 1.dp)
    }
}
