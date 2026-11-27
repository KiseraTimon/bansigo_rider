// app/src/main/java/com/project/bansigo_rider/ui/screens/options/OptionsScreen.kt
package com.project.bansigo_rider.ui.screens.options

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.RiderProfile
import com.project.bansigo_rider.core.network.ApiClient
import com.project.bansigo_rider.core.storage.SessionManager
import com.project.bansigo_rider.ui.components.BansiPrimaryButton
import com.project.bansigo_rider.ui.components.ServerConfigDialog
import com.project.bansigo_rider.ui.screens.options.sections.OptionsAccountSettingsSection
import com.project.bansigo_rider.ui.screens.options.sections.OptionsBusinessSettingsSection
import com.project.bansigo_rider.ui.screens.options.sections.OptionsBusinessWizardSection
import com.project.bansigo_rider.ui.screens.options.sections.OptionsContactUsSection
import com.project.bansigo_rider.ui.screens.options.sections.OptionsEmailVerificationSection
import com.project.bansigo_rider.ui.screens.options.sections.OptionsHubSection
import com.project.bansigo_rider.ui.screens.options.sections.OptionsPaymentMethodsSection
import com.project.bansigo_rider.ui.screens.options.sections.OptionsPreferencesSection
import com.project.bansigo_rider.ui.screens.options.sections.OptionsRiderReviewsSection
import com.project.bansigo_rider.ui.theme.*
import kotlinx.coroutines.launch

enum class OptionsSubPage {
    HUB,
    ACCOUNT,
    EMAIL_VERIFICATION,
    BUSINESS,
    BUSINESS_WIZARD,
    REVIEWS,
    PAYMENT,
    CONTACT,
    PREFERENCES
}

