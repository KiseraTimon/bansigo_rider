// app/src/main/java/com/project/bansigo_rider/core/network/ApiEndpoints.kt
package com.project.bansigo_rider.core.network

object ApiEndpoints {
    // Auth endpoints in bansigo_server/src/routers/server/auth.py
    const val LOGIN = "/auth/login"
    const val SIGNUP = "/auth/signup"
    const val PASSWORD_RESET = "/auth/password-reset"
    const val PASSWORD_RESET_CONFIRM = "/auth/password-reset/confirm"

    // Profile & verification endpoints in bansigo_server/src/routers/server/profiles.py
    const val USERS_ME = "/users/me"
    const val EMAIL_VERIFICATION = "/users/me/email-verification"
    const val EMAIL_VERIFICATION_CONFIRM = "/users/me/email-verification/confirm"

    // Rider endpoints in bansigo_server/src/routers/rider/deliveries.py
    const val RIDER_APPLY = "/rider/profile/apply"
    const val RIDER_DOCUMENT_UPLOAD = "/rider/documents/upload"
    const val RIDER_PROFILE = "/rider/profile"
    const val RIDER_AVAILABILITY = "/rider/availability"
    const val RIDER_LOCATION = "/rider/location"
    const val RIDER_DELIVERIES_ACTIVE = "/rider/deliveries/active"

    fun deliveryDetail(id: Int): String = "/rider/deliveries/$id"
    fun deliveryRespond(id: Int): String = "/rider/deliveries/$id/respond"
    fun deliveryPickup(id: Int): String = "/rider/deliveries/$id/pickup"
    fun deliveryArrive(id: Int): String = "/rider/deliveries/$id/arrive"
    fun deliveryComplete(id: Int): String = "/rider/deliveries/$id/complete"
    fun deliveryFail(id: Int): String = "/rider/deliveries/$id/fail"
    fun deliveryNavigation(id: Int): String = "/rider/deliveries/$id/navigation"
}
