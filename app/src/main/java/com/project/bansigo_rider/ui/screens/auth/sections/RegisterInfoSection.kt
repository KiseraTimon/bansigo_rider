// app/src/main/java/com/project/bansigo_rider/ui/screens/auth/sections/RegisterInfoSection.kt
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.ui.components.BansiPrimaryButton
import com.project.bansigo_rider.ui.components.BansiTextField
import com.project.bansigo_rider.ui.components.PersonalInfoIllustration
import com.project.bansigo_rider.ui.theme.*

@Composable
fun RegisterInfoSection(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

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
                text = "Your Information",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Please enter your first and last name to get started as a BansiGo fleet delivery rider.",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Center Illustration (Replaced image upload with PersonalInfoIllustration)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                PersonalInfoIllustration(width = 190.dp, height = 135.dp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // First Name Field
            BansiTextField(
                value = firstName,
                onValueChange = onFirstNameChange,
                label = "First Name",
                placeholder = "e.g. John",
                leadingIcon = Icons.Default.Person,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Last Name Field
            BansiTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                label = "Last Name",
                placeholder = "e.g. Ochieng",
                leadingIcon = Icons.Default.Person,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onDone = {
                        if (firstName.isNotBlank() && lastName.isNotBlank()) {
                            onNextClick()
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom CTA Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 8.dp)
        ) {
            BansiPrimaryButton(
                text = "Next",
                showArrow = true,
                onClick = onNextClick,
                enabled = firstName.isNotBlank() && lastName.isNotBlank()
            )
        }
    }
}
