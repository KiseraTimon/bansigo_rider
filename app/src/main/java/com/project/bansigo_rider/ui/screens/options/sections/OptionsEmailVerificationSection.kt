// app/src/main/java/com/project/bansigo_rider/ui/screens/options/sections/OptionsEmailVerificationSection.kt
package com.project.bansigo_rider.ui.screens.options.sections

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.UserProfile
import com.project.bansigo_rider.core.network.ApiClient
import com.project.bansigo_rider.core.storage.SessionManager
import com.project.bansigo_rider.ui.components.BansiPrimaryButton
import com.project.bansigo_rider.ui.components.EmailVerificationIllustration
import com.project.bansigo_rider.ui.components.EmailVerifiedSuccessIllustration
import com.project.bansigo_rider.ui.components.OtpCodeInput
import com.project.bansigo_rider.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OptionsEmailVerificationSection(
    email: String,
    sessionManager: SessionManager,
    apiClient: ApiClient,
    onBackClick: () -> Unit,
    onVerified: (UserProfile?) -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // step 1 = Code entry & resend, step 2 = Confirmation success illustration
    var step by remember { mutableIntStateOf(1) }
    var otpCode by remember { mutableStateOf("") }
    var countdown by remember { mutableIntStateOf(30) }
    var isConfirming by remember { mutableStateOf(false) }
    var isResending by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>("We sent a 6-digit verification code to your email.") }
    var isError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(countdown, step) {
        if (step == 1 && countdown > 0) {
            delay(1000)
            countdown -= 1
        }
    }

    BackHandler {
        if (step == 2) {
            onDoneClick()
        } else {
            onBackClick()
        }
    }

    fun submitCode(codeToVerify: String = otpCode) {
        if (codeToVerify.trim().length < 6) {
            isError = true
            statusMessage = "Please enter the full 6-digit verification code."
            return
        }
        if (isConfirming) return
        isConfirming = true
        statusMessage = null
        isError = false

        scope.launch {
            val res = apiClient.confirmEmailVerification(codeToVerify.trim())
            isConfirming = false
            if (res.isSuccess) {
                sessionManager.setEmailVerified(true)
                onVerified(res.getOrNull())
                step = 2
            } else {
                isError = true
                statusMessage = res.exceptionOrNull()?.message ?: "Invalid or expired code. Please try again."
            }
        }
    }

    fun resendCode() {
        if (isResending || countdown > 0) return
        isResending = true
        statusMessage = null
        isError = false

        scope.launch {
            val res = apiClient.requestEmailVerification()
            isResending = false
            if (res.isSuccess) {
                countdown = 30
                otpCode = ""
                isError = false
                statusMessage = "A new 6-digit code has been sent to $email."
            } else {
                isError = true
                statusMessage = res.exceptionOrNull()?.message ?: "Could not resend verification code."
            }
        }
    }

    AnimatedContent(
        targetState = step,
        transitionSpec = {
            slideInHorizontally { width -> width } togetherWith slideOutHorizontally { width -> -width / 3 }
        },
        label = "email_verification_step_animation",
        modifier = modifier.fillMaxSize()
    ) { currentStep ->
        if (currentStep == 1) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
            ) {
                OptionsSubPageTopBar(
                    title = "Verify Email",
                    subtitle = "Enter the 6-digit confirmation code",
                    onBackClick = onBackClick
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmailVerificationIllustration(width = 200.dp, height = 160.dp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Enter Verification Code",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "We have sent a 6-digit code to:",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = email,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OtpCodeInput(
                        code = otpCode,
                        onCodeChange = { otpCode = it },
                        length = 6,
                        onCompleted = { completed -> submitCode(completed) }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (countdown > 0) {
                            Text(
                                text = "Resend code in ${countdown}s",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMuted
                            )
                        } else {
                            Text(
                                text = if (isResending) "Sending..." else "Resend Code",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy,
                                modifier = Modifier.clickable(enabled = !isResending) {
                                    resendCode()
                                }
                            )
                        }

                        Text(
                            text = "Change Email",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            modifier = Modifier.clickable(onClick = onBackClick)
                        )
                    }

                    statusMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(18.dp))
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isError) Color(0xFFFEF2F2) else Color(0xFFEFF6FF)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isError) Icons.Default.ErrorOutline else Icons.Default.MarkEmailRead,
                                    contentDescription = null,
                                    tint = if (isError) BansiCrimsonDark else RoyalNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    fontSize = 12.sp,
                                    color = if (isError) BansiCrimsonDark else RoyalNavy,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceLight)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    BansiPrimaryButton(
                        text = if (isConfirming) "Verifying..." else "Verify Email",
                        showArrow = true,
                        onClick = { submitCode() },
                        enabled = otpCode.length == 6 && !isConfirming,
                        backgroundColor = BansiGreenDark
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
            ) {
                OptionsSubPageTopBar(
                    title = "Email Verified",
                    subtitle = "Verification complete",
                    onBackClick = onDoneClick
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    EmailVerifiedSuccessIllustration(width = 230.dp, height = 200.dp)

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BansiGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Email Confirmed!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavy
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Your email address ($email) has been verified. You will now receive fleet updates, payout receipts, and security alerts here.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 21.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceLight)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    BansiPrimaryButton(
                        text = "Back to Account",
                        showArrow = true,
                        onClick = onDoneClick,
                        backgroundColor = BansiGreen
                    )
                }
            }
        }
    }
}