@Composable
fun OptionsScreen(
    sessionManager: SessionManager,
    apiClient: ApiClient,
    riderProfile: RiderProfile?,
    onLogout: () -> Unit,
    onProfileUpdated: (RiderProfile) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(OptionsSubPage.HUB) }
    var showServerDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var activeProfile by remember(riderProfile) { mutableStateOf(riderProfile) }

    // Intercept hardware or system back gesture to navigate from sub-page back to HUB
    BackHandler(enabled = currentPage != OptionsSubPage.HUB) {
        currentPage = when (currentPage) {
            OptionsSubPage.BUSINESS_WIZARD -> OptionsSubPage.BUSINESS
            OptionsSubPage.EMAIL_VERIFICATION -> OptionsSubPage.ACCOUNT
            else -> OptionsSubPage.HUB
        }
    }

    // Dynamic state synced with session
    var fullName by remember { mutableStateOf(sessionManager.getFullName().ifBlank { sessionManager.getUsername() }) }
    var username by remember { mutableStateOf(sessionManager.getUsername()) }
    var phone by remember { mutableStateOf(sessionManager.getPhone()) }
    var email by remember { mutableStateOf(sessionManager.getEmail()) }
    var verificationEmail by remember { mutableStateOf(email) }
    var isEmailVerified by remember { mutableStateOf(sessionManager.isEmailVerified()) }
    var isPhoneVerified by remember { mutableStateOf(sessionManager.isPhoneVerified()) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun refreshOptionsData() {
        if (isRefreshing) return
        scope.launch {
            isRefreshing = true
            try {
                val userRes = apiClient.getCurrentUser()
                userRes.getOrNull()?.let { updated ->
                    fullName = "${updated.firstName} ${updated.lastName}".trim().ifBlank { updated.username }
                    username = updated.username
                    phone = updated.phone.orEmpty()
                    email = updated.email
                    isEmailVerified = updated.isVerified
                    isPhoneVerified = updated.isPhoneVerified
                }
                val riderRes = apiClient.getRiderProfile()
                riderRes.getOrNull()?.let { updatedRider ->
                    activeProfile = updatedRider
                    onProfileUpdated(updatedRider)
                }
            } finally {
                isRefreshing = false
            }
        }
    }

    if (showServerDialog) {
        ServerConfigDialog(
            sessionManager = sessionManager,
            apiClient = apiClient,
            onDismiss = { showServerDialog = false },
            onSaved = { /* refreshed */ }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = BansiCrimsonDark,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign Out of Fleet?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                }
            },
            text = {
                Text(
                    text = "You will be set offline and won't receive incoming delivery offers until you log back in.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                BansiPrimaryButton(
                    text = "Sign Out",
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    backgroundColor = BansiCrimsonDark,
                    modifier = Modifier.width(120.dp)
                )
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = SurfaceLight
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                if (targetState != OptionsSubPage.HUB) {
                    slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width / 3 }
                } else {
                    slideInHorizontally { width -> -width / 3 } togetherWith slideOutHorizontally { width -> width }
                }
            },
            label = "options_subpage_animation"
        ) { targetPage ->
            when (targetPage) {
                OptionsSubPage.HUB -> {
                    OptionsHubContent(
                        fullName = fullName,
                        username = username,
                        phone = phone,
                        email = email,
                        isEmailVerified = isEmailVerified,
                        isPhoneVerified = isPhoneVerified,
                        riderProfile = activeProfile,
                        onNavigateToAccount = { currentPage = OptionsSubPage.ACCOUNT },
                        onNavigateToBusiness = { currentPage = OptionsSubPage.BUSINESS },
                        onNavigateToReviews = { currentPage = OptionsSubPage.REVIEWS },
                        onNavigateToPayment = { currentPage = OptionsSubPage.PAYMENT },
                        onNavigateToContact = { currentPage = OptionsSubPage.CONTACT },
                        onNavigateToPreferences = { currentPage = OptionsSubPage.PREFERENCES },
                        onOpenServerConfig = { showServerDialog = true },
                        onLogoutClick = { showLogoutDialog = true },
                        isRefreshing = isRefreshing,
                        onRefresh = { refreshOptionsData() }
                    )
                }

                OptionsSubPage.ACCOUNT -> {
                    OptionsAccountSettingsSection(
                        sessionManager = sessionManager,
                        apiClient = apiClient,
                        onBackClick = { currentPage = OptionsSubPage.HUB },
                        onNavigateToEmailVerification = { targetEmail ->
                            verificationEmail = targetEmail
                            currentPage = OptionsSubPage.EMAIL_VERIFICATION
                        },
                        onProfileUpdated = { updated ->
                            fullName = "${updated.firstName} ${updated.lastName}".trim()
                            phone = updated.phone.orEmpty()
                            email = updated.email
                            isEmailVerified = updated.isVerified
                            isPhoneVerified = updated.isPhoneVerified
                        }
                    )
                }

                OptionsSubPage.EMAIL_VERIFICATION -> {
                    OptionsEmailVerificationSection(
                        email = verificationEmail.ifBlank { email },
                        sessionManager = sessionManager,
                        apiClient = apiClient,
                        onBackClick = { currentPage = OptionsSubPage.ACCOUNT },
                        onVerified = { updated ->
                            isEmailVerified = true
                            if (updated != null) {
                                fullName = "${updated.firstName} ${updated.lastName}".trim()
                                phone = updated.phone.orEmpty()
                                email = updated.email
                                isEmailVerified = updated.isVerified
                                isPhoneVerified = updated.isPhoneVerified
                            }
                        },
                        onDoneClick = { currentPage = OptionsSubPage.ACCOUNT }
                    )
                }

                OptionsSubPage.BUSINESS -> {
                    OptionsBusinessSettingsSection(
                        riderProfile = activeProfile,
                        onBackClick = { currentPage = OptionsSubPage.HUB },
                        onEditBusinessSettingsClick = { currentPage = OptionsSubPage.BUSINESS_WIZARD }
                    )
                }

                OptionsSubPage.BUSINESS_WIZARD -> {
                    OptionsBusinessWizardSection(
                        apiClient = apiClient,
                        currentProfile = activeProfile,
                        onBackClick = { currentPage = OptionsSubPage.BUSINESS },
                        onSuccess = { updated ->
                            activeProfile = updated
                            onProfileUpdated(updated)
                            currentPage = OptionsSubPage.BUSINESS
                        }
                    )
                }

                OptionsSubPage.REVIEWS -> {
                    OptionsRiderReviewsSection(
                        apiClient = apiClient,
                        onBackClick = { currentPage = OptionsSubPage.HUB }
                    )
                }

                OptionsSubPage.PAYMENT -> {
                    OptionsPaymentMethodsSection(
                        mpesaPhone = phone,
                        onBackClick = { currentPage = OptionsSubPage.HUB }
                    )
                }

                OptionsSubPage.CONTACT -> {
                    OptionsContactUsSection(
                        onBackClick = { currentPage = OptionsSubPage.HUB }
                    )
                }

                OptionsSubPage.PREFERENCES -> {
                    OptionsPreferencesSection(
                        sessionManager = sessionManager,
                        onBackClick = { currentPage = OptionsSubPage.HUB }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionsHubContent(
    fullName: String,
    username: String,
    phone: String,
    email: String,
    isEmailVerified: Boolean,
    isPhoneVerified: Boolean,
    riderProfile: RiderProfile?,
    onNavigateToAccount: () -> Unit,
    onNavigateToBusiness: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onNavigateToPayment: () -> Unit,
    onNavigateToContact: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onOpenServerConfig: () -> Unit,
    onLogoutClick: () -> Unit,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Sticky Immersive Header (28.dp top padding)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ManageAccounts,
                        contentDescription = null,
                        tint = RoyalNavy,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Profile & Options",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                    Text(
                        text = "Fleet settings, payouts and support desk",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            IconButton(
                onClick = onRefresh,
                enabled = !isRefreshing
            ) {
                if (isRefreshing) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = RoyalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Profile",
                        tint = TextSecondary
                    )
                }
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OptionsHubSection(
                    fullName = fullName,
                    username = username,
                    phone = phone,
                    email = email,
                    isEmailVerified = isEmailVerified,
                    isPhoneVerified = isPhoneVerified,
                    riderProfile = riderProfile,
                    onNavigateToAccount = onNavigateToAccount,
                    onNavigateToBusiness = onNavigateToBusiness,
                    onNavigateToReviews = onNavigateToReviews,
                    onNavigateToPayment = onNavigateToPayment,
                    onNavigateToContact = onNavigateToContact,
                    onNavigateToPreferences = onNavigateToPreferences,
                    onOpenServerConfig = onOpenServerConfig,
                    onLogoutClick = onLogoutClick
                )

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
