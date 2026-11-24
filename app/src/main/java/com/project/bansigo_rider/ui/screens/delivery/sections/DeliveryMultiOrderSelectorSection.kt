// app/src/main/java/com/project/bansigo_rider/ui/screens/delivery/sections/DeliveryMultiOrderSelectorSection.kt
package com.project.bansigo_rider.ui.screens.delivery.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricMoped
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.project.bansigo_rider.core.models.DeliveryStatus
import com.project.bansigo_rider.core.models.DeliverySummaryItem
import com.project.bansigo_rider.ui.theme.*

@Composable
fun DeliveryMultiOrderSelectorSection(
    activeDeliveries: List<DeliverySummaryItem>,
    selectedDeliveryId: Int,
    onSelectDelivery: (Int) -> Unit,
    onBatchPickupAll: (() -> Unit)? = null,
    isBatchPickupLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (activeDeliveries.isEmpty()) return

    val scrollState = rememberScrollState()
    val acceptedCount = activeDeliveries.count { it.status == DeliveryStatus.ACCEPTED }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = RoyalNavy,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Active Deliveries",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RoyalNavyLight
                ) {
                    Text(
                        text = "${activeDeliveries.size} Orders",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Total batch earnings preview
            val totalBatchFees = activeDeliveries.sumOf { it.riderFee }
            Text(
                text = "Total: KES ${totalBatchFees.toInt()}",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BansiGreenDark
            )
        }

        // Horizontal Order Chips Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            activeDeliveries.forEachIndexed { index, item ->
                val isSelected = item.id == selectedDeliveryId
                OrderChipCard(
                    item = item,
                    index = index + 1,
                    isSelected = isSelected,
                    onClick = { onSelectDelivery(item.id) }
                )
            }
        }

        // Batch Action: If rider has 2 or more accepted orders waiting at the supermarket dock
        if (acceptedCount >= 2 && onBatchPickupAll != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = BansiGreenSubtle,
                border = BorderStroke(1.dp, BansiGreen.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            tint = BansiGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Dock Batch Pickup Available",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "$acceptedCount accepted orders at supermarket bay",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = onBatchPickupAll,
                        enabled = !isBatchPickupLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BansiGreen,
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        if (isBatchPickupLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Pick Up All",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderChipCard(
    item: DeliverySummaryItem,
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) RoyalNavy else BorderLight
    val containerColor = if (isSelected) SurfaceCard else Color(0xFFF9FAFB)

    Surface(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        shadowElevation = if (isSelected) 3.dp else 1.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#$index • Order #${item.orderId}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) RoyalNavy else TextPrimary
                )
                Text(
                    text = "KES ${item.riderFee.toInt()}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BansiGreenDark
                )
            }

            // Status Pill
            val (statusBg, statusFg) = when (item.status) {
                DeliveryStatus.ASSIGNED -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
                DeliveryStatus.ACCEPTED -> Pair(RoyalNavyLight, RoyalNavy)
                DeliveryStatus.PICKED_UP -> Pair(BansiGreenSubtle, BansiGreenDark)
                DeliveryStatus.ARRIVED -> Pair(Color(0xFFEDE9FE), Color(0xFF6D28D9))
                DeliveryStatus.DELIVERED -> Pair(BansiGreenSubtle, BansiGreen)
                else -> Pair(BorderLight, TextSecondary)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusBg
                ) {
                    Text(
                        text = item.status.label,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusFg,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            // Destination preview
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = item.destinationAddress,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
