// app/src/main/java/com/project/bansigo_rider/core/models/WalletModels.kt
package com.project.bansigo_rider.core.models

data class WalletSummary(
    val totalEarnings: Double,
    val availableBalance: Double,
    val codCashInHand: Double, // Cash-on-delivery collected from customers that must be remitted
    val todayEarnings: Double,
    val weeklyEarnings: Double = 0.0,
    val completedTripsCount: Int,
    val pendingPayouts: Double = 0.0,
    val averageTripFee: Double = 0.0,
    val totalDistanceKm: Double = 0.0
)

data class RiderWalletResponse(
    val summary: WalletSummary,
    val transactions: List<TransactionRecord>
)

data class WithdrawResult(
    val record: TransactionRecord,
    val availableBalance: Double
)

data class TransactionRecord(
    val id: String,
    val orderNumber: String,
    val amount: Double,
    val type: TransactionType,
    val description: String,
    val timestamp: String,
    val distanceKm: Double? = null,
    val status: String = "Completed",
    val referenceCode: String? = null
)

enum class TransactionType {
    DELIVERY_EARNING,
    COD_COLLECTED,
    PAYOUT_WITHDRAWAL,
    STORE_SETTLEMENT
}

enum class TransactionFilter(val label: String) {
    ALL("All"),
    EARNINGS("Earnings"),
    PAYOUTS("Payouts"),
    COD("COD Cash")
}
