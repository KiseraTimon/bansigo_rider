// app/src/main/java/com/project/bansigo_rider/ui/screens/options/sections/OptionsAccountSettingsSection.kt
package com.project.bansigo_rider.ui.screens.options.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.UserProfile
import com.project.bansigo_rider.core.network.ApiClient
import com.project.bansigo_rider.core.storage.SessionManager
import com.project.bansigo_rider.ui.components.BansiPrimaryButton
import com.project.bansigo_rider.ui.components.BansiTextField
import com.project.bansigo_rider.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OptionsAccountSettingsSection(
    sessionManager: SessionManager,
    apiClient: ApiClient,
    onBackClick: () -> Unit,
    onNavigateToEmailVerification: (String) -> Unit = {},
    onProfileUpdated: (UserProfile) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf(sessionManager.getFirstName()) }
    var lastName by remember { mutableStateOf(sessionManager.getLastName()) }
    var username by remember { mutableStateOf(sessionManager.getUsername()) }
    var email by remember { mutableStateOf(sessionManager.getEmail()) }
    var phone by remember { mutableStateOf(sessionManager.getPhone()) }

    var isEmailVerified by remember { mutableStateOf(sessionManager.isEmailVerified()) }
    var isPhoneVerified by remember { mutableStateOf(sessionManager.isPhoneVerified()) }

    var isSaving by remember { mutableStateOf(false) }
    var isSendingOtp by remember { mutableStateOf(false) }

    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Sub-page Top Navigation Bar
        OptionsSubPageTopBar(
            title = "Account Settings",
            subtitle = "Personal details & contact verification",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Picture Preview Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val initials = rememberInitials(firstName, lastName, username)

                    // Profile Avatar Box with Camera Icon Outside (not clipped inside)
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Main Circular Avatar
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(RoyalNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }

                        // Camera icon pill placed OUTSIDE the clipped circle on top-right/bottom-end
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .background(BansiGreenDark, CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Upload profile picture",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$firstName $lastName".trim().ifBlank { username },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))

                        // Explicit username display next to upload avatar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(InputBackground, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AlternateEmail,
                                contentDescription = null,
                                tint = RoyalNavy,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = username.ifBlank { "rider_account" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RoyalNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap to update avatar picture",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Warning Banner for Unverified State
            if (!isEmailVerified || !isPhoneVerified) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEF3C7), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "Alert",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Contact Verification Required",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Bansi dispatch protocol requires verified contact info. Modifying your phone or email will automatically require re-verification.",
                                fontSize = 12.sp,
                                color = Color(0xFFB45309),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Status message
            statusMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isError) Color(0xFFFEE2E2) else Color(0xFFECFDF5),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = msg,
                        fontSize = 12.sp,
                        color = if (isError) BansiCrimsonDark else Color(0xFF047857),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Edit Profile Form Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Personal Information",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Names row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            BansiTextField(
                                value = firstName,
                                onValueChange = { firstName = it },
                                label = "First Name *",
                                placeholder = "e.g. John"
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            BansiTextField(
                                value = lastName,
                                onValueChange = { lastName = it },
                                label = "Last Name *",
                                placeholder = "e.g. Ochieng"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Username field with @ prefix
                    BansiTextField(
                        value = username,
                        onValueChange = { username = it.filter { char -> char.isLetterOrDigit() || char == '_' } },
                        label = "Rider Handle / Username *",
                        placeholder = "e.g. rider_john",
                        leadingIcon = Icons.Default.AlternateEmail
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Phone Field + Verification Alert & Button
                    Column {
                        BansiTextField(
                            value = phone,
                            onValueChange = {
                                if (it != phone) {
                                    phone = it
                                    if (it != sessionManager.getPhone()) {
                                        isPhoneVerified = false
                                        sessionManager.setPhoneVerified(false)
                                    }
                                }
                            },
                            label = "Phone Number (M-Pesa registered) *",
                            placeholder = "07XXXXXXXX or +254...",
                            leadingIcon = Icons.Default.Phone,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isPhoneVerified) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Phone verified for dispatch & M-Pesa",
                                        fontSize = 11.sp,
                                        color = Color(0xFF059669)
                                    )
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Used for dispatch & M-Pesa payouts (save changes below)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email Field + Verification Alert & Button
                    Column {
                        BansiTextField(
                            value = email,
                            onValueChange = {
                                if (it != email) {
                                    email = it
                                    if (it != sessionManager.getEmail()) {
                                        isEmailVerified = false
                                        sessionManager.setEmailVerified(false)
                                    }
                                }
                            },
                            label = "Email Address *",
                            placeholder = "rider@domain.com",
                            leadingIcon = Icons.Default.Email,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isEmailVerified) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF059669),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Email verified",
                                        fontSize = 11.sp,
                                        color = Color(0xFF059669)
                                    )
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.WarningAmber,
                                        contentDescription = null,
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Email requires verification",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFD97706)
                                    )
                                }

                                Text(
                                    text = if (isSendingOtp) "Sending code..." else "Send Code",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalNavy,
                                    modifier = Modifier.clickable(enabled = !isSendingOtp) {
                                        if (email.isBlank()) {
                                            isError = true
                                            statusMessage = "Please enter your email address first"
                                            return@clickable
                                        }
                                        isSendingOtp = true
                                        statusMessage = null
                                        scope.launch {
                                            val res = apiClient.requestEmailVerification()
                                            isSendingOtp = false
                                            if (res.isSuccess) {
                                                onNavigateToEmailVerification(email.trim())
                                            } else {
                                                isError = true
                                                statusMessage = res.exceptionOrNull()?.message ?: "Could not send verification code"
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Save Button with strict blank validation
                    BansiPrimaryButton(
                        text = if (isSaving) "Saving Changes..." else "Save Changes",
                        icon = Icons.Default.Save,
                        onClick = {
                            if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || email.isBlank() || phone.isBlank()) {
                                isError = true
                                statusMessage = "All required fields (*) cannot be submitted blank."
                                return@BansiPrimaryButton
                            }

                            if (!email.contains("@") || !email.contains(".")) {
                                isError = true
                                statusMessage = "Please enter a valid email address."
                                return@BansiPrimaryButton
                            }

                            if (phone.trim().length < 9) {
                                isError = true
                                statusMessage = "Please enter a valid Kenyan phone number."
                                return@BansiPrimaryButton
                            }

                            isSaving = true
                            statusMessage = null
                            scope.launch {
                                val res = apiClient.updateProfile(
                                    firstName = firstName.trim(),
                                    lastName = lastName.trim(),
                                    username = username.trim(),
                                    email = email.trim(),
                                    phone = phone.trim()
                                )
                                isSaving = false
                                if (res.isSuccess) {
                                    isError = false
                                    val updatedUser = res.getOrNull()
                                    if (updatedUser != null) {
                                        onProfileUpdated(updatedUser)
                                        isEmailVerified = updatedUser.isVerified
                                    }
                                    statusMessage = "Account details saved successfully."
                                } else {
                                    isError = true
                                    statusMessage = res.exceptionOrNull()?.message ?: "Failed to update account details"
                                }
                            }
                        },
                        enabled = !isSaving,
                        backgroundColor = RoyalNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun rememberInitials(firstName: String, lastName: String, username: String): String {
    val f = firstName.trim().take(1).uppercase()
    val l = lastName.trim().take(1).uppercase()
    return if (f.isNotBlank() && l.isNotBlank()) {
        "$f$l"
    } else if (f.isNotBlank()) {
        f
    } else if (username.isNotBlank()) {
        username.take(2).uppercase()
    } else {
        "R"
    }
}
