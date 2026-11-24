// app/src/main/java/com/project/bansigo_rider/ui/screens/home/sections/HomeQuickActionsSection.kt
package com.project.bansigo_rider.ui.screens.home.sections

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun HomeQuickActionsSection(
    onNavigateToDelivery: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToOptions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dockHotline = "+254700226744"

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Fleet Operations Shortcuts",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ShortcutCard(
                label = "Route Map",
                subtext = "Live Waypoints",
                icon = Icons.Default.Map,
                tint = RoyalNavy,
                bg = RoyalNavyLight,
                onClick = onNavigateToDelivery,
                modifier = Modifier.weight(1f)
            )

            ShortcutCard(
                label = "Earnings",
                subtext = "Wallet & Cash",
                icon = Icons.Default.AccountBalanceWallet,
                tint = BansiGreen,
                bg = BansiGreenSubtle,
                onClick = onNavigateToWallet,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ShortcutCard(
                label = "Vetting",
                subtext = "Rider Docs",
                icon = Icons.Default.Badge,
                tint = Color(0xFFF59E0B),
                bg = Color(0xFFFEF3C7),
                onClick = onNavigateToOptions,
                modifier = Modifier.weight(1f)
            )

            ShortcutCard(
                label = "Dispatch Bay",
                subtext = "Call Hotline",
                icon = Icons.Default.Call,
                tint = BansiCrimson,
                bg = Color(0xFFFFEBEE),
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$dockHotline")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ShortcutCard(
    label: String,
    subtext: String,
    icon: ImageVector,
    tint: Color,
    bg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtext,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
