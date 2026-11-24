// app/src/main/java/com/project/bansigo_rider/MainActivity.kt
package com.project.bansigo_rider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.project.bansigo_rider.core.models.ApprovalStatus
import com.project.bansigo_rider.core.models.DeliveryStatus
import com.project.bansigo_rider.core.models.RiderProfile
import com.project.bansigo_rider.core.models.RiderStatus
import com.project.bansigo_rider.core.network.ApiClient
import com.project.bansigo_rider.core.storage.SessionManager
import com.project.bansigo_rider.ui.components.AppNavBar
import com.project.bansigo_rider.ui.components.AppTab
import com.project.bansigo_rider.ui.components.AppTopBar
import com.project.bansigo_rider.ui.components.ServerConfigDialog
import com.project.bansigo_rider.ui.screens.auth.AuthScreen
import com.project.bansigo_rider.ui.screens.delivery.DeliveryScreen
import com.project.bansigo_rider.ui.screens.home.HomeScreen
import com.project.bansigo_rider.ui.screens.options.OptionsScreen
import com.project.bansigo_rider.ui.screens.wallet.WalletScreen
import com.project.bansigo_rider.ui.theme.BackgroundLight
import com.project.bansigo_rider.ui.theme.Bansigo_riderTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()

        sessionManager = SessionManager.getInstance(this)
        apiClient = ApiClient(sessionManager)

        setContent {
            Bansigo_riderTheme {
                RiderApp(
                    sessionManager = sessionManager,
                    apiClient = apiClient
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    /**
     * Immersive Fullscreen Mode:
     * Hides the bottom navigation action buttons and top status bar (battery, notifications, clock).
     * System bars will briefly show with a swipe from the edge and automatically hide again.
     */
    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}

@Composable
fun RiderApp(
    sessionManager: SessionManager,
    apiClient: ApiClient
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    var isLoggedIn by remember { mutableStateOf(sessionManager.isLoggedIn()) }
    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var selectedDeliveryId by remember { mutableStateOf<Int?>(null) }
    var activeDeliveriesCount by remember { mutableIntStateOf(0) }

    var riderProfile by remember { mutableStateOf<RiderProfile?>(null) }
    var showServerConfigDialog by remember { mutableStateOf(false) }
    var isRefreshingApp by remember { mutableStateOf(false) }

    fun refreshRiderProfile() {
        if (!isLoggedIn || isRefreshingApp) return
        scope.launch {
            isRefreshingApp = true
            try {
                apiClient.getCurrentUser()
                val result = apiClient.getRiderProfile()
                if (result.isSuccess) {
                    riderProfile = result.getOrNull()
                }
                // Also refresh active deliveries badge
                val deliveriesRes = apiClient.getActiveDeliveries()
                if (deliveriesRes.isSuccess) {
                    activeDeliveriesCount = deliveriesRes.getOrNull().orEmpty().count {
                        it.status != DeliveryStatus.DELIVERED &&
                        it.status != DeliveryStatus.FAILED &&
                        it.status != DeliveryStatus.CANCELLED
                    }
                }
            } finally {
                isRefreshingApp = false
            }
        }
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            val hasFine = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasFine) {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            refreshRiderProfile()
        }
    }

    // Continuous GPS telemetry ping every 5 seconds while online or fulfilling active deliveries
    LaunchedEffect(isLoggedIn, riderProfile?.status, activeDeliveriesCount) {
        val status = riderProfile?.status ?: sessionManager.getRiderStatus()
        if (!isLoggedIn || (status == RiderStatus.OFFLINE && activeDeliveriesCount == 0)) return@LaunchedEffect
        while (true) {
            val loc = getBestLastKnownLocation(context)
            if (loc != null) {
                apiClient.updateLocation(loc.latitude, loc.longitude)
            }
            delay(5000L)
        }
    }

    if (showServerConfigDialog) {
        ServerConfigDialog(
            sessionManager = sessionManager,
            apiClient = apiClient,
            onDismiss = { showServerConfigDialog = false },
            onSaved = { refreshRiderProfile() }
        )
    }

    if (!isLoggedIn) {
        AuthScreen(
            sessionManager = sessionManager,
            apiClient = apiClient,
            onAuthSuccess = {
                isLoggedIn = true
                currentTab = AppTab.HOME
                refreshRiderProfile()
            }
        )
    } else {
        Scaffold(
            topBar = {
                if (currentTab != AppTab.HOME) {
                    AppTopBar(
                        title = currentTab.title,
                        riderStatus = riderProfile?.status ?: sessionManager.getRiderStatus(),
                        approvalStatus = riderProfile?.approvalStatus ?: sessionManager.getApprovalStatus(),
                        onToggleAvailability = { goOnline ->
                            scope.launch {
                                val target = if (goOnline) RiderStatus.AVAILABLE else RiderStatus.OFFLINE
                                val res = apiClient.setAvailability(target)
                                if (res.isSuccess) {
                                    res.getOrNull()?.let { riderProfile = it }
                                }
                            }
                        },
                        onOpenServerConfig = { showServerConfigDialog = true },
                        onRefresh = { refreshRiderProfile() },
                        isRefreshing = isRefreshingApp
                    )
                }
            },
            bottomBar = {
                AppNavBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        currentTab = tab
                        if (tab == AppTab.DELIVERY) {
                            // If opening delivery tab directly, reset specific selection to latest active
                            selectedDeliveryId = null
                        }
                    },
                    activeDeliveriesCount = activeDeliveriesCount
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    AppTab.HOME -> {
                        HomeScreen(
                            sessionManager = sessionManager,
                            apiClient = apiClient,
                            riderProfile = riderProfile,
                            onRiderProfileUpdated = { riderProfile = it },
                            onNavigateToDelivery = { deliveryId ->
                                selectedDeliveryId = deliveryId
                                currentTab = AppTab.DELIVERY
                            },
                            onNavigateToTab = { tab ->
                                currentTab = tab
                                if (tab == AppTab.DELIVERY) {
                                    selectedDeliveryId = null
                                }
                            },
                            onOpenServerConfig = { showServerConfigDialog = true },
                            onRefreshProfile = { refreshRiderProfile() }
                        )
                    }

                    AppTab.DELIVERY -> {
                        DeliveryScreen(
                            deliveryId = selectedDeliveryId,
                            sessionManager = sessionManager,
                            apiClient = apiClient,
                            onBackToHome = {
                                currentTab = AppTab.HOME
                            }
                        )
                    }

                    AppTab.WALLET -> {
                        WalletScreen(
                            sessionManager = sessionManager,
                            apiClient = apiClient
                        )
                    }

                    AppTab.OPTIONS -> {
                        OptionsScreen(
                            sessionManager = sessionManager,
                            apiClient = apiClient,
                            riderProfile = riderProfile,
                            onLogout = {
                                sessionManager.clear()
                                isLoggedIn = false
                                riderProfile = null
                            },
                            onProfileUpdated = { updated ->
                                riderProfile = updated
                            }
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun getBestLastKnownLocation(context: Context): Location? {
    val hasFine = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    if (!hasFine && !hasCoarse) return null

    val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
    val providers = lm.getProviders(true)
    var best: Location? = null
    for (p in providers) {
        val l = try { lm.getLastKnownLocation(p) } catch (_: Exception) { null } ?: continue
        if (best == null || l.accuracy < best.accuracy) {
            best = l
        }
    }
    return best
}
