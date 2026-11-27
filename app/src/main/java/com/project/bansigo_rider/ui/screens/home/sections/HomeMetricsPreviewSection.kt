// app/src/main/java/com/project/bansigo_rider/ui/screens/home/sections/HomeMetricsPreviewSection.kt
package com.project.bansigo_rider.ui.screens.home.sections

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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.config.AppConfig
import com.project.bansigo_rider.ui.theme.BansiCrimson
import com.project.bansigo_rider.ui.theme.BansiGreen
import com.project.bansigo_rider.ui.theme.BansiGreenSubtle
import com.project.bansigo_rider.ui.theme.BorderLight
import com.project.bansigo_rider.ui.theme.RoyalNavy
import com.project.bansigo_rider.ui.theme.RoyalNavyLight
import com.project.bansigo_rider.ui.theme.SurfaceCard
import com.project.bansigo_rider.ui.theme.TextMuted
import com.project.bansigo_rider.ui.theme.TextPrimary
import com.project.bansigo_rider.ui.theme.TextSecondary

@Composable
fun HomeMetricsPreviewSection(
    completedCount: Int,
    todayEarnings: Double,
    codCollected: Double,
    ratingAverage: Double?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: Completed Deliveries & Today's Earnings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "Deliveries",
                value = "$completedCount Done",
                subtext = "Bansi Fleet Migori",
                icon = Icons.Default.CheckCircleOutline,
                iconTint = RoyalNavy,
                iconBg = RoyalNavyLight,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Today's Earnings",
                value = "KES ${String.format("%.0f", todayEarnings)}",
                subtext = "${(AppConfig.RIDER_FEE_SHARE * 100).toInt()}% Fee Share",
                subtextHighlight = true,
                icon = Icons.Default.AccountBalanceWallet,
                iconTint = BansiGreen,
                iconBg = BansiGreenSubtle,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: Cash-on-Delivery & Satisfaction Rating
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "COD in Hand",
                value = "KES ${String.format("%.0f", codCollected)}",
                subtext = "Remit at Accounts Desk",
                icon = Icons.Default.Payments,
                iconTint = Color(0xFFF59E0B),
                iconBg = Color(0xFFFEF3C7),
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Fleet Rating",
                value = ratingAverage?.let { String.format("%.1f ★", it) } ?: "New",
                subtext = if (ratingAverage != null) "Customer Satisfaction" else "Awaiting reviews",
                icon = Icons.Default.Star,
                iconTint = Color(0xFFF59E0B),
                iconBg = Color(0xFFFEF3C7),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
    subtextHighlight: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtext,
                fontSize = 10.sp,
                fontWeight = if (subtextHighlight) FontWeight.Bold else FontWeight.Normal,
                color = if (subtextHighlight) BansiGreen else TextMuted
            )
        }
    }
}
