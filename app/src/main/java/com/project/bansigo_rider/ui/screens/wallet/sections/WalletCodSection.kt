// app/src/main/java/com/project/bansigo_rider/ui/screens/wallet/sections/WalletCodSection.kt
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
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
import com.project.bansigo_rider.ui.theme.*

@Composable
fun WalletCodSection(
    codCashInHand: Double,
    modifier: Modifier = Modifier
) {
    val hasCashDue = codCashInHand > 0.0

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, if (hasCashDue) Color(0xFFFDE68A) else BorderLight),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (hasCashDue) Color(0xFFFEF3C7) else BansiGreenSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = if (hasCashDue) Color(0xFFD97706) else BansiGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Cash-on-Delivery (COD)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (hasCashDue) "Store remittance due" else "Zero cash due • Account clear",
                            fontSize = 11.sp,
                            color = if (hasCashDue) Color(0xFFD97706) else BansiGreenDark
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "KES ${String.format("%,.2f", codCashInHand)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (hasCashDue) Color(0xFFB45309) else BansiGreenDark
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Policy Notice Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (hasCashDue) Color(0xFFFFFBEB) else InputBackground,
                border = BorderStroke(1.dp, if (hasCashDue) Color(0xFFFDE68A) else BorderLight)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (hasCashDue) Icons.Default.Schedule else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (hasCashDue) Color(0xFFD97706) else BansiGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (hasCashDue) "Daily Remittance Policy" else "Remittance Status: Up to date",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasCashDue) Color(0xFF92400E) else TextPrimary
                        )
                    }

                    Text(
                        text = if (hasCashDue) {
                            "All physical cash collected from customers during Cash-on-Delivery drop-offs must be surrendered daily at the Bansi Mega Supermarket Accounts Cashier Desk before 8:00 PM."
                        } else {
                            "You have remitted all customer cash collected. Great job keeping your fleet accounting balance clear!"
                        },
                        fontSize = 11.sp,
                        color = if (hasCashDue) Color(0xFF92400E) else TextSecondary,
                        lineHeight = 15.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hub Accounts: Bansi Mega Supermarket, Migori",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (hasCashDue) Color(0xFFFDE68A) else BansiGreenSubtle
                        ) {
                            Text(
                                text = if (hasCashDue) "Pending Remit" else "Clear",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hasCashDue) Color(0xFF92400E) else BansiGreenDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
