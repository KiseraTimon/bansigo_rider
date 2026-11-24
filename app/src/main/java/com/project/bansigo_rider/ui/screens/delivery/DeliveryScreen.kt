// app/src/main/java/com/project/bansigo_rider/ui/screens/delivery/DeliveryScreen.kt
package com.project.bansigo_rider.ui.screens.delivery

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.DeliveryDetail
import com.project.bansigo_rider.core.models.DeliveryStatus
import com.project.bansigo_rider.core.models.DeliverySummaryItem
import com.project.bansigo_rider.core.models.GeoPoint
import com.project.bansigo_rider.core.models.RiderNavigation
import com.project.bansigo_rider.core.network.ApiClient
import com.project.bansigo_rider.core.storage.SessionManager
import com.project.bansigo_rider.ui.components.BansiPrimaryButton
import com.project.bansigo_rider.ui.screens.delivery.sections.DeliveryActionsSection
import com.project.bansigo_rider.ui.screens.delivery.sections.DeliveryHeaderSection
import com.project.bansigo_rider.ui.screens.delivery.sections.DeliveryItemsSection
import com.project.bansigo_rider.ui.screens.delivery.sections.DeliveryMapSection
import com.project.bansigo_rider.ui.screens.delivery.sections.DeliveryMultiOrderSelectorSection
import com.project.bansigo_rider.ui.screens.delivery.sections.DeliveryNavigationSection
import com.project.bansigo_rider.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DeliveryScreen(
    deliveryId: Int?,
    sessionManager: SessionManager,
    apiClient: ApiClient,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    var activeDeliveries by remember { mutableStateOf<List<DeliverySummaryItem>>(emptyList()) }
    var selectedDeliveryId by remember { mutableIntStateOf(deliveryId ?: 0) }
    var deliveryDetail by remember { mutableStateOf<DeliveryDetail?>(null) }
    var navigation by remember { mutableStateOf<RiderNavigation?>(null) }
    var otherDropoffs by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }
    var riderLocation by remember { mutableStateOf<GeoPoint?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var isActionLoading by remember { mutableStateOf(false) }
    var isBatchPickupLoading by remember { mutableStateOf(false) }

    fun loadSelectedDelivery(targetId: Int) {
        if (targetId <= 0) {
            deliveryDetail = null
            navigation = null
            isLoading = false
            return
        }
        isLoading = true
        scope.launch {
            val detailRes = apiClient.getDeliveryDetail(targetId)
            if (detailRes.isSuccess) {
                deliveryDetail = detailRes.getOrNull()
                val navRes = apiClient.getNavigation(targetId)
                if (navRes.isSuccess) {
                    navigation = navRes.getOrNull()
                }
            } else {
                deliveryDetail = null
            }
            isLoading = false
        }
    }

    fun refreshAllDeliveries() {
        isLoading = true
        scope.launch {
            val activeRes = apiClient.getActiveDeliveries()
            if (activeRes.isSuccess) {
                val list = activeRes.getOrNull().orEmpty().filter {
                    it.status != DeliveryStatus.DELIVERED &&
                    it.status != DeliveryStatus.FAILED &&
                    it.status != DeliveryStatus.CANCELLED
                }
                activeDeliveries = list

                // Determine which order to select
                val targetId = if (selectedDeliveryId > 0 && list.any { it.id == selectedDeliveryId }) {
                    selectedDeliveryId
                } else if (deliveryId != null && deliveryId > 0 && list.any { it.id == deliveryId }) {
                    deliveryId
                } else {
                    list.firstOrNull()?.id ?: 0
                }

                selectedDeliveryId = targetId
                loadSelectedDelivery(targetId)

                // Collect other active drop-off locations for multi-delivery route display
                val others = mutableListOf<GeoPoint>()
                for (d in list) {
                    if (d.id != targetId) {
                        val otherDetailRes = apiClient.getDeliveryDetail(d.id)
                        otherDetailRes.getOrNull()?.let { others.add(it.dropoffLocation) }
                    }
                }
                otherDropoffs = others
            } else {
                isLoading = false
            }
        }
    }

    LaunchedEffect(deliveryId) {
        if (deliveryId != null && deliveryId > 0) {
            selectedDeliveryId = deliveryId
        }
        refreshAllDeliveries()
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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackToHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = RoyalNavy
                        )
                    }
                    Text(
                        text = "Delivery Mission Control",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                }

                IconButton(onClick = { refreshAllDeliveries() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = TextSecondary
                    )
                }
            }

            if (isLoading && deliveryDetail == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = RoyalNavy)
                }
            } else if (deliveryDetail == null) {
                // Empty state when no active delivery mission
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(RoyalNavyLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TwoWheeler,
                                contentDescription = null,
                                tint = RoyalNavy,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "No Active Delivery Missions",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "When you accept an order from the dispatch radar on the Home tab, the active route and mission actions will appear here.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        BansiPrimaryButton(
                            text = "Return to Home Radar",
                            onClick = onBackToHome,
                            modifier = Modifier.fillMaxWidth(0.7f)
                        )
                    }
                }
            } else {
                val currentDelivery = deliveryDetail!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Section 1: Multi-Delivery Batch Selector (when rider has multiple deliveries)
                    if (activeDeliveries.size > 1) {
                        DeliveryMultiOrderSelectorSection(
                            activeDeliveries = activeDeliveries,
                            selectedDeliveryId = selectedDeliveryId,
                            onSelectDelivery = { newId ->
                                selectedDeliveryId = newId
                                loadSelectedDelivery(newId)
                            },
                            onBatchPickupAll = {
                                isBatchPickupLoading = true
                                scope.launch {
                                    val acceptedList = activeDeliveries.filter { it.status == DeliveryStatus.ACCEPTED }
                                    var count = 0
                                    for (d in acceptedList) {
                                        val res = apiClient.pickupDelivery(d.id)
                                        if (res.isSuccess) count++
                                    }
                                    isBatchPickupLoading = false
                                    refreshAllDeliveries()
                                    snackbarHostState.showSnackbar("Batch picked up $count orders at supermarket bay!")
                                }
                            },
                            isBatchPickupLoading = isBatchPickupLoading
                        )
                    }

                    // Section 2: Order Header & Lifecycle Progress Timeline
                    DeliveryHeaderSection(delivery = currentDelivery)

                    // Section 3: Interactive OpenStreetMap with Shaded Route & Multi-Dropoff Pins
                    DeliveryMapSection(
                        delivery = currentDelivery,
                        riderLocation = riderLocation,
                        otherDropoffs = otherDropoffs,
                        currentStep = navigation?.steps?.firstOrNull()
                    )

                    // Section 4: Turn-by-Turn Guidance Maneuvers
                    DeliveryNavigationSection(navigation = navigation)

                    // Section 5: Recipient & Cargo Manifest Details
                    DeliveryItemsSection(delivery = currentDelivery)

                    // Section 6: Action State Machine (Pickup, Arrive, Handover PIN, Report Issue)
                    DeliveryActionsSection(
                        delivery = currentDelivery,
                        isActionLoading = isActionLoading,
                        onPickup = {
                            isActionLoading = true
                            scope.launch {
                                val res = apiClient.pickupDelivery(currentDelivery.deliveryId)
                                isActionLoading = false
                                if (res.isSuccess) {
                                    refreshAllDeliveries()
                                    snackbarHostState.showSnackbar("Parcel marked picked up! Proceed to customer.")
                                } else {
                                    snackbarHostState.showSnackbar(res.exceptionOrNull()?.localizedMessage ?: "Pickup failed")
                                }
                            }
                        },
                        onArrive = {
                            isActionLoading = true
                            scope.launch {
                                val res = apiClient.arriveDelivery(currentDelivery.deliveryId)
                                isActionLoading = false
                                if (res.isSuccess) {
                                    refreshAllDeliveries()
                                    snackbarHostState.showSnackbar("Marked arrived. Request customer handover PIN.")
                                } else {
                                    snackbarHostState.showSnackbar(res.exceptionOrNull()?.localizedMessage ?: "Action failed")
                                }
                            }
                        },
                        onComplete = { handoverCode, idChecked, cashCollected ->
                            isActionLoading = true
                            scope.launch {
                                val res = apiClient.completeDelivery(
                                    deliveryId = currentDelivery.deliveryId,
                                    handoverCode = handoverCode,
                                    idChecked = idChecked,
                                    cashCollected = cashCollected
                                )
                                isActionLoading = false
                                if (res.isSuccess) {
                                    refreshAllDeliveries()
                                    snackbarHostState.showSnackbar("Delivery verified and completed! Fee added to wallet.")
                                } else {
                                    snackbarHostState.showSnackbar(res.exceptionOrNull()?.localizedMessage ?: "PIN incorrect or completion failed")
                                }
                            }
                        },
                        onFail = { reason ->
                            isActionLoading = true
                            scope.launch {
                                val res = apiClient.failDelivery(currentDelivery.deliveryId, reason)
                                isActionLoading = false
                                if (res.isSuccess) {
                                    refreshAllDeliveries()
                                    snackbarHostState.showSnackbar("Problem report submitted to dispatch console.")
                                } else {
                                    snackbarHostState.showSnackbar(res.exceptionOrNull()?.localizedMessage ?: "Failed to submit report")
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
