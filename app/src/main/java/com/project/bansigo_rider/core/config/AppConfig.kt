// app/src/main/java/com/project/bansigo_rider/core/config/AppConfig.kt
package com.project.bansigo_rider.core.config

enum class AppEnvironment {
    DEVELOPMENT,
    TESTING,
    PRODUCTION
}

data class AppConfig(
    val environment: AppEnvironment = AppEnvironment.DEVELOPMENT,
    val apiBaseUrl: String = USB_DEBUG_URL,
    val connectTimeoutSeconds: Long = 10L,
    val readTimeoutSeconds: Long = 20L,
    val enableLogging: Boolean = true
) {
    companion object {
        // USB Cable debugging with adb reverse tcp:8000 tcp:8000 (0ms latency, works without Wi-Fi)
        const val USB_DEBUG_URL = "http://127.0.0.1:8000/api"
        const val LOCALHOST_URL = "http://localhost:8000/api"
        const val DEFAULT_WIFI_URL = "http://192.168.100.54:8000/api"
        const val EMULATOR_URL = "http://10.0.2.2:8000/api"

        // Bansi Mega Supermarket - Migori Hub
        const val SUPERMARKET_NAME = "Bansi Mega Supermarket"
        const val SUPERMARKET_LATITUDE = -1.0641
        const val SUPERMARKET_LONGITUDE = 34.4741
        const val MAX_DELIVERY_RADIUS_KM = 7.0
        const val BASE_DELIVERY_FEE = 50.0
        const val DELIVERY_FEE_PER_KM = 20.0
        const val RIDER_FEE_SHARE = 0.70 // 70% share of delivery fees goes to rider

        // Default shared instance
        private var _current: AppConfig = AppConfig()
        val current: AppConfig
            get() = _current

        fun setCustomApiBaseUrl(url: String) {
            val trimmed = url.trim().removeSuffix("/")
            _current = _current.copy(apiBaseUrl = trimmed)
        }

        fun setEnvironment(env: AppEnvironment, url: String? = null) {
            val base = url ?: when (env) {
                AppEnvironment.DEVELOPMENT -> USB_DEBUG_URL
                AppEnvironment.TESTING -> LOCALHOST_URL
                AppEnvironment.PRODUCTION -> "https://api.bansigo.com/api"
            }
            _current = _current.copy(
                environment = env,
                apiBaseUrl = base
            )
        }
    }
}
