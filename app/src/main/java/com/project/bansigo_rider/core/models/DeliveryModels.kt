// app/src/main/java/com/project/bansigo_rider/core/models/DeliveryModels.kt
package com.project.bansigo_rider.core.models

enum class DeliveryStatus(val label: String) {
    PENDING("Pending"),
    ASSIGNED("Assigned"),
    ACCEPTED("Accepted"),
    PICKED_UP("Picked Up"),
    ARRIVED("Arrived"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    FAILED("Failed")
}

data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val name: String = "",
    val address: String = ""
)

data class NavigationStep(
    val instruction: String,
    val distanceM: Int,
    val durationS: Int,
    val streetName: String = "",
    val type: String = "straight"
)

data class RiderNavigation(
    val deliveryId: Int,
    val leg: String, // "to_supermarket" or "to_customer"
    val destinationName: String,
    val distanceM: Int,
    val durationS: Int,
    val estimatedMinutes: Int,
    val routingAlgorithm: String,
    val steps: List<NavigationStep>,
    val waypoints: List<GeoPoint>
)

data class DeliveryDetail(
    val deliveryId: Int,
    val orderId: Int,
    val orderNumber: String,
    val status: DeliveryStatus,
    val requiresIdCheck: Boolean,
    val riderFee: Double,
    val pickupLocation: GeoPoint,
    val dropoffLocation: GeoPoint,
    val customerName: String = "Customer",
    val customerPhone: String = "",
    val customerNote: String? = null,
    val paymentMethod: String = "mpesa",
    val cashToCollect: Double? = null,
    val estimatedDistanceM: Int? = null,
    val estimatedDurationS: Int? = null,
    val routingAlgorithm: String? = null,
    val routeWaypoints: List<GeoPoint> = emptyList(),
    val itemsSummary: List<String> = emptyList()
)

data class DeliverySummaryItem(
    val id: Int,
    val orderId: Int,
    val status: DeliveryStatus,
    val riderFee: Double,
    val estimatedDistanceM: Int?,
    val estimatedDurationS: Int?,
    val destinationAddress: String = "Migori Town",
    val assignedAt: String? = null
)
