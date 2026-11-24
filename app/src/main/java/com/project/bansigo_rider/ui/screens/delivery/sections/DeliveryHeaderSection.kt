// app/src/main/java/com/project/bansigo_rider/ui/screens/delivery/sections/DeliveryHeaderSection.kt
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.DeliveryDetail
import com.project.bansigo_rider.core.models.DeliveryStatus
import com.project.bansigo_rider.ui.components.DeliveryStatusBadge
import com.project.bansigo_rider.ui.theme.*

@Composable
fun DeliveryHeaderSection(
    delivery: DeliveryDetail,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row 1: Order ID and Delivery Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = delivery.orderNumber,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavy
                        )
                        if (delivery.requiresIdCheck) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFEBEE)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "ID Check",
                                        tint = BansiCrimson,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "18+ ID",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BansiCrimson
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = "Delivery #${delivery.deliveryId} • Bansi Mega Supermarket",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                DeliveryStatusBadge(status = delivery.status)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step Timeline: Accepted -> Picked Up -> Arrived -> Delivered
            DeliveryProgressTimeline(currentStatus = delivery.status)

            Spacer(modifier = Modifier.height(14.dp))

            // Fee and Payment Summary Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = InputBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rider Guaranteed Share",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "KES ${delivery.riderFee.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BansiGreenDark
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Payment Settlement",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        val isCod = delivery.paymentMethod.equals("cash", ignoreCase = true) ||
                                delivery.paymentMethod.equals("cash_on_delivery", ignoreCase = true) ||
                                delivery.cashToCollect != null
                        Text(
                            text = if (isCod) {
                                "COD: Collect KES ${delivery.cashToCollect?.toInt() ?: 0}"
                            } else {
                                "Pre-paid via M-Pesa"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCod) Color(0xFFD97706) else BansiGreenDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliveryProgressTimeline(currentStatus: DeliveryStatus) {
    val steps = listOf(
        Pair("Accepted", DeliveryStatus.ACCEPTED),
        Pair("Picked Up", DeliveryStatus.PICKED_UP),
        Pair("Arrived", DeliveryStatus.ARRIVED),
        Pair("Delivered", DeliveryStatus.DELIVERED)
    )

    val currentStepIndex = when (currentStatus) {
        DeliveryStatus.ASSIGNED -> 0
        DeliveryStatus.ACCEPTED -> 0
        DeliveryStatus.PICKED_UP -> 1
        DeliveryStatus.ARRIVED -> 2
        DeliveryStatus.DELIVERED -> 3
        else -> 0
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, (label, status) ->
            val isCompleted = index < currentStepIndex
            val isCurrent = index == currentStepIndex

            val iconColor = when {
                isCompleted -> BansiGreen
                isCurrent -> RoyalNavy
                else -> Color(0xFFD1D5DB)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> BansiGreen
                                isCurrent -> RoyalNavy
                                else -> BorderLight
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isCurrent) Color.White else TextMuted)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCurrent) RoyalNavy else TextSecondary,
                    maxLines = 1
                )
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .weight(0.7f)
                        .background(if (index < currentStepIndex) BansiGreen else BorderLight)
                )
            }
        }
    }
}
