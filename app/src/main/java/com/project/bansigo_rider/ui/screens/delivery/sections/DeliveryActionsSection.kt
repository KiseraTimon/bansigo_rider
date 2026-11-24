// app/src/main/java/com/project/bansigo_rider/ui/screens/delivery/sections/DeliveryActionsSection.kt
package com.project.bansigo_rider.ui.screens.delivery.sections

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WhereToVote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.DeliveryDetail
import com.project.bansigo_rider.core.models.DeliveryStatus
import com.project.bansigo_rider.ui.components.BansiOutlineButton
import com.project.bansigo_rider.ui.components.BansiPrimaryButton
import com.project.bansigo_rider.ui.components.BansiSuccessButton
import com.project.bansigo_rider.ui.components.BansiTextField
import com.project.bansigo_rider.ui.theme.*

@Composable
fun DeliveryActionsSection(
    delivery: DeliveryDetail,
    isActionLoading: Boolean,
    onPickup: () -> Unit,
    onArrive: () -> Unit,
    onComplete: (handoverCode: String, idChecked: Boolean, cashCollected: Double?) -> Unit,
    onFail: (reason: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFailDialog by remember { mutableStateOf(false) }
    var failReason by remember { mutableStateOf("") }

    // Handover completion states
    var handoverCode by remember { mutableStateOf("") }
    var idChecked by remember { mutableStateOf(!delivery.requiresIdCheck) }
    var cashCollectedConfirmed by remember { mutableStateOf(delivery.cashToCollect == null) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Mission Control Actions",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = RoyalNavy
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (delivery.status) {
                DeliveryStatus.ACCEPTED, DeliveryStatus.ASSIGNED, DeliveryStatus.PENDING -> {
                    Text(
                        text = "Head to Bansi Mega Supermarket dispatch dock. Confirm order packing before departing.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BansiPrimaryButton(
                        text = "Confirm Parcel Picked Up",
                        icon = Icons.Default.ShoppingBag,
                        onClick = onPickup,
                        isLoading = isActionLoading
                    )
                }

                DeliveryStatus.PICKED_UP -> {
                    Text(
                        text = "Parcel in transit to customer. Follow OpenStreetMap route instructions. Mark arrived when at destination.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BansiPrimaryButton(
                        text = "Mark Arrived at Destination",
                        icon = Icons.Default.WhereToVote,
                        onClick = onArrive,
                        isLoading = isActionLoading,
                        backgroundColor = RoyalNavy
                    )
                }

                DeliveryStatus.ARRIVED -> {
                    Column {
                        Text(
                            text = "Customer Handover Verification",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavyDark
                        )
                        Text(
                            text = "Request the 4-digit PIN code displayed on the customer's BansiGo app screen to unlock parcel handover.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        BansiTextField(
                            value = handoverCode,
                            onValueChange = { if (it.length <= 8) handoverCode = it },
                            label = "Customer Handover PIN",
                            placeholder = "4-digit code (e.g. 4821)",
                            leadingIcon = Icons.Default.Pin,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        if (delivery.requiresIdCheck) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = idChecked,
                                    onCheckedChange = { idChecked = it },
                                    colors = CheckboxDefaults.colors(checkedColor = BansiCrimson)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "I verified customer National ID (Age 18+ items confirmed)",
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            }
                        }

                        if (delivery.cashToCollect != null && delivery.cashToCollect > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = cashCollectedConfirmed,
                                    onCheckedChange = { cashCollectedConfirmed = it },
                                    colors = CheckboxDefaults.colors(checkedColor = BansiGreenDark)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "I collected KES ${delivery.cashToCollect.toInt()} in cash from customer",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val canComplete = handoverCode.length in 4..8 &&
                                (!delivery.requiresIdCheck || idChecked) &&
                                (delivery.cashToCollect == null || cashCollectedConfirmed)

                        BansiSuccessButton(
                            text = "Verify PIN & Complete Delivery",
                            icon = Icons.Default.CheckCircle,
                            enabled = canComplete,
                            isLoading = isActionLoading,
                            onClick = {
                                onComplete(
                                    handoverCode.trim(),
                                    idChecked,
                                    if (delivery.cashToCollect != null && cashCollectedConfirmed) delivery.cashToCollect else null
                                )
                            }
                        )
                    }
                }

                DeliveryStatus.DELIVERED -> {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BansiGreenSubtle,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BansiGreenDark,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Mission Completed Successfully",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BansiGreenDark
                                )
                                Text(
                                    text = "KES ${delivery.riderFee.toInt()} added to your wallet balance.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                DeliveryStatus.FAILED, DeliveryStatus.CANCELLED -> {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFEBEE),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = BansiCrimson,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Delivery ${delivery.status.name}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BansiCrimsonDark
                                )
                                Text(
                                    text = "Returned to supermarket dispatch dock inventory.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Report problem button (available during active states)
            if (delivery.status in listOf(DeliveryStatus.ACCEPTED, DeliveryStatus.PICKED_UP, DeliveryStatus.ARRIVED)) {
                Spacer(modifier = Modifier.height(14.dp))
                BansiOutlineButton(
                    text = "Report Problem / Delivery Failed",
                    icon = Icons.Default.ReportProblem,
                    color = BansiCrimson,
                    onClick = { showFailDialog = true }
                )
            }
        }
    }

    // Modal dialog to report failure to bansigo_server
    if (showFailDialog) {
        val predefinedReasons = listOf(
            "Customer unreachable via phone",
            "Incorrect handover PIN code",
            "Customer refused delivery",
            "Wrong delivery address",
            "Cargo / merchandise damaged",
            "Supermarket dock pack error"
        )

        AlertDialog(
            onDismissRequest = { showFailDialog = false },
            title = {
                Text(
                    text = "Report Delivery Problem",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BansiCrimsonDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Select failure reason to notify dispatch operations:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    predefinedReasons.forEach { reason ->
                        val isSelected = failReason == reason
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { failReason = reason },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) BansiCrimson.copy(alpha = 0.1f) else InputBackground,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) BansiCrimson else BorderLight
                            )
                        ) {
                            Text(
                                text = reason,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) BansiCrimsonDark else TextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (failReason.isNotBlank()) {
                            showFailDialog = false
                            onFail(failReason)
                        }
                    },
                    enabled = failReason.isNotBlank()
                ) {
                    Text("Submit Report", color = BansiCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFailDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
