// app/src/main/java/com/project/bansigo_rider/ui/screens/wallet/sections/WalletBalanceSection.kt
package com.project.bansigo_rider.ui.screens.wallet.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ElectricMoped
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.config.AppConfig
import com.project.bansigo_rider.core.models.WalletSummary
import com.project.bansigo_rider.ui.components.BansiSuccessButton
import com.project.bansigo_rider.ui.theme.*

@Composable
fun WalletBalanceSection(
    wallet: WalletSummary,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = RoyalNavyDark,
        shadowElevation = 3.dp,
        modifier = modifier.fillMaxWidth()
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
            // Top Row: Title & 70% Share Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Available Balance",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Bansi Fleet Migori",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    }
                }

                // 70% Share Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BansiGreen.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, BansiGreen.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "${(AppConfig.RIDER_FEE_SHARE * 100).toInt()}% Fee Share",
                        color = BansiGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Balance Amount
            Text(
                text = "KES ${String.format("%,.2f", wallet.availableBalance)}",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Stats Row: Evenly distributed with weight(1f) to prevent squeeze
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatPill(
                    label = "Today",
                    value = "KES ${wallet.todayEarnings.toInt()}",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                )

                StatPill(
                    label = "This Week",
                    value = "KES ${wallet.weeklyEarnings.toInt().coerceAtLeast(wallet.totalEarnings.toInt())}",
                    icon = Icons.Default.Speed,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                )

                StatPill(
                    label = "Trips",
                    value = "${wallet.completedTripsCount} Done",
                    icon = Icons.Default.ElectricMoped,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Withdraw Button
            BansiSuccessButton(
                text = "Withdraw to M-Pesa",
                icon = Icons.Default.PhoneAndroid,
                showArrow = true,
                onClick = onWithdrawClick,
                enabled = wallet.availableBalance >= 50.0
            )

            if (wallet.availableBalance < 50.0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Minimum M-Pesa withdrawal threshold is KES 50.00",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BansiGreen,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
