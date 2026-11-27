// app/src/main/java/com/project/bansigo_rider/ui/screens/options/sections/OptionsHubSection.kt
package com.project.bansigo_rider.ui.screens.options.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.ApprovalStatus
import com.project.bansigo_rider.core.models.RiderProfile
import com.project.bansigo_rider.ui.components.ApprovalStatusBadge
import com.project.bansigo_rider.ui.theme.*

@Composable
fun OptionsHubSection(
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
    modifier: Modifier = Modifier
) {
    val initials = rememberInitials(fullName, username)
    val hasUnverified = !isEmailVerified

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Rider Profile Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToAccount() }
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stylized Avatar with Initials
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(RoyalNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )

                        // Camera indicator icon
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(BansiGreenDark)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Name, Handle, Phone, and Approval Badge
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = fullName.ifBlank { username },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = phone.ifBlank { email },
                            fontSize = 13.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ApprovalStatusBadge(status = riderProfile?.approvalStatus ?: ApprovalStatus.PENDING)
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Edit Profile",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // If user has unverified details, show alert pill
                if (hasUnverified) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Unverified contact details detected. Tap to verify.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }
            }
        }

        // 2. Main Options Group: Account, Business, Payment
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Item 1: Account Settings
                OptionNavRow(
                    icon = Icons.Default.AccountCircle,
                    iconBg = Color(0xFFEFF6FF),
                    iconTint = RoyalNavy,
                    title = "Account Settings",
                    subtitle = "Personal details, phone & email verification",
                    badge = if (hasUnverified) "Action Needed" else null,
                    badgeColor = Color(0xFFD97706),
                    onClick = onNavigateToAccount
                )

                HorizontalDivider(color = BorderLight, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                // Item 2: Business Settings
                OptionNavRow(
                    icon = Icons.Default.BusinessCenter,
                    iconBg = Color(0xFFF0FDF4),
                    iconTint = BansiGreenDark,
                    title = "Business Settings",
                    subtitle = "National ID, licence & vehicle credentials",
                    onClick = onNavigateToBusiness
                )

                HorizontalDivider(color = BorderLight, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                // Item 3: Customer Ratings & Reviews
                val ratingBadge = riderProfile?.ratingAverage?.let {
                    String.format("%.1f ★ (%d)", it, riderProfile.ratingCount)
                }
                OptionNavRow(
                    icon = Icons.Default.Star,
                    iconBg = Color(0xFFFEF3C7),
                    iconTint = Color(0xFFD97706),
                    title = "Customer Ratings & Feedback",
                    subtitle = "View delivery ratings & customer comments",
                    badge = ratingBadge,
                    badgeColor = Color(0xFFD97706),
                    onClick = onNavigateToReviews
                )

                HorizontalDivider(color = BorderLight, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                // Item 4: Payment Methods
                OptionNavRow(
                    icon = Icons.Default.Payments,
                    iconBg = Color(0xFFF0FDF4),
                    iconTint = BansiGreenDark,
                    title = "Payment Methods",
                    subtitle = "M-Pesa B2C direct payout channel",
                    onClick = onNavigateToPayment
                )
            }
        }

        // 3. Support & Preferences Group
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Item 4: Contact Us
                OptionNavRow(
                    icon = Icons.Default.HeadsetMic,
                    iconBg = Color(0xFFEFF6FF),
                    iconTint = RoyalNavy,
                    title = "Contact Us",
                    subtitle = "Direct hotline, WhatsApp & supermarket hub",
                    onClick = onNavigateToContact
                )

                HorizontalDivider(color = BorderLight, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                // Item 5: Preferences
                OptionNavRow(
                    icon = Icons.Default.Tune,
                    iconBg = Color(0xFFF3F4F6),
                    iconTint = TextPrimary,
                    title = "App Preferences",
                    subtitle = "Sound chime alerts & low-bandwidth mode",
                    onClick = onNavigateToPreferences
                )

                HorizontalDivider(color = BorderLight, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                // Item 6: Server Environment Config
                OptionNavRow(
                    icon = Icons.Default.Dns,
                    iconBg = Color(0xFFF3F4F6),
                    iconTint = RoyalNavy,
                    title = "Server Configuration",
                    subtitle = "Switch environment & ping latency test",
                    onClick = onOpenServerConfig
                )
            }
        }

        // 4. Session & Logout
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OptionNavRow(
                icon = Icons.AutoMirrored.Filled.Logout,
                iconBg = Color(0xFFFEE2E2),
                iconTint = BansiCrimsonDark,
                title = "Sign Out",
                subtitle = "Go offline and end current session",
                titleColor = BansiCrimsonDark,
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
private fun OptionNavRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    titleColor: Color = TextPrimary,
    badge: String? = null,
    badgeColor: Color = BansiGreenDark,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor
                    )
                    badge?.let {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = it,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun rememberInitials(fullName: String, username: String): String {
    val parts = fullName.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
        parts.size == 1 && parts[0].length >= 2 -> parts[0].take(2).uppercase()
        parts.size == 1 -> parts[0].take(1).uppercase()
        username.isNotBlank() -> username.take(2).uppercase()
        else -> "R"
    }
}
