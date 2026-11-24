// app/src/main/java/com/project/bansigo_rider/ui/screens/auth/sections/RegisterPasswordSection.kt
package com.project.bansigo_rider.ui.screens.auth.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.project.bansigo_rider.ui.components.ChoosePasswordIllustration
import com.project.bansigo_rider.ui.theme.*

@Composable
fun RegisterPasswordSection(
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val passwordsMatch = password == confirmPassword || confirmPassword.isEmpty()
    val isFormValid = password.length >= 6 && password == confirmPassword

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Scrollable content area
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
                text = "Choose a Password",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "For security & safety please choose a strong password for your account.",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Center Password & Security Illustration (Matching Inspo 2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                ChoosePasswordIllustration(width = 190.dp, height = 135.dp)
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Password Input
            BansiPasswordField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "Enter at least 6 characters",
                leadingIcon = Icons.Default.Lock,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Input
            BansiPasswordField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Confirm Password",
                placeholder = "Re-enter your password",
                leadingIcon = Icons.Default.Lock,
                isError = !passwordsMatch,
                errorMessage = if (!passwordsMatch) "Passwords do not match" else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onDone = {
                        if (isFormValid) {
                            onSignUpClick()
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom CTA Button: MUST read "Sign Up" per requirement 4
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 8.dp)
        ) {
            BansiPrimaryButton(
                text = "Sign Up",
                showArrow = true,
                onClick = onSignUpClick,
                enabled = isFormValid,
                isLoading = isLoading
            )
        }
    }
}
