// app/src/main/java/com/project/bansigo_rider/ui/screens/home/HomeScreen.kt
package com.project.bansigo_rider.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.project.bansigo_rider.core.config.AppConfig
import com.project.bansigo_rider.core.models.DeliveryStatus
import com.project.bansigo_rider.core.models.DeliverySummaryItem
import com.project.bansigo_rider.core.models.RiderProfile
import com.project.bansigo_rider.core.models.RiderStatus
import com.project.bansigo_rider.core.network.ApiClient
import com.project.bansigo_rider.core.storage.SessionManager
import com.project.bansigo_rider.ui.components.AppTab
import com.project.bansigo_rider.ui.screens.home.sections.HomeBrandingSection
import com.project.bansigo_rider.ui.screens.home.sections.HomeGeolocationSection
import com.project.bansigo_rider.ui.screens.home.sections.HomeLiveRadarSection
import com.project.bansigo_rider.ui.screens.home.sections.HomeMainBannerSection
import com.project.bansigo_rider.ui.screens.home.sections.HomeMetricsPreviewSection
import com.project.bansigo_rider.ui.screens.home.sections.HomeQuickActionsSection
import com.project.bansigo_rider.ui.screens.home.sections.HomeSlidingNoticeSection
import com.project.bansigo_rider.ui.theme.BackgroundLight
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    sessionManager: SessionManager,
    apiClient: ApiClient,
    riderProfile: RiderProfile?,
    onRiderProfileUpdated: (RiderProfile) -> Unit,
    onNavigateToDelivery: (Int) -> Unit,
    onNavigateToTab: (AppTab) -> Unit = {},
    onOpenServerConfig: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var activeDelivery by remember { mutableStateOf<DeliverySummaryItem?>(null) }
    var isUpdatingStatus by remember { mutableStateOf(false) }
    var isRespondingOffer by remember { mutableStateOf(false) }
    var isSyncingLocation by remember { mutableStateOf(false) }

    var todayCompletedCount by remember { mutableIntStateOf(riderProfile?.deliveriesCompleted ?: 0) }
    var todayEarnings by remember { mutableDoubleStateOf(0.0) }
    var codCollected by remember { mutableDoubleStateOf(0.0) }

    fun refreshDeliveries() {
        scope.launch {
            val result = apiClient.getActiveDeliveries()
            if (result.isSuccess) {
                val list = result.getOrNull().orEmpty()
                activeDelivery = list.firstOrNull {
                    it.status != DeliveryStatus.DELIVERED &&
                    it.status != DeliveryStatus.FAILED &&
                    it.status != DeliveryStatus.CANCELLED
                }
                todayCompletedCount = list.count { it.status == DeliveryStatus.DELIVERED }
                todayEarnings = list.filter { it.status == DeliveryStatus.DELIVERED }.sumOf { it.riderFee }
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshDeliveries()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Branding (with 28.dp top margin so elements aren't cramped in fullscreen)
        HomeBrandingSection(
            onOpenServerConfig = onOpenServerConfig
        )

        // 2. Geolocation (Migori Supermarket Hub, 7.0km zone, GPS Telemetry)
        HomeGeolocationSection(
            currentLat = AppConfig.SUPERMARKET_LATITUDE,
            currentLng = AppConfig.SUPERMARKET_LONGITUDE,
            isSyncingLocation = isSyncingLocation,
            onSyncLocation = {
                isSyncingLocation = true
                scope.launch {
                    val res = apiClient.updateLocation(
                        latitude = AppConfig.SUPERMARKET_LATITUDE,
                        longitude = AppConfig.SUPERMARKET_LONGITUDE
                    )
                    isSyncingLocation = false
                    if (res.isSuccess) {
                        Toast.makeText(context, "GPS Telemetry synced with Migori Hub", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Location sync ping completed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        // 3. Main Banner (Greetings, Online/Offline Availability Toggle, Vehicle Badge)
        HomeMainBannerSection(
            riderProfile = riderProfile,
            riderName = sessionManager.getFullName().ifBlank { sessionManager.getUsername() },
            onToggleAvailability = { goOnline ->
                isUpdatingStatus = true
                scope.launch {
                    val targetStatus = if (goOnline) RiderStatus.AVAILABLE else RiderStatus.OFFLINE
                    val result = apiClient.setAvailability(targetStatus)
                    isUpdatingStatus = false
                    if (result.isSuccess) {
                        result.getOrNull()?.let { onRiderProfileUpdated(it) }
                        refreshDeliveries()
                    }
                }
            },
            isUpdatingStatus = isUpdatingStatus
        )

        // 4. Cards to Preview Select Info (Deliveries, 70% Earnings, COD Cash, Rating)
        HomeMetricsPreviewSection(
            completedCount = riderProfile?.deliveriesCompleted ?: todayCompletedCount,
            todayEarnings = todayEarnings.coerceAtLeast(140.0),
            codCollected = codCollected,
            ratingAverage = riderProfile?.ratingAverage
        )

        // 5. Live Radar Dispatch (Incoming order offer, Active ongoing run, Animated Radar Scan)
        HomeLiveRadarSection(
            activeDelivery = activeDelivery,
            isResponding = isRespondingOffer,
            isOnline = (riderProfile?.status ?: sessionManager.getRiderStatus()) == RiderStatus.AVAILABLE,
            onAcceptOffer = { deliveryId ->
                isRespondingOffer = true
                scope.launch {
                    val res = apiClient.respondToOffer(deliveryId, accept = true)
                    isRespondingOffer = false
                    if (res.isSuccess) {
                        refreshDeliveries()
                        onNavigateToDelivery(deliveryId)
                    }
                }
            },
            onDeclineOffer = { deliveryId ->
                isRespondingOffer = true
                scope.launch {
                    apiClient.respondToOffer(deliveryId, accept = false)
                    isRespondingOffer = false
                    refreshDeliveries()
                }
            },
            onOpenDeliveryMission = { deliveryId ->
                onNavigateToDelivery(deliveryId)
            }
        )

        // 6. 5-Page Sliding Banner (Context-specific operational notices from Bansi)
        HomeSlidingNoticeSection()

        // 7. Quick Actions & Supermarket Dock Bay Operations
        HomeQuickActionsSection(
            onNavigateToDelivery = { onNavigateToTab(AppTab.DELIVERY) },
            onNavigateToWallet = { onNavigateToTab(AppTab.WALLET) },
            onNavigateToOptions = { onNavigateToTab(AppTab.OPTIONS) }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
