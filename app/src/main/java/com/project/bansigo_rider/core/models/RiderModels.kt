// app/src/main/java/com/project/bansigo_rider/core/models/RiderModels.kt
package com.project.bansigo_rider.core.models

enum class ApprovalStatus(val label: String) {
    PENDING("Vetting Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    SUSPENDED("Suspended")
}

enum class RiderStatus(val label: String) {
    OFFLINE("Offline"),
    AVAILABLE("Online / Available"),
    BUSY("Busy on Delivery")
}

enum class VehicleType(val label: String) {
    BICYCLE("Bicycle"),
    MOTORCYCLE("Motorcycle (Boda Boda)"),
    TUKTUK("Tuk-Tuk"),
    CAR("Car")
}

data class RiderProfile(
    val id: Int,
    val userId: Int,
    val approvalStatus: ApprovalStatus,
    val status: RiderStatus,
    val vehicleType: VehicleType,
    val vehiclePlate: String?,
    val vehicleDescription: String?,
    val vehicleProofUrl: String? = null,
    val nationalIdNumber: String? = null,
    val idCardFrontUrl: String? = null,
    val idCardBackUrl: String? = null,
    val licenceNumber: String? = null,
    val licenceExpiry: String?,
    val drivingLicenceUrl: String? = null,
    val ratingAverage: Double?,
    val ratingCount: Int = 0,
    val deliveriesCompleted: Int = 0,
    val lastSeenAt: String? = null
)

data class RiderApplyRequest(
    val nationalIdNumber: String,
    val idCardFrontUrl: String? = null,
    val idCardBackUrl: String? = null,
    val vehicleType: VehicleType = VehicleType.MOTORCYCLE,
    val vehiclePlate: String? = null,
    val vehicleDescription: String? = null,
    val vehicleProofUrl: String? = null,
    val licenceNumber: String? = null,
    val licenceExpiry: String? = null,
    val drivingLicenceUrl: String? = null
)
