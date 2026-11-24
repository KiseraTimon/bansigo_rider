// app/src/main/java/com/project/bansigo_rider/ui/screens/wallet/sections/WalletHistorySection.kt
package com.project.bansigo_rider.ui.screens.wallet.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.TransactionFilter
import com.project.bansigo_rider.core.models.TransactionRecord
import com.project.bansigo_rider.core.models.TransactionType
import com.project.bansigo_rider.ui.theme.*

@Composable
fun WalletHistorySection(
    records: List<TransactionRecord>,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(TransactionFilter.ALL) }
    val filterScrollState = rememberScrollState()

    val filteredRecords = remember(records, selectedFilter) {
        when (selectedFilter) {
            TransactionFilter.ALL -> records
            TransactionFilter.EARNINGS -> records.filter { it.type == TransactionType.DELIVERY_EARNING }
            TransactionFilter.PAYOUTS -> records.filter { it.type == TransactionType.PAYOUT_WITHDRAWAL }
            TransactionFilter.COD -> records.filter {
                it.type == TransactionType.COD_COLLECTED || it.type == TransactionType.STORE_SETTLEMENT
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header
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
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(RoyalNavyLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = RoyalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "Transaction Ledger",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = InputBackground,
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Text(
                    text = "${filteredRecords.size} Records",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(filterScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TransactionFilter.values().forEach { filter ->
                val isSelected = filter == selectedFilter
                val count = when (filter) {
                    TransactionFilter.ALL -> records.size
                    TransactionFilter.EARNINGS -> records.count { it.type == TransactionType.DELIVERY_EARNING }
                    TransactionFilter.PAYOUTS -> records.count { it.type == TransactionType.PAYOUT_WITHDRAWAL }
                    TransactionFilter.COD -> records.count {
                        it.type == TransactionType.COD_COLLECTED || it.type == TransactionType.STORE_SETTLEMENT
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) RoyalNavy else SurfaceCard,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) RoyalNavy else BorderLight
                    ),
                    shadowElevation = if (isSelected) 1.dp else 0.dp,
                    modifier = Modifier.clickable { selectedFilter = filter }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = filter.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White.copy(alpha = 0.25f) else InputBackground)
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = count.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Transaction Item Cards List (Individual card-tiles preventing horizontal squeeze)
        if (filteredRecords.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceCard,
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "No Transactions Found",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "No records match the '${selectedFilter.label}' filter.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filteredRecords.forEach { record ->
                    TransactionCardTile(record = record)
                }
            }
        }
    }
}

@Composable
private fun TransactionCardTile(record: TransactionRecord) {
    val config = getTransactionDisplayConfig(record.type)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side: Type Icon + Description & Metadata
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Type Icon Badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(config.iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = config.icon,
                        contentDescription = null,
                        tint = config.iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Middle Info Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Description (truncated safely if long)
                    Text(
                        text = record.description,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Sub-row: Timestamp & Reference / Distance badges
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = record.timestamp,
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        record.referenceCode?.let { ref ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = InputBackground
                            ) {
                                Text(
                                    text = ref,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    maxLines = 1
                                )
                            }
                        }

                        if (record.distanceKm != null && record.distanceKm > 0) {
                            Text(
                                text = "• ${String.format("%.1f", record.distanceKm)} km",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right Side: Amount & Status Badge (Guaranteed safe intrinsic width, never squeezed)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val prefix = if (config.isPositive) "+" else "-"
                Text(
                    text = "$prefix KES ${String.format("%,.0f", record.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (config.isPositive) BansiGreenDark else BansiCrimson
                )

                // Clean Status Pill Badge
                val (statusBg, statusFg) = when (record.status.lowercase()) {
                    "completed" -> Pair(BansiGreenSubtle, BansiGreenDark)
                    "settled" -> Pair(Color(0xFFEDE9FE), Color(0xFF6D28D9))
                    "remitted" -> Pair(RoyalNavyLight, RoyalNavy)
                    else -> Pair(InputBackground, TextSecondary)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBg
                ) {
                    Text(
                        text = record.status,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusFg,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private data class TransactionDisplayConfig(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val isPositive: Boolean
)

private fun getTransactionDisplayConfig(type: TransactionType): TransactionDisplayConfig {
    return when (type) {
        TransactionType.DELIVERY_EARNING -> TransactionDisplayConfig(
            icon = Icons.Default.ArrowDownward,
            iconBg = BansiGreenSubtle,
            iconTint = BansiGreenDark,
            isPositive = true
        )
        TransactionType.PAYOUT_WITHDRAWAL -> TransactionDisplayConfig(
            icon = Icons.Default.ArrowUpward,
            iconBg = Color(0xFFFFEBEE),
            iconTint = BansiCrimson,
            isPositive = false
        )
        TransactionType.COD_COLLECTED -> TransactionDisplayConfig(
            icon = Icons.Default.Payments,
            iconBg = Color(0xFFFEF3C7),
            iconTint = Color(0xFFD97706),
            isPositive = true
        )
        TransactionType.STORE_SETTLEMENT -> TransactionDisplayConfig(
            icon = Icons.Default.Receipt,
            iconBg = RoyalNavyLight,
            iconTint = RoyalNavy,
            isPositive = false
        )
    }
}
