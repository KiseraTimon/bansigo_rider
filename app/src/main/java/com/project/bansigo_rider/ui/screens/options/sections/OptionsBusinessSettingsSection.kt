// app/src/main/java/com/project/bansigo_rider/ui/screens/options/sections/OptionsBusinessSettingsSection.kt
package com.project.bansigo_rider.ui.screens.options.sections

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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.ApprovalStatus
import com.project.bansigo_rider.core.models.RiderProfile
import com.project.bansigo_rider.ui.components.ApprovalStatusBadge
import com.project.bansigo_rider.ui.components.BansiPrimaryButton
import com.project.bansigo_rider.ui.theme.*

@Composable
fun OptionsBusinessSettingsSection(
    riderProfile: RiderProfile?,
    onBackClick: () -> Unit,
    onEditBusinessSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        OptionsSubPageTopBar(
            title = "Business Settings",
            subtitle = "Vetting credentials & vehicle records",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Approval Status Header Card
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Fleet Vetting Status",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavy
                        )
                        Text(
                            text = "Reviewed by Migori dispatch administration",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    ApprovalStatusBadge(status = riderProfile?.approvalStatus ?: ApprovalStatus.PENDING)
                }
            }

            // Identification & Licencing Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Rider Identification & Licencing",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // National ID
                    BusinessDetailItem(
                        icon = Icons.Default.Badge,
                        label = "National ID Number",
                        value = riderProfile?.nationalIdNumber ?: "Not recorded / On file",
                        isMuted = riderProfile?.nationalIdNumber.isNullOrBlank()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Driving Licence
                    BusinessDetailItem(
                        icon = Icons.Default.DriveEta,
                        label = "Driving Licence Number",
                        value = riderProfile?.licenceNumber ?: "Verified in administrative console",
                        isMuted = riderProfile?.licenceNumber.isNullOrBlank()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Expiry Date
                    BusinessDetailItem(
                        icon = Icons.Default.CalendarToday,
                        label = "Licence Expiry Date",
                        value = riderProfile?.licenceExpiry ?: "N/A (Bicycle or Fleet Record)",
                        isMuted = riderProfile?.licenceExpiry.isNullOrBlank()
                    )
                }
            }

            // Vehicle Specifications Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Vehicle Specifications",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Vehicle Type
                    BusinessDetailItem(
                        icon = Icons.Default.TwoWheeler,
                        label = "Fleet Vehicle Type",
                        value = riderProfile?.vehicleType?.label ?: "Motorcycle (Boda Boda)"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Number Plate
                    BusinessDetailItem(
                        icon = Icons.Default.Badge,
                        label = "Registration Number Plate",
                        value = riderProfile?.vehiclePlate ?: "Pending Documentation",
                        isMuted = riderProfile?.vehiclePlate.isNullOrBlank()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Description / Color / Model
                    BusinessDetailItem(
                        icon = Icons.Default.Description,
                        label = "Vehicle Model & Cargo Carrier",
                        value = riderProfile?.vehicleDescription ?: "Red Boxer 150cc delivery carrier",
                        isMuted = riderProfile?.vehicleDescription.isNullOrBlank()
                    )
                }
            }

            // Notice Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = RoyalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Modifying your vehicle plate or national ID resets your vetting status to Pending until validated by Bansi dispatch supervisors.",
                        fontSize = 12.sp,
                        color = RoyalNavy,
                        lineHeight = 16.sp
                    )
                }
            }

            // Edit Credentials Button
            BansiPrimaryButton(
                text = if (riderProfile == null || riderProfile.vehiclePlate.isNullOrBlank()) {
                    "Submit Business Credentials"
                } else {
                    "Edit Business Credentials"
                },
                icon = Icons.Default.Edit,
                onClick = onEditBusinessSettingsClick,
                backgroundColor = RoyalNavy
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BusinessDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    isMuted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(InputBackground, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = RoyalNavy,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isMuted) TextSecondary else TextPrimary
                )
            }
        }
    }
}
