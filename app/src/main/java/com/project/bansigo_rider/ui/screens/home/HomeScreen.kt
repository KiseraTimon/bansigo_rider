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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.RiderNotification
import kotlinx.coroutines.delay
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sessionManager: SessionManager,
    apiClient: ApiClient,
    riderProfile: RiderProfile?,
    onRiderProfileUpdated: (RiderProfile) -> Unit,
    onNavigateToDelivery: (Int) -> Unit,
    onNavigateToTab: (AppTab) -> Unit = {},
    onOpenServerConfig: () -> Unit = {},
    onRefreshProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var activeDelivery by remember { mutableStateOf<DeliverySummaryItem?>(null) }
    var notifications by remember { mutableStateOf<List<RiderNotification>>(emptyList()) }
    var isUpdatingStatus by remember { mutableStateOf(false) }
    var isRespondingOffer by remember { mutableStateOf(false) }
    var isSyncingLocation by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    var todayCompletedCount by remember { mutableIntStateOf(riderProfile?.deliveriesCompleted ?: 0) }
    var todayEarnings by remember { mutableDoubleStateOf(0.0) }
    var codCollected by remember { mutableDoubleStateOf(0.0) }

    fun refreshDeliveries() {
        scope.launch {
            isRefreshing = true
            try {
                onRefreshProfile()
                val result = apiClient.getActiveDeliveries()
                if (result.isSuccess) {
                    val list = result.getOrNull().orEmpty()
                    activeDelivery = list.firstOrNull {
                        it.status != DeliveryStatus.DELIVERED &&
                        it.status != DeliveryStatus.FAILED &&
                        it.status != DeliveryStatus.CANCELLED
                    }
                }
                val walletRes = apiClient.getRiderWallet()
                if (walletRes.isSuccess) {
                    walletRes.getOrNull()?.summary?.let { summary ->
                        todayCompletedCount = summary.completedTripsCount
                        todayEarnings = summary.todayEarnings
                        codCollected = summary.codCashInHand
                    }
                }
                val notifRes = apiClient.getNotifications()
                if (notifRes.isSuccess) {
                    notifications = notifRes.getOrNull().orEmpty()
                }
            } finally {
                isRefreshing = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshDeliveries()
    }

    // Live dispatch offer & notification polling every 5 seconds while online
    LaunchedEffect(riderProfile?.status, activeDelivery?.id) {
        while (true) {
            delay(5000L)
            val status = riderProfile?.status ?: sessionManager.getRiderStatus()
            if (status != RiderStatus.OFFLINE || activeDelivery != null) {
                val res = apiClient.getActiveDeliveries()
                if (res.isSuccess) {
                    activeDelivery = res.getOrNull().orEmpty().firstOrNull {
                        it.status != DeliveryStatus.DELIVERED &&
                        it.status != DeliveryStatus.FAILED &&
                        it.status != DeliveryStatus.CANCELLED
                    }
                }
                val notifRes = apiClient.getNotifications()
                if (notifRes.isSuccess) {
                    notifications = notifRes.getOrNull().orEmpty()
                }
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { refreshDeliveries() },
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Branding (with 28.dp top margin so elements aren't cramped in fullscreen)
            HomeBrandingSection(
                onOpenServerConfig = onOpenServerConfig,
                onRefresh = { refreshDeliveries() },
                isRefreshing = isRefreshing
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
            isOnline = (riderProfile?.status ?: sessionManager.getRiderStatus()) != RiderStatus.OFFLINE,
            onAcceptOffer = { deliveryId ->
                isRespondingOffer = true
                scope.launch {
                    val res = apiClient.respondToOffer(deliveryId, accept = true)
                    isRespondingOffer = false
                    if (res.isSuccess) {
                        refreshDeliveries()
                        onNavigateToDelivery(deliveryId)
                    } else {
                        Toast.makeText(context, res.exceptionOrNull()?.localizedMessage ?: "Failed to accept", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDeclineOffer = { deliveryId, reasonCode, reasonText ->
                isRespondingOffer = true
                scope.launch {
                    val res = apiClient.respondToOffer(
                        deliveryId = deliveryId,
                        accept = false,
                        reasonCode = reasonCode,
                        reasonText = reasonText
                    )
                    isRespondingOffer = false
                    if (res.isFailure) {
                        Toast.makeText(context, res.exceptionOrNull()?.localizedMessage ?: "Decline failed", Toast.LENGTH_SHORT).show()
                    }
                    refreshDeliveries()
                }
            },
            onOpenDeliveryMission = { deliveryId ->
                onNavigateToDelivery(deliveryId)
            }
        )

        // 5b. Reliability / Cooldown Notice & Recent Notifications (Pickup Codes, SLA Alerts)
        if ((riderProfile?.penaltyPoints ?: 0) > 0 || riderProfile?.dispatchCooldownUntil != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFEF2F2),
                border = BorderStroke(1.dp, Color(0xFFFECACA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Dispatch Reliability Notice (${riderProfile?.penaltyPoints ?: 0} penalty pts)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF991B1B)
                    )
                    Text(
                        text = if (riderProfile?.dispatchCooldownUntil != null)
                            "Auto-dispatch cooldown active until ${riderProfile.dispatchCooldownUntil}. Maintain justified responses and on-time handovers."
                        else
                            "Unjustified rejections: ${riderProfile?.unjustifiedRejectionsCount ?: 0}. Overdue deliveries: ${riderProfile?.overdueDeliveriesCount ?: 0}.",
                        fontSize = 11.5.sp,
                        color = Color(0xFFB91C1C)
                    )
                }
            }
        }

        if (notifications.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Dispatch Notifications (${notifications.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2C59)
                    )
                    notifications.take(3).forEach { notif ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (notif.type == "pickup_code") Color(0xFFECFDF5)
                            else if (notif.type == "penalty" || notif.type == "warning") Color(0xFFFEF3C7)
                            else Color(0xFFF8FAFC),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = notif.title,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = notif.message,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }
                }
            }
        }

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
}
