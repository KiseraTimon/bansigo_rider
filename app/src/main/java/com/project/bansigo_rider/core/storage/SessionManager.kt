// app/src/main/java/com/project/bansigo_rider/core/storage/SessionManager.kt
package com.project.bansigo_rider.core.storage

import android.content.Context
import android.content.SharedPreferences
import com.project.bansigo_rider.core.config.AppConfig
import com.project.bansigo_rider.core.models.ApprovalStatus
import com.project.bansigo_rider.core.models.RiderStatus
import com.project.bansigo_rider.core.models.VehicleType

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "bansigo_rider_session"
        private const val KEY_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"
        private const val KEY_USERNAME = "username"
        private const val KEY_PICTURE = "profile_picture"
        private const val KEY_IS_VERIFIED = "is_verified"
        private const val KEY_IS_PHONE_VERIFIED = "is_phone_verified"
        private const val KEY_RIDER_ID = "rider_id"
        private const val KEY_APPROVAL_STATUS = "approval_status"
        private const val KEY_RIDER_STATUS = "rider_status"
        private const val KEY_VEHICLE_TYPE = "vehicle_type"
        private const val KEY_VEHICLE_PLATE = "vehicle_plate"
        private const val KEY_CUSTOM_API_URL = "custom_api_url"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also {
                    instance = it
                    // Sync persisted server URL into AppConfig if previously changed
                    it.getCustomApiUrl()?.let { url ->
                        if (url.isNotBlank()) AppConfig.setCustomApiBaseUrl(url)
                    }
                }
            }
        }
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun saveUser(
        userId: Int,
        firstName: String,
        lastName: String,
        email: String,
        phone: String?,
        username: String,
        picture: String? = null,
        isVerified: Boolean = false,
        isPhoneVerified: Boolean = true
    ) {
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_FIRST_NAME, firstName)
            .putString(KEY_LAST_NAME, lastName)
            .putString(KEY_EMAIL, email)
            .putString(KEY_PHONE, phone)
            .putString(KEY_USERNAME, username)
            .putString(KEY_PICTURE, picture)
            .putBoolean(KEY_IS_VERIFIED, isVerified)
            .putBoolean(KEY_IS_PHONE_VERIFIED, isPhoneVerified)
            .apply()
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getFirstName(): String = prefs.getString(KEY_FIRST_NAME, "") ?: ""
    fun getLastName(): String = prefs.getString(KEY_LAST_NAME, "") ?: ""
    fun getFullName(): String {
        val first = prefs.getString(KEY_FIRST_NAME, "") ?: ""
        val last = prefs.getString(KEY_LAST_NAME, "") ?: ""
        return "$first $last".trim()
    }
    fun getEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""
    fun getPhone(): String = prefs.getString(KEY_PHONE, "") ?: ""
    fun getUsername(): String = prefs.getString(KEY_USERNAME, "") ?: ""
    fun getPicture(): String? = prefs.getString(KEY_PICTURE, null)
    fun isEmailVerified(): Boolean = prefs.getBoolean(KEY_IS_VERIFIED, false)
    fun isPhoneVerified(): Boolean = prefs.getBoolean(KEY_IS_PHONE_VERIFIED, true)

    fun setEmailVerified(verified: Boolean) {
        prefs.edit().putBoolean(KEY_IS_VERIFIED, verified).apply()
    }

    fun setPhoneVerified(verified: Boolean) {
        prefs.edit().putBoolean(KEY_IS_PHONE_VERIFIED, verified).apply()
    }

    fun setPicture(pictureUrl: String?) {
        prefs.edit().putString(KEY_PICTURE, pictureUrl).apply()
    }

    fun saveRiderProfile(
        riderId: Int,
        approvalStatus: ApprovalStatus,
        riderStatus: RiderStatus,
        vehicleType: VehicleType,
        vehiclePlate: String?
    ) {
        prefs.edit()
            .putInt(KEY_RIDER_ID, riderId)
            .putString(KEY_APPROVAL_STATUS, approvalStatus.name)
            .putString(KEY_RIDER_STATUS, riderStatus.name)
            .putString(KEY_VEHICLE_TYPE, vehicleType.name)
            .putString(KEY_VEHICLE_PLATE, vehiclePlate)
            .apply()
    }

    fun getRiderId(): Int = prefs.getInt(KEY_RIDER_ID, -1)
    fun getApprovalStatus(): ApprovalStatus {
        val raw = prefs.getString(KEY_APPROVAL_STATUS, ApprovalStatus.PENDING.name)
        return try {
            ApprovalStatus.valueOf(raw ?: ApprovalStatus.PENDING.name)
        } catch (_: Exception) {
            ApprovalStatus.PENDING
        }
    }

    fun getRiderStatus(): RiderStatus {
        val raw = prefs.getString(KEY_RIDER_STATUS, RiderStatus.OFFLINE.name)
        return try {
            RiderStatus.valueOf(raw ?: RiderStatus.OFFLINE.name)
        } catch (_: Exception) {
            RiderStatus.OFFLINE
        }
    }

    fun updateRiderStatus(status: RiderStatus) {
        prefs.edit().putString(KEY_RIDER_STATUS, status.name).apply()
    }

    fun getVehicleType(): VehicleType {
        val raw = prefs.getString(KEY_VEHICLE_TYPE, VehicleType.MOTORCYCLE.name)
        return try {
            VehicleType.valueOf(raw ?: VehicleType.MOTORCYCLE.name)
        } catch (_: Exception) {
            VehicleType.MOTORCYCLE
        }
    }

    fun getVehiclePlate(): String = prefs.getString(KEY_VEHICLE_PLATE, "") ?: ""

    fun saveCustomApiUrl(url: String) {
        prefs.edit().putString(KEY_CUSTOM_API_URL, url).apply()
        AppConfig.setCustomApiBaseUrl(url)
    }

    fun getCustomApiUrl(): String? = prefs.getString(KEY_CUSTOM_API_URL, null)

    fun clear() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_FIRST_NAME)
            .remove(KEY_LAST_NAME)
            .remove(KEY_EMAIL)
            .remove(KEY_PHONE)
            .remove(KEY_USERNAME)
            .remove(KEY_PICTURE)
            .remove(KEY_IS_VERIFIED)
            .remove(KEY_IS_PHONE_VERIFIED)
            .remove(KEY_RIDER_ID)
            .remove(KEY_APPROVAL_STATUS)
            .remove(KEY_RIDER_STATUS)
            .remove(KEY_VEHICLE_TYPE)
            .remove(KEY_VEHICLE_PLATE)
            .apply()
    }
}
