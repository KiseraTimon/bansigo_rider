// app/src/main/java/com/project/bansigo_rider/ui/screens/home/sections/HomeLiveRadarSection.kt
package com.project.bansigo_rider.ui.screens.home.sections

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.DeliveryStatus
import com.project.bansigo_rider.core.models.DeliverySummaryItem
import com.project.bansigo_rider.ui.theme.BansiCrimson
import com.project.bansigo_rider.ui.theme.BansiCrimsonLight
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
fun HomeLiveRadarSection(
    activeDelivery: DeliverySummaryItem?,
    isResponding: Boolean,
    isOnline: Boolean,
    onAcceptOffer: (Int) -> Unit,
    onDeclineOffer: (deliveryId: Int, reasonCode: String?, reasonText: String?) -> Unit,
    onOpenDeliveryMission: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var pendingDeclineId by remember { mutableStateOf<Int?>(null) }

    if (pendingDeclineId != null) {
        DeclineReasonDialog(
            onDismiss = { pendingDeclineId = null },
            onConfirmDecline = { code, text ->
                val targetId = pendingDeclineId!!
                pendingDeclineId = null
                onDeclineOffer(targetId, code, text)
            }
        )
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isOnline) BansiGreenSubtle else RoyalNavyLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = "Radar",
                            tint = if (isOnline) BansiGreen else RoyalNavy,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "Live Dispatch Radar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                if (isOnline) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BansiGreenSubtle
                    ) {
                        Text(
                            text = "Scanning 7km",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BansiGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when {
                // Scenario A: Active incoming offer / assignment
                activeDelivery != null && activeDelivery.status == DeliveryStatus.ASSIGNED -> {
                    IncomingOfferCard(
                        delivery = activeDelivery,
                        isResponding = isResponding,
                        onAccept = { onAcceptOffer(activeDelivery.id) },
                        onDecline = { pendingDeclineId = activeDelivery.id }
                    )
                }

                // Scenario B: Active ongoing mission
                activeDelivery != null -> {
                    ActiveMissionCard(
                        delivery = activeDelivery,
                        onOpenMission = { onOpenDeliveryMission(activeDelivery.id) }
                    )
                }

                // Scenario C: Idle & Online -> Animated Radar Sweep
                isOnline -> {
                    IdleRadarSweep()
                }

                // Scenario D: Offline
                else -> {
                    OfflinePrompt()
                }
            }
        }
    }
}

@Composable
private fun DeclineReasonDialog(
    onDismiss: () -> Unit,
    onConfirmDecline: (reasonCode: String?, reasonText: String?) -> Unit
) {
    val reasons = listOf(
        "vehicle_breakdown" to "Vehicle breakdown / puncture",
        "medical_emergency" to "Medical or personal emergency",
        "unsafe_area" to "Route / weather safety hazard",
        "over_capacity" to "Cargo box at full capacity",
        "shift_ending" to "Shift ending / battery low",
        "other" to "Other justified reason",
        "" to "Decline without reason (incurs penalty)"
    )
    var selectedCode by remember { mutableStateOf("vehicle_breakdown") }
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = BansiCrimson,
        textContentColor = TextPrimary,
        title = {
            Text(
                text = "Decline Delivery Offer",
                fontWeight = FontWeight.Bold,
                color = BansiCrimson
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Rejecting offers while online without a valid cause adds penalty points and may trigger a dispatch cooldown.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                reasons.forEach { (code, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCode = code }
                            .padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = selectedCode == code,
                            onClick = { selectedCode = code }
                        )
                        Text(text = label, fontSize = 12.5.sp, color = TextPrimary)
                    }
                }
                if (selectedCode == "other" || selectedCode.isNotEmpty()) {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Details (required for 'Other')") },
                        singleLine = false,
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmDecline(
                        selectedCode.ifBlank { null },
                        noteText.trim().ifBlank { null }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = BansiCrimson)
            ) {
                Text("Confirm Decline")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Keep Offer")
            }
        }
    )
}

@Composable
private fun IncomingOfferCard(
    delivery: DeliverySummaryItem,
    isResponding: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BansiGreenSubtle)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "INCOMING DISPATCH OFFER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BansiGreen
                )
                Text(
                    text = "Order #${delivery.orderId}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Text(
                text = "KES ${String.format("%.0f", delivery.riderFee)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BansiGreen
            )
        }

        // Drop-off address
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Drop-off",
                tint = RoyalNavy,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = delivery.destinationAddress.ifBlank { "Migori Town Center" },
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onDecline,
                enabled = !isResponding,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BansiCrimson
                ),
                border = BorderStroke(1.dp, BansiCrimson.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Decline",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Decline", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onAccept,
                enabled = !isResponding,
                modifier = Modifier
                    .weight(1.5f)
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BansiGreen,
                    contentColor = Color.White
                )
            ) {
                if (isResponding) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Accept Offer", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Accept",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveMissionCard(
    delivery: DeliverySummaryItem,
    onOpenMission: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenMission() },
        shape = RoundedCornerShape(14.dp),
        color = RoyalNavyLight,
        border = BorderStroke(1.dp, RoyalNavy.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RoyalNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Active Run",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "ONGOING MISSION • ${delivery.status.name}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RoyalNavy
                    )
                    Text(
                        text = "Order #${delivery.orderId}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Tap to open navigation & route guidance",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = RoyalNavy,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun IdleRadarSweep() {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_waves")
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )
    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(90.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(90.dp)) {
                drawCircle(
                    color = BansiGreen.copy(alpha = (1f - wave1).coerceIn(0f, 0.4f)),
                    radius = (size.minDimension / 2) * wave1,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawCircle(
                    color = BansiGreen.copy(alpha = (1f - wave2).coerceIn(0f, 0.4f)),
                    radius = (size.minDimension / 2) * wave2,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BansiGreenSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Radar,
                    contentDescription = "Radar Active",
                    tint = BansiGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Connected to Bansi Supermarket Dispatch",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Text(
            text = "Awaiting customer delivery assignments in Migori…",
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}

@Composable
private fun OfflinePrompt() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "You are currently OFFLINE",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Switch status to ONLINE above to receive supermarket dispatch orders.",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
