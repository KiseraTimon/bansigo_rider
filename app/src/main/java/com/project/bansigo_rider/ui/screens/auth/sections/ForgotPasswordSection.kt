// app/src/main/java/com/project/bansigo_rider/ui/screens/auth/sections/ForgotPasswordSection.kt
package com.project.bansigo_rider.ui.screens.auth.sections

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.ui.components.BansiPasswordField
import com.project.bansigo_rider.ui.components.BansiPrimaryButton
import com.project.bansigo_rider.ui.components.BansiTextField
import com.project.bansigo_rider.ui.components.ContactInfoIllustration
import com.project.bansigo_rider.ui.components.OtpCodeInput
import com.project.bansigo_rider.ui.theme.*

@Composable
fun ForgotPasswordSection(
    email: String,
    onEmailChange: (String) -> Unit,
    resetCode: String,
    onResetCodeChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmNewPassword: String,
    onConfirmNewPasswordChange: (String) -> Unit,
    isCodeSent: Boolean,
    isLoading: Boolean,
    onRequestCode: () -> Unit,
    onConfirmReset: () -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (!isCodeSent) "Reset Password" else "Enter Reset Code",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (!isCodeSent)
                    "Enter your registered email address and we will send you a password recovery code."
                else
                    "We sent a 6-digit verification code to $email. Enter the code and choose your new password.",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Center Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                ContactInfoIllustration(width = 180.dp, height = 135.dp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!isCodeSent) {
                BansiTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Registered Email Address",
                    placeholder = "rider@example.com",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onDone = {
                            if (email.isNotBlank()) onRequestCode()
                        }
                    )
                )
            } else {
                Text(
                    text = "Verification Code",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OtpCodeInput(
                    code = resetCode,
                    onCodeChange = onResetCodeChange,
                    length = 6
                )

                Spacer(modifier = Modifier.height(16.dp))

                BansiPasswordField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
                    label = "New Password",
                    placeholder = "At least 6 characters",
                    leadingIcon = Icons.Default.Lock,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                val match = newPassword == confirmNewPassword || confirmNewPassword.isEmpty()
                BansiPasswordField(
                    value = confirmNewPassword,
                    onValueChange = onConfirmNewPasswordChange,
                    label = "Confirm New Password",
                    placeholder = "Re-enter new password",
                    leadingIcon = Icons.Default.Lock,
                    isError = !match,
                    errorMessage = if (!match) "Passwords do not match" else null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onDone = {
                            if (resetCode.length >= 4 && newPassword.length >= 6 && newPassword == confirmNewPassword) {
                                onConfirmReset()
                            }
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom Actions area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 8.dp)
        ) {
            if (!isCodeSent) {
                BansiPrimaryButton(
                    text = "Send Reset Code",
                    showArrow = true,
                    onClick = onRequestCode,
                    enabled = email.isNotBlank(),
                    isLoading = isLoading
                )
            } else {
                BansiPrimaryButton(
                    text = "Update Password",
                    showArrow = true,
                    onClick = onConfirmReset,
                    enabled = resetCode.length >= 4 && newPassword.length >= 6 && newPassword == confirmNewPassword,
                    isLoading = isLoading
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Back to Sign In",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = BansiGreen,
                    modifier = Modifier.clickable(onClick = onBackToLogin)
                )
            }
        }
    }
}
