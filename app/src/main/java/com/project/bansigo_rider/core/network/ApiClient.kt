// app/src/main/java/com/project/bansigo_rider/core/network/ApiClient.kt
package com.project.bansigo_rider.core.network

import com.project.bansigo_rider.core.config.AppConfig
import com.project.bansigo_rider.core.models.*
import com.project.bansigo_rider.core.storage.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiClient(private val sessionManager: SessionManager) {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(AppConfig.current.connectTimeoutSeconds, TimeUnit.SECONDS)
        .readTimeout(AppConfig.current.readTimeoutSeconds, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
                .header("Accept", "application/json")
                .header("User-Agent", "BansiGo-Rider-Android/1.0")

            val token = sessionManager.getToken()
            if (!token.isNullOrBlank() && original.header("Authorization") == null) {
                builder.header("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }
        .build()

    private fun getBaseUrl(): String = AppConfig.current.apiBaseUrl.removeSuffix("/")

    suspend fun pingServer(testUrl: String? = null): Result<Long> = withContext(Dispatchers.IO) {
        val base = (testUrl ?: getBaseUrl()).removeSuffix("/")
        val root = if (base.endsWith("/api")) base.removeSuffix("/api") else base
        val target = "$root/openapi.json"
        val start = System.currentTimeMillis()
        try {
            val request = Request.Builder()
                .url(target)
                .head()
                .build()
            client.newCall(request).execute().use { response ->
                val elapsed = System.currentTimeMillis() - start
                if (response.isSuccessful || response.code in listOf(200, 401, 403, 404, 405)) {
                    Result.success(elapsed)
                } else {
                    Result.failure(IOException("Server returned status ${response.code}"))
                }
            }
        } catch (e: Exception) {
            // Also try fallback ping directly to base URL
            try {
                val fallbackReq = Request.Builder().url(base).get().build()
                client.newCall(fallbackReq).execute().use { resp ->
                    val elapsed = System.currentTimeMillis() - start
                    if (resp.code in listOf(200, 401, 403, 404, 405)) {
                        Result.success(elapsed)
                    } else {
                        Result.failure(e)
                    }
                }
            } catch (_: Exception) {
                Result.failure(e)
            }
        }
    }

    // ----------------------------------------------------
    // AUTHENTICATION
    // ----------------------------------------------------

    suspend fun login(username: String, password: String): Result<TokenResponse> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.LOGIN
        val formBody = FormBody.Builder()
            .add("username", username.trim())
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val message = parseErrorMessage(body, "Login failed (${response.code})")
                    return@withContext Result.failure(IOException(message))
                }
                val json = JSONObject(body)
                val token = TokenResponse(
                    accessToken = json.getString("access_token"),
                    tokenType = json.optString("token_type", "bearer")
                )
                sessionManager.saveToken(token.accessToken)
                Result.success(token)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        username: String? = null
    ): Result<UserProfile> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.SIGNUP
        val json = JSONObject().apply {
            put("first_name", firstName.trim())
            put("last_name", lastName.trim())
            put("email", email.trim())
            put("phone", phone.trim())
            put("role", "rider")
            if (!username.isNullOrBlank()) {
                put("username", username.trim())
            }
            put("password", password)
            put("password_confirm", password)
        }

        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody(jsonMediaType))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    val message = parseErrorMessage(body, "Registration failed (${response.code})")
                    return@withContext Result.failure(IOException(message))
                }
                val respJson = JSONObject(body)
                val user = UserProfile(
                    id = respJson.getInt("id"),
                    firstName = respJson.getString("first_name"),
                    lastName = respJson.getString("last_name"),
                    email = respJson.getString("email"),
                    phone = if (respJson.isNull("phone")) null else respJson.optString("phone"),
                    username = respJson.getString("username"),
                    role = respJson.optString("role", "rider"),
                    picture = if (respJson.isNull("picture")) null else respJson.optString("picture"),
                    isVerified = respJson.optBoolean("is_verified", false),
                    isPhoneVerified = true
                )
                sessionManager.saveUser(
                    userId = user.id,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    phone = user.phone,
                    username = user.username,
                    picture = user.picture,
                    isVerified = user.isVerified,
                    isPhoneVerified = user.isPhoneVerified
                )
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<UserProfile> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.USERS_ME
        val request = Request.Builder().url(url).get().build()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Failed to load user")))
                }
                val json = JSONObject(body)
                val user = UserProfile(
                    id = json.getInt("id"),
                    firstName = json.getString("first_name"),
                    lastName = json.getString("last_name"),
                    email = json.getString("email"),
                    phone = if (json.isNull("phone")) null else json.optString("phone"),
                    username = json.getString("username"),
                    role = json.optString("role", "rider"),
                    picture = if (json.isNull("picture")) null else json.optString("picture"),
                    isVerified = json.optBoolean("is_verified", false),
                    isPhoneVerified = true
                )
                sessionManager.saveUser(
                    userId = user.id,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    phone = user.phone,
                    username = user.username,
                    picture = user.picture,
                    isVerified = user.isVerified,
                    isPhoneVerified = user.isPhoneVerified
                )
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        firstName: String? = null,
        lastName: String? = null,
        username: String? = null,
        email: String? = null,
        phone: String? = null,
        picture: String? = null
    ): Result<UserProfile> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.USERS_ME
        val json = JSONObject()
        if (firstName != null) json.put("first_name", firstName.trim())
        if (lastName != null) json.put("last_name", lastName.trim())
        if (username != null) json.put("username", username.trim())
        if (email != null) json.put("email", email.trim())
        if (phone != null) json.put("phone", phone.trim())
        if (picture != null) json.put("picture", picture)

        val request = Request.Builder()
            .url(url)
            .patch(json.toString().toRequestBody(jsonMediaType))
            .build()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Failed to update profile")))
                }
                val respJson = JSONObject(body)
                val user = UserProfile(
                    id = respJson.getInt("id"),
                    firstName = respJson.getString("first_name"),
                    lastName = respJson.getString("last_name"),
                    email = respJson.getString("email"),
                    phone = if (respJson.isNull("phone")) null else respJson.optString("phone"),
                    username = respJson.getString("username"),
                    role = respJson.optString("role", "rider"),
                    picture = if (respJson.isNull("picture")) null else respJson.optString("picture"),
                    isVerified = respJson.optBoolean("is_verified", false),
                    isPhoneVerified = true
                )
                sessionManager.saveUser(
                    userId = user.id,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    phone = user.phone,
                    username = user.username,
                    picture = user.picture,
                    isVerified = user.isVerified,
                    isPhoneVerified = user.isPhoneVerified
                )
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestEmailVerification(): Result<String> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.EMAIL_VERIFICATION
        val request = Request.Builder()
            .url(url)
            .post("{}".toRequestBody(jsonMediaType))
            .build()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful && response.code != 202) {
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Failed to send verification code")))
                }
                Result.success("Verification code has been sent to your email.")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmEmailVerification(code: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.EMAIL_VERIFICATION_CONFIRM
        val json = JSONObject().apply {
            put("code", code.trim())
        }
        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody(jsonMediaType))
            .build()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Invalid verification code")))
                }
                val respJson = JSONObject(body)
                val user = UserProfile(
                    id = respJson.getInt("id"),
                    firstName = respJson.getString("first_name"),
                    lastName = respJson.getString("last_name"),
                    email = respJson.getString("email"),
                    phone = if (respJson.isNull("phone")) null else respJson.optString("phone"),
                    username = respJson.getString("username"),
                    role = respJson.optString("role", "rider"),
                    picture = if (respJson.isNull("picture")) null else respJson.optString("picture"),
                    isVerified = respJson.optBoolean("is_verified", true),
                    isPhoneVerified = true
                )
                sessionManager.saveUser(
                    userId = user.id,
                    firstName = user.firstName,
                    lastName = user.lastName,
                    email = user.email,
                    phone = user.phone,
                    username = user.username,
                    picture = user.picture,
                    isVerified = user.isVerified,
                    isPhoneVerified = user.isPhoneVerified
                )
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun requestPasswordReset(email: String): Result<String> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.PASSWORD_RESET
        val json = JSONObject().apply {
            put("email", email.trim())
        }
        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody(jsonMediaType))
            .build()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful && response.code != 202) {
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Reset request failed")))
                }
                Result.success("If the email is registered, a reset code has been sent.")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun confirmPasswordReset(email: String, code: String, newPassword: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.PASSWORD_RESET_CONFIRM
        val json = JSONObject().apply {
            put("email", email.trim())
            put("code", code.trim())
            put("new_password", newPassword)
        }
        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody(jsonMediaType))
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful && response.code != 204) {
                    val body = response.body?.string().orEmpty()
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Failed to reset password")))
                }
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // RIDER PROFILE & AVAILABILITY
    // ----------------------------------------------------

    suspend fun applyRiderProfile(req: RiderApplyRequest): Result<RiderProfile> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.RIDER_APPLY
        val json = JSONObject().apply {
            put("national_id_number", req.nationalIdNumber.trim())
            req.idCardFrontUrl?.let { if (it.isNotBlank()) put("id_card_front_url", it.trim()) }
            req.idCardBackUrl?.let { if (it.isNotBlank()) put("id_card_back_url", it.trim()) }
            put("vehicle_type", req.vehicleType.name.lowercase())
            req.vehiclePlate?.let { if (it.isNotBlank()) put("vehicle_plate", it.trim().uppercase()) }
            req.vehicleDescription?.let { if (it.isNotBlank()) put("vehicle_description", it.trim()) }
            req.vehicleProofUrl?.let { if (it.isNotBlank()) put("vehicle_proof_url", it.trim()) }
            req.licenceNumber?.let { if (it.isNotBlank()) put("licence_number", it.trim()) }
            req.licenceExpiry?.let { if (it.isNotBlank()) put("licence_expiry", it.trim()) }
            req.drivingLicenceUrl?.let { if (it.isNotBlank()) put("driving_licence_url", it.trim()) }
        }

        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody(jsonMediaType))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Application failed")))
                }
                val rider = parseRiderProfile(JSONObject(body))
                sessionManager.saveRiderProfile(
                    riderId = rider.id,
                    approvalStatus = rider.approvalStatus,
                    riderStatus = rider.status,
                    vehicleType = rider.vehicleType,
                    vehiclePlate = rider.vehiclePlate
                )
                Result.success(rider)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadRiderDocument(
        docType: String,
        fileName: String,
        fileBytes: ByteArray,
        mimeType: String = "image/jpeg"
    ): Result<String> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.RIDER_DOCUMENT_UPLOAD + "?doc_type=" + docType.trim()
        val mediaType = mimeType.toMediaType()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                fileName,
                fileBytes.toRequestBody(mediaType)
            )
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Upload failed (${response.code})")))
                }
                val json = JSONObject(body)
                val uploadedUrl = json.getString("url")
                Result.success(uploadedUrl)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRiderProfile(): Result<RiderProfile> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.RIDER_PROFILE
        val request = Request.Builder().url(url).get().build()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Profile lookup failed")))
                }
                val rider = parseRiderProfile(JSONObject(body))
                sessionManager.saveRiderProfile(
                    riderId = rider.id,
                    approvalStatus = rider.approvalStatus,
                    riderStatus = rider.status,
                    vehicleType = rider.vehicleType,
                    vehiclePlate = rider.vehiclePlate
                )
                Result.success(rider)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setAvailability(newStatus: RiderStatus): Result<RiderProfile> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.RIDER_AVAILABILITY
        val json = JSONObject().apply {
            put("status", newStatus.name.lowercase())
        }
        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody(jsonMediaType))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Availability update failed")))
                }
                val rider = parseRiderProfile(JSONObject(body))
                sessionManager.updateRiderStatus(rider.status)
                Result.success(rider)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLocation(latitude: Double, longitude: Double): Result<Boolean> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.RIDER_LOCATION
        val json = JSONObject().apply {
            put("latitude", latitude)
            put("longitude", longitude)
        }
        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody(jsonMediaType))
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(IOException("Location ping returned ${response.code}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // DELIVERIES & LIFECYCLE
    // ----------------------------------------------------

    suspend fun getActiveDeliveries(): Result<List<DeliverySummaryItem>> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.RIDER_DELIVERIES_ACTIVE
        val request = Request.Builder().url(url).get().build()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Failed to load deliveries")))
                }
                val array = JSONArray(body)
                val list = mutableListOf<DeliverySummaryItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        DeliverySummaryItem(
                            id = obj.getInt("id"),
                            orderId = obj.getInt("order_id"),
                            status = parseDeliveryStatus(obj.optString("status", "assigned")),
                            riderFee = obj.optDouble("rider_fee", 50.0),
                            estimatedDistanceM = if (obj.isNull("estimated_distance_m")) null else obj.optInt("estimated_distance_m"),
                            estimatedDurationS = if (obj.isNull("estimated_duration_s")) null else obj.optInt("estimated_duration_s"),
                            assignedAt = if (obj.isNull("assigned_at")) null else obj.optString("assigned_at")
                        )
                    )
                }
                Result.success(list)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDeliveryDetail(deliveryId: Int): Result<DeliveryDetail> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.deliveryDetail(deliveryId)
        val request = Request.Builder().url(url).get().build()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Delivery details failed")))
                }
                val obj = JSONObject(body)
                val pickupJson = obj.optJSONObject("pickup_location") ?: JSONObject()
                val dropoffJson = obj.optJSONObject("dropoff_location") ?: JSONObject()

                val waypointsList = mutableListOf<GeoPoint>()
                val waypointsArray = obj.optJSONArray("route_waypoints")
                if (waypointsArray != null) {
                    for (i in 0 until waypointsArray.length()) {
                        val pt = waypointsArray.optJSONArray(i)
                        if (pt != null && pt.length() >= 2) {
                            waypointsList.add(GeoPoint(pt.getDouble(0), pt.getDouble(1)))
                        }
                    }
                }

                val detail = DeliveryDetail(
                    deliveryId = obj.getInt("delivery_id"),
                    orderId = obj.getInt("order_id"),
                    orderNumber = obj.optString("order_number", "#" + obj.getInt("order_id")),
                    status = parseDeliveryStatus(obj.optString("status", "assigned")),
                    requiresIdCheck = obj.optBoolean("requires_id_check", false),
                    riderFee = obj.optDouble("rider_fee", 50.0),
                    pickupLocation = GeoPoint(
                        latitude = pickupJson.optDouble("latitude", AppConfig.SUPERMARKET_LATITUDE),
                        longitude = pickupJson.optDouble("longitude", AppConfig.SUPERMARKET_LONGITUDE),
                        name = pickupJson.optString("name", AppConfig.SUPERMARKET_NAME),
                        address = pickupJson.optString("address", "Migori - Sirare Highway")
                    ),
                    dropoffLocation = GeoPoint(
                        latitude = dropoffJson.optDouble("latitude", AppConfig.SUPERMARKET_LATITUDE),
                        longitude = dropoffJson.optDouble("longitude", AppConfig.SUPERMARKET_LONGITUDE),
                        name = dropoffJson.optString("recipient_name", "Customer"),
                        address = dropoffJson.optString("address_text", dropoffJson.optString("street_address", "Migori Area"))
                    ),
                    customerName = dropoffJson.optString("recipient_name", "Customer"),
                    customerPhone = dropoffJson.optString("recipient_phone", dropoffJson.optString("phone", "")),
                    customerNote = if (obj.isNull("customer_note")) null else obj.optString("customer_note"),
                    paymentMethod = obj.optString("payment_method", "mpesa"),
                    cashToCollect = if (obj.isNull("cash_to_collect")) null else obj.optDouble("cash_to_collect"),
                    estimatedDistanceM = if (obj.isNull("estimated_distance_m")) null else obj.optInt("estimated_distance_m"),
                    estimatedDurationS = if (obj.isNull("estimated_duration_s")) null else obj.optInt("estimated_duration_s"),
                    routingAlgorithm = obj.optString("routing_algorithm", "dijkstra"),
                    routeWaypoints = waypointsList
                )
                Result.success(detail)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun respondToOffer(deliveryId: Int, accept: Boolean): Result<Boolean> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.deliveryRespond(deliveryId)
        val json = JSONObject().apply { put("accept", accept) }
        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody(jsonMediaType))
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) Result.success(true)
                else Result.failure(IOException(parseErrorMessage(response.body?.string().orEmpty(), "Response failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun pickupDelivery(deliveryId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.deliveryPickup(deliveryId)
        val request = Request.Builder()
            .url(url)
            .post("{}".toRequestBody(jsonMediaType))
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) Result.success(true)
                else Result.failure(IOException(parseErrorMessage(response.body?.string().orEmpty(), "Pickup failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun arriveDelivery(deliveryId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.deliveryArrive(deliveryId)
        val request = Request.Builder()
            .url(url)
            .post("{}".toRequestBody(jsonMediaType))
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) Result.success(true)
                else Result.failure(IOException(parseErrorMessage(response.body?.string().orEmpty(), "Arrive failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeDelivery(
        deliveryId: Int,
        handoverCode: String,
        idChecked: Boolean,
        cashCollected: Double? = null
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.deliveryComplete(deliveryId)
        val json = JSONObject().apply {
            put("handover_code", handoverCode.trim())
            put("id_checked", idChecked)
            cashCollected?.let { put("cash_collected", it) }
        }
        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody(jsonMediaType))
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) Result.success(true)
                else Result.failure(IOException(parseErrorMessage(response.body?.string().orEmpty(), "Handover PIN invalid or completion failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun failDelivery(deliveryId: Int, reason: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val url = getBaseUrl() + ApiEndpoints.deliveryFail(deliveryId)
        val json = JSONObject().apply { put("reason", reason.trim()) }
        val request = Request.Builder()
            .url(url)
            .post(json.toString().toRequestBody(jsonMediaType))
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) Result.success(true)
                else Result.failure(IOException(parseErrorMessage(response.body?.string().orEmpty(), "Failed to report delivery problem")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNavigation(
        deliveryId: Int,
        lat: Double? = null,
        lon: Double? = null
    ): Result<RiderNavigation> = withContext(Dispatchers.IO) {
        var url = getBaseUrl() + ApiEndpoints.deliveryNavigation(deliveryId)
        if (lat != null && lon != null) {
            url += "?lat=$lat&lon=$lon"
        }
        val request = Request.Builder().url(url).get().build()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException(parseErrorMessage(body, "Navigation failed")))
                }
                val obj = JSONObject(body)
                val stepsList = mutableListOf<NavigationStep>()
                val stepsArray = obj.optJSONArray("steps")
                if (stepsArray != null) {
                    for (i in 0 until stepsArray.length()) {
                        val s = stepsArray.getJSONObject(i)
                        stepsList.add(
                            NavigationStep(
                                instruction = s.optString("instruction", "Continue"),
                                distanceM = s.optInt("distance_m", 0),
                                durationS = s.optInt("duration_s", 0),
                                streetName = s.optString("street_name", ""),
                                type = s.optString("type", "straight")
                            )
                        )
                    }
                }

                val waypointsList = mutableListOf<GeoPoint>()
                val waypointsArray = obj.optJSONArray("waypoints")
                if (waypointsArray != null) {
                    for (i in 0 until waypointsArray.length()) {
                        val pt = waypointsArray.optJSONArray(i)
                        if (pt != null && pt.length() >= 2) {
                            waypointsList.add(GeoPoint(pt.getDouble(0), pt.getDouble(1)))
                        }
                    }
                }

                val nav = RiderNavigation(
                    deliveryId = obj.getInt("delivery_id"),
                    leg = obj.optString("leg", "to_customer"),
                    destinationName = obj.optString("destination_name", "Destination"),
                    distanceM = obj.optInt("distance_m", 0),
                    durationS = obj.optInt("duration_s", 0),
                    estimatedMinutes = obj.optInt("estimated_minutes", 1),
                    routingAlgorithm = obj.optString("routing_algorithm", "dijkstra"),
                    steps = stepsList,
                    waypoints = waypointsList
                )
                Result.success(nav)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------
    // PARSING HELPERS
    // ----------------------------------------------------

    private fun parseRiderProfile(json: JSONObject): RiderProfile {
        val appStatusStr = json.optString("approval_status", "pending").uppercase()
        val riderStatusStr = json.optString("status", "offline").uppercase()
        val vehicleTypeStr = json.optString("vehicle_type", "motorcycle").uppercase()

        val approvalStatus = try {
            ApprovalStatus.valueOf(appStatusStr)
        } catch (_: Exception) {
            ApprovalStatus.PENDING
        }

        val status = try {
            RiderStatus.valueOf(riderStatusStr)
        } catch (_: Exception) {
            RiderStatus.OFFLINE
        }

        val vehicleType = try {
            VehicleType.valueOf(vehicleTypeStr)
        } catch (_: Exception) {
            VehicleType.MOTORCYCLE
        }

        return RiderProfile(
            id = json.getInt("id"),
            userId = json.getInt("user_id"),
            approvalStatus = approvalStatus,
            status = status,
            vehicleType = vehicleType,
            vehiclePlate = if (json.isNull("vehicle_plate")) null else json.optString("vehicle_plate"),
            vehicleDescription = if (json.isNull("vehicle_description")) null else json.optString("vehicle_description"),
            vehicleProofUrl = if (json.isNull("vehicle_proof_url")) null else json.optString("vehicle_proof_url"),
            nationalIdNumber = if (json.isNull("national_id_number")) null else json.optString("national_id_number"),
            idCardFrontUrl = if (json.isNull("id_card_front_url")) null else json.optString("id_card_front_url"),
            idCardBackUrl = if (json.isNull("id_card_back_url")) null else json.optString("id_card_back_url"),
            licenceNumber = if (json.isNull("licence_number")) null else json.optString("licence_number"),
            licenceExpiry = if (json.isNull("licence_expiry")) null else json.optString("licence_expiry"),
            drivingLicenceUrl = if (json.isNull("driving_licence_url")) null else json.optString("driving_licence_url"),
            ratingAverage = if (json.isNull("rating_average")) null else json.optDouble("rating_average"),
            ratingCount = json.optInt("rating_count", 0),
            deliveriesCompleted = json.optInt("deliveries_completed", 0),
            lastSeenAt = if (json.isNull("last_seen_at")) null else json.optString("last_seen_at")
        )
    }

    private fun parseDeliveryStatus(statusStr: String): DeliveryStatus {
        return try {
            DeliveryStatus.valueOf(statusStr.uppercase())
        } catch (_: Exception) {
            DeliveryStatus.ASSIGNED
        }
    }

    private fun parseErrorMessage(responseBody: String, fallback: String): String {
        return try {
            val json = JSONObject(responseBody)
            if (json.has("detail")) {
                val detail = json.get("detail")
                if (detail is JSONArray) {
                    val first = detail.optJSONObject(0)
                    first?.optString("msg") ?: detail.toString()
                } else {
                    detail.toString()
                }
            } else {
                fallback
            }
        } catch (_: Exception) {
            fallback
        }
    }
}
