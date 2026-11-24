// app/src/main/java/com/project/bansigo_rider/ui/screens/auth/sections/RegisterOtpSection.kt
package com.project.bansigo_rider.ui.screens.auth.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.ui.components.BansiPrimaryButton
import com.project.bansigo_rider.ui.components.OtpCodeInput
import com.project.bansigo_rider.ui.components.PhoneVerificationIllustration
import com.project.bansigo_rider.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun RegisterOtpSection(
    phoneNumber: String,
    otpCode: String,
    onOtpCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onChangePhoneClick: () -> Unit,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 30 seconds countdown timer for resend
    var countdown by remember { mutableIntStateOf(30) }

    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000)
            countdown -= 1
        }
    }

    // Mask phone number: e.g. 0712345678 -> 07XXXXXX78
    val maskedPhone = remember(phoneNumber) {
        val clean = phoneNumber.trim()
        if (clean.length >= 6) {
            val prefix = clean.take(2)
            val suffix = clean.takeLast(2)
            val maskedMiddle = "X".repeat((clean.length - 4).coerceAtLeast(4))
            "$prefix$maskedMiddle$suffix"
        } else {
            clean
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        // Top Phone & Leaf Illustration (Matching Inspo 3)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),
            contentAlignment = Alignment.Center
        ) {
            PhoneVerificationIllustration(width = 200.dp, height = 180.dp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Enter Verification Code",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We have sent SMS to:",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Text(
            text = maskedPhone,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(28.dp))

        // OTP Code Input Boxes (Matching Inspo 3)
        OtpCodeInput(
            code = otpCode,
            onCodeChange = onOtpCodeChange,
            length = 6,
            onCompleted = {
                onVerifyClick()
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Action Row: Resend OTP (left) & Change Phone Number (right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (countdown > 0) {
                Text(
                    text = "Resend OTP ($countdown s)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )
            } else {
                Text(
                    text = "Resend OTP",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentOrange,
                    modifier = Modifier.clickable {
                        countdown = 30
                        onResendClick()
                    }
                )
            }

            Text(
                text = "Change Phone Number",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                modifier = Modifier.clickable(onClick = onChangePhoneClick)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Next Button
        BansiPrimaryButton(
            text = "Next",
            showArrow = true,
            onClick = onVerifyClick,
            enabled = otpCode.length >= 4 // Allow 4-6 digits verification
        )
    }
}
