// app/src/main/java/com/project/bansigo_rider/ui/screens/auth/AuthScreen.kt
package com.project.bansigo_rider.ui.screens.auth

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.network.ApiClient
import com.project.bansigo_rider.core.storage.SessionManager
import com.project.bansigo_rider.ui.components.ServerConfigDialog
import com.project.bansigo_rider.ui.screens.auth.sections.ForgotPasswordSection
import com.project.bansigo_rider.ui.screens.auth.sections.LoginSection
import com.project.bansigo_rider.ui.screens.auth.sections.RegisterContactSection
import com.project.bansigo_rider.ui.screens.auth.sections.RegisterInfoSection
import com.project.bansigo_rider.ui.screens.auth.sections.RegisterPasswordSection
import com.project.bansigo_rider.ui.screens.splash.SplashScreen
import com.project.bansigo_rider.ui.theme.*
import kotlinx.coroutines.launch

enum class AuthStage {
    SPLASH,
    LOGIN,
    REGISTER_INFO,
    REGISTER_CONTACT,
    REGISTER_PASSWORD,
    FORGOT_PASSWORD
}

@Composable
fun AuthScreen(
    sessionManager: SessionManager,
    apiClient: ApiClient,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stage by remember { mutableStateOf(AuthStage.SPLASH) }
    var showServerDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Login Form State
    var loginUsernameOrEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }

    // Register Multi-Page State (3 pages: Info -> Contact -> Password)
    var regFirstName by remember { mutableStateOf("") }
    var regLastName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }

    // Forgot Password State
    var forgotEmail by remember { mutableStateOf("") }
    var forgotResetCode by remember { mutableStateOf("") }
    var forgotNewPassword by remember { mutableStateOf("") }
    var forgotConfirmPassword by remember { mutableStateOf("") }
    var isForgotCodeSent by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Handle Hardware Back Navigation
    BackHandler(enabled = stage != AuthStage.SPLASH && stage != AuthStage.LOGIN) {
        errorMessage = null
        stage = when (stage) {
            AuthStage.REGISTER_INFO -> AuthStage.LOGIN
            AuthStage.REGISTER_CONTACT -> AuthStage.REGISTER_INFO
            AuthStage.REGISTER_PASSWORD -> AuthStage.REGISTER_CONTACT
            AuthStage.FORGOT_PASSWORD -> AuthStage.LOGIN
            else -> AuthStage.LOGIN
        }
    }

    if (showServerDialog) {
        ServerConfigDialog(
            sessionManager = sessionManager,
            apiClient = apiClient,
            onDismiss = { showServerDialog = false },
            onSaved = { /* Reload configs */ }
        )
    }

    // 1. Splash Screen
    if (stage == AuthStage.SPLASH) {
        SplashScreen(
            onFinished = {
                errorMessage = null
                stage = AuthStage.LOGIN
            }
        )
        return
    }

    // 2. Main Authentication Frame
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(TopGlow, BackgroundLight, BackgroundLight)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (stage != AuthStage.LOGIN) {
                        IconButton(
                            onClick = {
                                errorMessage = null
                                stage = when (stage) {
                                    AuthStage.REGISTER_INFO -> AuthStage.LOGIN
                                    AuthStage.REGISTER_CONTACT -> AuthStage.REGISTER_INFO
                                    AuthStage.REGISTER_PASSWORD -> AuthStage.REGISTER_CONTACT
                                    AuthStage.FORGOT_PASSWORD -> AuthStage.LOGIN
                                    else -> AuthStage.LOGIN
                                }
                            },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF111827)
                            )
                        }
                    } else {
                        // Brand Monogram
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(RoyalNavy, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "B",
                                    color = SurfaceLight,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row {
                                    Text("Bansi", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = RoyalNavy)
                                    Text("Go", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = BansiCrimson)
                                }
                                Text("Rider Fleet", fontSize = 11.sp, color = Color(0xFF6B7280))
                            }
                        }
                    }

                    // Server Configuration Icon Button (Persistent Session Switcher)
                    IconButton(
                        onClick = { showServerDialog = true },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dns,
                            contentDescription = "Server Settings",
                            tint = Color(0xFF6B7280)
                        )
                    }
                }

                // Error Notice Banner
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BansiCrimsonLight, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = BansiCrimsonDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = errorMessage!!,
                            color = BansiCrimsonDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Animated Screen Content Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    AnimatedContent(
                        targetState = stage,
                        transitionSpec = {
                            slideInHorizontally { width -> width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> -width } + fadeOut()
                        },
                        label = "authStageTransition"
                    ) { currentStage ->
                        when (currentStage) {
                            AuthStage.LOGIN -> {
                                LoginSection(
                                    usernameOrEmail = loginUsernameOrEmail,
                                    onUsernameOrEmailChange = { loginUsernameOrEmail = it },
                                    password = loginPassword,
                                    onPasswordChange = { loginPassword = it },
                                    isLoading = isLoading,
                                    onLoginClick = {
                                        isLoading = true
                                        errorMessage = null
                                        scope.launch {
                                            val result = apiClient.login(loginUsernameOrEmail, loginPassword)
                                            isLoading = false
                                            if (result.isSuccess) {
                                                onAuthSuccess()
                                            } else {
                                                errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Invalid credentials. Please verify."
                                            }
                                        }
                                    },
                                    onForgotPasswordClick = {
                                        errorMessage = null
                                        stage = AuthStage.FORGOT_PASSWORD
                                    },
                                    onNavigateToRegister = {
                                        errorMessage = null
                                        stage = AuthStage.REGISTER_INFO
                                    }
                                )
                            }

                            AuthStage.REGISTER_INFO -> {
                                RegisterInfoSection(
                                    firstName = regFirstName,
                                    onFirstNameChange = { regFirstName = it },
                                    lastName = regLastName,
                                    onLastNameChange = { regLastName = it },
                                    onNextClick = {
                                        errorMessage = null
                                        stage = AuthStage.REGISTER_CONTACT
                                    }
                                )
                            }

                            AuthStage.REGISTER_CONTACT -> {
                                RegisterContactSection(
                                    email = regEmail,
                                    onEmailChange = { regEmail = it },
                                    phone = regPhone,
                                    onPhoneChange = { regPhone = it },
                                    onNextClick = {
                                        errorMessage = null
                                        stage = AuthStage.REGISTER_PASSWORD
                                    }
                                )
                            }

                            AuthStage.REGISTER_PASSWORD -> {
                                RegisterPasswordSection(
                                    password = regPassword,
                                    onPasswordChange = { regPassword = it },
                                    confirmPassword = regConfirmPassword,
                                    onConfirmPasswordChange = { regConfirmPassword = it },
                                    isLoading = isLoading,
                                    onSignUpClick = {
                                        isLoading = true
                                        errorMessage = null
                                        scope.launch {
                                            // 1. Sign up on bansigo_server (auto-generating username on server)
                                            val signupRes = apiClient.signup(
                                                firstName = regFirstName,
                                                lastName = regLastName,
                                                email = regEmail,
                                                phone = regPhone,
                                                password = regPassword
                                            )
                                            if (signupRes.isSuccess) {
                                                // 2. Auto-login on bansigo_server using email & password
                                                val loginRes = apiClient.login(regEmail, regPassword)
                                                isLoading = false
                                                if (loginRes.isSuccess) {
                                                    // 3. Immediately enter app - NO REDIRECTION TO VERIFICATION PAGE
                                                    onAuthSuccess()
                                                } else {
                                                    stage = AuthStage.LOGIN
                                                    loginUsernameOrEmail = regEmail
                                                }
                                            } else {
                                                isLoading = false
                                                errorMessage = signupRes.exceptionOrNull()?.localizedMessage ?: "Registration failed"
                                            }
                                        }
                                    }
                                )
                            }

                            AuthStage.FORGOT_PASSWORD -> {
                                ForgotPasswordSection(
                                    email = forgotEmail,
                                    onEmailChange = { forgotEmail = it },
                                    resetCode = forgotResetCode,
                                    onResetCodeChange = { forgotResetCode = it },
                                    newPassword = forgotNewPassword,
                                    onNewPasswordChange = { forgotNewPassword = it },
                                    confirmNewPassword = forgotConfirmPassword,
                                    onConfirmNewPasswordChange = { forgotConfirmPassword = it },
                                    isCodeSent = isForgotCodeSent,
                                    isLoading = isLoading,
                                    onRequestCode = {
                                        isLoading = true
                                        errorMessage = null
                                        scope.launch {
                                            val res = apiClient.requestPasswordReset(forgotEmail)
                                            isLoading = false
                                            if (res.isSuccess) {
                                                isForgotCodeSent = true
                                            } else {
                                                errorMessage = res.exceptionOrNull()?.localizedMessage ?: "Failed to send reset code"
                                            }
                                        }
                                    },
                                    onConfirmReset = {
                                        isLoading = true
                                        errorMessage = null
                                        scope.launch {
                                            val res = apiClient.confirmPasswordReset(
                                                email = forgotEmail,
                                                code = forgotResetCode,
                                                newPassword = forgotNewPassword
                                            )
                                            isLoading = false
                                            if (res.isSuccess) {
                                                loginUsernameOrEmail = forgotEmail
                                                stage = AuthStage.LOGIN
                                            } else {
                                                errorMessage = res.exceptionOrNull()?.localizedMessage ?: "Password reset failed"
                                            }
                                        }
                                    },
                                    onBackToLogin = {
                                        errorMessage = null
                                        stage = AuthStage.LOGIN
                                    }
                                )
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}
