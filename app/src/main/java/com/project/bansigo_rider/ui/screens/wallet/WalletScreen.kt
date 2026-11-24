// app/src/main/java/com/project/bansigo_rider/ui/screens/wallet/WalletScreen.kt
package com.project.bansigo_rider.ui.screens.wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.DeliveryStatus
import com.project.bansigo_rider.core.models.TransactionRecord
import com.project.bansigo_rider.core.models.TransactionType
import com.project.bansigo_rider.core.models.WalletSummary
import com.project.bansigo_rider.core.network.ApiClient
import com.project.bansigo_rider.core.storage.SessionManager
import com.project.bansigo_rider.ui.screens.wallet.sections.WalletAnalyticsSection
import com.project.bansigo_rider.ui.screens.wallet.sections.WalletBalanceSection
import com.project.bansigo_rider.ui.screens.wallet.sections.WalletCodSection
import com.project.bansigo_rider.ui.screens.wallet.sections.WalletHistorySection
import com.project.bansigo_rider.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun WalletScreen(
    sessionManager: SessionManager,
    apiClient: ApiClient,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showWithdrawDialog by remember { mutableStateOf(false) }
    var isWithdrawing by remember { mutableStateOf(false) }
    var withdrawAmountText by remember { mutableStateOf("") }
    var withdrawError by remember { mutableStateOf<String?>(null) }

    var walletSummary by remember {
        mutableStateOf(
            WalletSummary(
                totalEarnings = 2450.0,
                availableBalance = 1680.0,
                codCashInHand = 0.0,
                todayEarnings = 210.0,
                weeklyEarnings = 2450.0,
                completedTripsCount = 12,
                pendingPayouts = 0.0,
                averageTripFee = 70.0,
                totalDistanceKm = 34.6
            )
        )
    }

    var transactionHistory by remember {
        mutableStateOf(
            listOf(
                TransactionRecord(
                    id = "tx-1",
                    orderNumber = "#1048",
                    amount = 70.0,
                    type = TransactionType.DELIVERY_EARNING,
                    description = "Delivery Fee: Order #1048",
                    timestamp = "Today, 11:30 AM",
                    distanceKm = 2.4,
                    status = "Completed",
                    referenceCode = "EARN-1048"
                ),
                TransactionRecord(
                    id = "tx-2",
                    orderNumber = "#1045",
                    amount = 70.0,
                    type = TransactionType.DELIVERY_EARNING,
                    description = "Delivery Fee: Order #1045",
                    timestamp = "Today, 09:45 AM",
                    distanceKm = 1.8,
                    status = "Completed",
                    referenceCode = "EARN-1045"
                ),
                TransactionRecord(
                    id = "tx-3",
                    orderNumber = "MPESA-B2C",
                    amount = 500.0,
                    type = TransactionType.PAYOUT_WITHDRAWAL,
                    description = "M-Pesa Payout: 07** *** 744",
                    timestamp = "Yesterday, 06:15 PM",
                    status = "Settled",
                    referenceCode = "QHK92841"
                ),
                TransactionRecord(
                    id = "tx-4",
                    orderNumber = "#1031",
                    amount = 70.0,
                    type = TransactionType.DELIVERY_EARNING,
                    description = "Delivery Fee: Order #1031",
                    timestamp = "Yesterday, 04:20 PM",
                    distanceKm = 3.2,
                    status = "Completed",
                    referenceCode = "EARN-1031"
                ),
                TransactionRecord(
                    id = "tx-5",
                    orderNumber = "#1029",
                    amount = 350.0,
                    type = TransactionType.STORE_SETTLEMENT,
                    description = "Accounts Desk Cash Remit",
                    timestamp = "Yesterday, 03:00 PM",
                    status = "Remitted",
                    referenceCode = "REMIT-842"
                )
            )
        )
    }

    fun refreshWallet() {
        scope.launch {
            val res = apiClient.getActiveDeliveries()
            if (res.isSuccess) {
                val list = res.getOrNull().orEmpty()
                val delivered = list.filter { it.status == DeliveryStatus.DELIVERED }
                if (delivered.isNotEmpty()) {
                    val earningsToday = delivered.sumOf { it.riderFee }
                    val currentBalance = walletSummary.availableBalance.coerceAtLeast(earningsToday)
                    walletSummary = walletSummary.copy(
                        completedTripsCount = walletSummary.completedTripsCount.coerceAtLeast(delivered.size),
                        todayEarnings = earningsToday,
                        availableBalance = currentBalance
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshWallet()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
        ) {
            // Header (with 28.dp top margin ensuring zero bezel clipping in fullscreen mode)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rider Wallet & Payouts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalNavy
                )

                IconButton(onClick = { refreshWallet() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Wallet",
                        tint = TextSecondary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Hero Available Balance & Instant M-Pesa Payout
                WalletBalanceSection(
                    wallet = walletSummary,
                    onWithdrawClick = {
                        withdrawAmountText = walletSummary.availableBalance.toInt().toString()
                        withdrawError = null
                        showWithdrawDialog = true
                    }
                )

                // Section 2: Cash-on-Delivery (COD) Remittance Tracker
                WalletCodSection(codCashInHand = walletSummary.codCashInHand)

                // Section 3: Fleet Earnings & Performance Analytics
                WalletAnalyticsSection(wallet = walletSummary)

                // Section 4: Filterable Transactions & Ledger History
                WalletHistorySection(records = transactionHistory)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Interactive M-Pesa Payout Modal
    if (showWithdrawDialog) {
        val phoneNumber = "254700226744"
        val maxAvailable = walletSummary.availableBalance

        AlertDialog(
            onDismissRequest = { if (!isWithdrawing) showWithdrawDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(BansiGreenSubtle, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = BansiGreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "M-Pesa Payout",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Direct B2C disbursement to your registered Safaricom phone number.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    // Phone Number preview
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = InputBackground,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Destination Account", fontSize = 11.sp, color = TextMuted)
                            Text(text = "+$phoneNumber", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    // Amount input field
                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) {
                                withdrawAmountText = input
                                withdrawError = null
                            }
                        },
                        label = { Text("Withdrawal Amount (KES)") },
                        placeholder = { Text("e.g. 500") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = withdrawError != null,
                        supportingText = withdrawError?.let { { Text(it, color = BansiCrimson) } },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BansiGreen,
                            unfocusedBorderColor = BorderLight
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Quick Preset Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(200, 500, 1000).forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = InputBackground,
                                border = BorderStroke(1.dp, BorderLight),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        withdrawAmountText = preset.toString()
                                        withdrawError = null
                                    }
                            ) {
                                Text(
                                    text = "KES $preset",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = RoyalNavy,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        // Max Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BansiGreenSubtle,
                            border = BorderStroke(1.dp, BansiGreen.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    withdrawAmountText = maxAvailable.toInt().toString()
                                    withdrawError = null
                                }
                        ) {
                            Text(
                                text = "All",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BansiGreenDark,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    // Zero Fee Tag
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0FDF4),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BansiGreenDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "0% B2C fee • Free payout for Bansi Fleet Riders",
                                fontSize = 10.sp,
                                color = BansiGreenDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = withdrawAmountText.toDoubleOrNull()
                        when {
                            amount == null || amount <= 0.0 -> {
                                withdrawError = "Enter a valid amount"
                            }
                            amount < 50.0 -> {
                                withdrawError = "Minimum withdrawal is KES 50.00"
                            }
                            amount > maxAvailable -> {
                                withdrawError = "Insufficient balance (Max: KES ${maxAvailable.toInt()})"
                            }
                            else -> {
                                isWithdrawing = true
                                scope.launch {
                                    kotlinx.coroutines.delay(1000) // Realistic network simulation
                                    val newBalance = (maxAvailable - amount).coerceAtLeast(0.0)
                                    walletSummary = walletSummary.copy(availableBalance = newBalance)

                                    val mpesaRef = "QK" + Random.nextInt(100000, 999999)
                                    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())

                                    val newRecord = TransactionRecord(
                                        id = "tx-${System.currentTimeMillis()}",
                                        orderNumber = "MPESA-B2C",
                                        amount = amount,
                                        type = TransactionType.PAYOUT_WITHDRAWAL,
                                        description = "M-Pesa Payout: +$phoneNumber",
                                        timestamp = "Today, $timeStr",
                                        status = "Settled",
                                        referenceCode = mpesaRef
                                    )

                                    transactionHistory = listOf(newRecord) + transactionHistory
                                    isWithdrawing = false
                                    showWithdrawDialog = false
                                    snackbarHostState.showSnackbar("KES ${amount.toInt()} sent to M-Pesa ($mpesaRef) successfully!")
                                }
                            }
                        }
                    },
                    enabled = !isWithdrawing,
                    colors = ButtonDefaults.buttonColors(containerColor = BansiGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isWithdrawing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Confirm Payout", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showWithdrawDialog = false },
                    enabled = !isWithdrawing
                ) {
                    Text(text = "Cancel", color = TextSecondary)
                }
            }
        )
    }
}
