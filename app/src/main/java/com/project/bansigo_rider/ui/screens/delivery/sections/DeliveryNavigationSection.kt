// app/src/main/java/com/project/bansigo_rider/ui/screens/delivery/sections/DeliveryNavigationSection.kt
package com.project.bansigo_rider.ui.screens.delivery.sections

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
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Straight
import androidx.compose.material.icons.filled.TurnLeft
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material.icons.filled.TurnSharpLeft
import androidx.compose.material.icons.filled.TurnSharpRight
import androidx.compose.material.icons.filled.TurnSlightLeft
import androidx.compose.material.icons.filled.TurnSlightRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.NavigationStep
import com.project.bansigo_rider.core.models.RiderNavigation
import com.project.bansigo_rider.ui.theme.*

@Composable
fun DeliveryNavigationSection(
    navigation: RiderNavigation?,
    modifier: Modifier = Modifier
) {
    if (navigation == null || navigation.steps.isEmpty()) return

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        tint = RoyalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Turn-by-Turn Guidance",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                }

                Text(
                    text = "${navigation.estimatedMinutes} min • ${if (navigation.distanceM >= 1000) String.format("%.1f km", navigation.distanceM / 1000.0) else "${navigation.distanceM} m"}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BansiGreenDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            navigation.steps.take(4).forEachIndexed { index, step ->
                NavigationStepRow(step = step, isFirst = index == 0)
                if (index < 3 && index < navigation.steps.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun NavigationStepRow(step: NavigationStep, isFirst: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = if (isFirst) RoyalNavyLight else InputBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = getManeuverIcon(step.type)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(if (isFirst) RoyalNavy else BorderLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isFirst) SurfaceLight else TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = step.instruction,
                    fontSize = 12.sp,
                    fontWeight = if (isFirst) FontWeight.Bold else FontWeight.Medium,
                    color = if (isFirst) RoyalNavyDark else TextPrimary,
                    maxLines = 1
                )
                if (step.streetName.isNotBlank()) {
                    Text(
                        text = step.streetName,
                        fontSize = 10.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            Text(
                text = "${step.distanceM}m",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isFirst) RoyalNavy else TextSecondary
            )
        }
    }
}

private fun getManeuverIcon(type: String): ImageVector {
    return when (type.lowercase()) {
        "turn_left", "sharp_left" -> Icons.Default.TurnLeft
        "turn_right", "sharp_right" -> Icons.Default.TurnRight
        "slight_left" -> Icons.Default.TurnSlightLeft
        "slight_right" -> Icons.Default.TurnSlightRight
        else -> Icons.Default.Straight
    }
}
