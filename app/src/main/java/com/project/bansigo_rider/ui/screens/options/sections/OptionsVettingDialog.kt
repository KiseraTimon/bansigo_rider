// app/src/main/java/com/project/bansigo_rider/ui/screens/options/sections/OptionsVettingDialog.kt
package com.project.bansigo_rider.ui.screens.options.sections

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.project.bansigo_rider.core.models.RiderApplyRequest
import com.project.bansigo_rider.core.models.RiderProfile
import com.project.bansigo_rider.core.models.VehicleType
import com.project.bansigo_rider.core.network.ApiClient
import com.project.bansigo_rider.ui.components.BansiPrimaryButton
import com.project.bansigo_rider.ui.components.BansiTextField
import com.project.bansigo_rider.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OptionsVettingDialog(
    apiClient: ApiClient,
    currentProfile: RiderProfile?,
    onDismiss: () -> Unit,
    onSuccess: (RiderProfile) -> Unit
) {
    val context = LocalContext.current
    var nationalId by remember { mutableStateOf(currentProfile?.nationalIdNumber.orEmpty()) }
    var vehicleType by remember { mutableStateOf(currentProfile?.vehicleType ?: VehicleType.MOTORCYCLE) }
    var vehiclePlate by remember { mutableStateOf(currentProfile?.vehiclePlate.orEmpty()) }
    var vehicleDescription by remember { mutableStateOf(currentProfile?.vehicleDescription.orEmpty()) }
    var licenceNumber by remember { mutableStateOf(currentProfile?.licenceNumber.orEmpty()) }
    var licenceExpiry by remember { mutableStateOf(currentProfile?.licenceExpiry.orEmpty()) }

    // Uploaded document URLs
    var idCardFrontUrl by remember { mutableStateOf(currentProfile?.idCardFrontUrl.orEmpty()) }
    var idCardBackUrl by remember { mutableStateOf(currentProfile?.idCardBackUrl.orEmpty()) }
    var drivingLicenceUrl by remember { mutableStateOf(currentProfile?.drivingLicenceUrl.orEmpty()) }
    var vehicleProofUrl by remember { mutableStateOf(currentProfile?.vehicleProofUrl.orEmpty()) }

    // Upload loading flags
    var uploadingDocType by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Helper to upload selected image
    fun uploadUri(uri: Uri, docType: String) {
        uploadingDocType = docType
        errorMessage = null
        scope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: ByteArray(0)
                inputStream?.close()

                if (bytes.isEmpty()) {
                    errorMessage = "Unable to read selected document"
                    uploadingDocType = null
                    return@launch
                }

                val fileName = "${docType}_${System.currentTimeMillis()}.jpg"
                val res = apiClient.uploadRiderDocument(docType, fileName, bytes, "image/jpeg")
                uploadingDocType = null

                if (res.isSuccess) {
                    val url = res.getOrNull().orEmpty()
                    when (docType) {
                        "id_front" -> idCardFrontUrl = url
                        "id_back" -> idCardBackUrl = url
                        "licence" -> drivingLicenceUrl = url
                        "vehicle_proof" -> vehicleProofUrl = url
                    }
                } else {
                    errorMessage = res.exceptionOrNull()?.message ?: "Upload failed"
                }
            } catch (e: Exception) {
                uploadingDocType = null
                errorMessage = e.message ?: "Failed to upload document"
            }
        }
    }

    // Image pickers for each doc type
    val idFrontPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadUri(it, "id_front") }
    }
    val idBackPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadUri(it, "id_back") }
    }
    val licencePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadUri(it, "licence") }
    }
    val vehiclePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { uploadUri(it, "vehicle_proof") }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Rider Vetting & Documents",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavy
                        )
                        Text(
                            text = "Upload ID cards, licence & vehicle proof for administration vetting",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error Notice Banner
                if (!errorMessage.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BansiCrimsonLight, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = errorMessage!!, color = BansiCrimsonDark, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // National ID Number
                BansiTextField(
                    value = nationalId,
                    onValueChange = { nationalId = it },
                    label = "National ID Number *",
                    placeholder = "e.g. 12345678",
                    leadingIcon = Icons.Default.Badge
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ID Card Front & Back Upload Section
                Text(
                    text = "National ID Card Uploads *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        DocumentUploadTile(
                            label = "ID Card Front",
                            url = idCardFrontUrl,
                            isUploading = uploadingDocType == "id_front",
                            onPickClick = { idFrontPicker.launch("image/*") }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DocumentUploadTile(
                            label = "ID Card Back",
                            url = idCardBackUrl,
                            isUploading = uploadingDocType == "id_back",
                            onPickClick = { idBackPicker.launch("image/*") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Vehicle Type Selector
                Text(
                    text = "Fleet Vehicle Type *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VehicleType.entries.forEach { type ->
                        val isSelected = vehicleType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isSelected) BansiGreenLight else InputBackground,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) BansiGreenDark else BorderLight,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { vehicleType = type }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type.label.split(" ").first(),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BansiGreenDark else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Registration Plate
                BansiTextField(
                    value = vehiclePlate,
                    onValueChange = { vehiclePlate = it.uppercase() },
                    label = if (vehicleType != VehicleType.BICYCLE) "Registration Number Plate *" else "Registration Number Plate (Optional)",
                    placeholder = "e.g. KMDG 123A",
                    leadingIcon = Icons.Default.DirectionsCar
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Vehicle Description
                BansiTextField(
                    value = vehicleDescription,
                    onValueChange = { vehicleDescription = it },
                    label = "Vehicle Description / Cargo Carrier *",
                    placeholder = "e.g. Boxer 150 Black with insulated food box",
                    leadingIcon = Icons.Default.DirectionsCar
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Proof of Vehicle Upload Tile
                DocumentUploadTile(
                    label = "Vehicle / Logbook Proof Photo",
                    url = vehicleProofUrl,
                    isUploading = uploadingDocType == "vehicle_proof",
                    onPickClick = { vehiclePicker.launch("image/*") }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Licence Number
                BansiTextField(
                    value = licenceNumber,
                    onValueChange = { licenceNumber = it },
                    label = if (vehicleType != VehicleType.BICYCLE) "Driving Licence Number *" else "Driving Licence Number (Optional)",
                    placeholder = "e.g. DL-98765432",
                    leadingIcon = Icons.Default.Badge
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Licence Expiry
                BansiTextField(
                    value = licenceExpiry,
                    onValueChange = { licenceExpiry = it },
                    label = if (vehicleType != VehicleType.BICYCLE) "Licence Expiry (YYYY-MM-DD) *" else "Licence Expiry (Optional)",
                    placeholder = "e.g. 2028-12-31",
                    leadingIcon = Icons.Default.CalendarToday
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Driving Licence Document Upload Tile
                DocumentUploadTile(
                    label = "Driving Licence Card Photo",
                    url = drivingLicenceUrl,
                    isUploading = uploadingDocType == "licence",
                    onPickClick = { licencePicker.launch("image/*") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Form submission with strict validation
                BansiPrimaryButton(
                    text = "Submit Vetting Credentials",
                    showArrow = true,
                    onClick = {
                        if (nationalId.isBlank()) {
                            errorMessage = "National ID number is required"
                            return@BansiPrimaryButton
                        }
                        if (nationalId.trim().length < 6) {
                            errorMessage = "National ID number must be at least 6 digits"
                            return@BansiPrimaryButton
                        }
                        if (vehicleType != VehicleType.BICYCLE) {
                            if (vehiclePlate.isBlank()) {
                                errorMessage = "Vehicle registration plate is required for motorised vehicles"
                                return@BansiPrimaryButton
                            }
                            if (licenceNumber.isBlank() || licenceExpiry.isBlank()) {
                                errorMessage = "Driving licence number and expiry date are required for motorised vehicles"
                                return@BansiPrimaryButton
                            }
                        }
                        if (vehicleDescription.isBlank()) {
                            errorMessage = "Vehicle description and cargo carrier info are required"
                            return@BansiPrimaryButton
                        }

                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            val req = RiderApplyRequest(
                                nationalIdNumber = nationalId.trim(),
                                idCardFrontUrl = idCardFrontUrl.ifBlank { null },
                                idCardBackUrl = idCardBackUrl.ifBlank { null },
                                vehicleType = vehicleType,
                                vehiclePlate = vehiclePlate.trim().ifBlank { null },
                                vehicleDescription = vehicleDescription.trim().ifBlank { null },
                                vehicleProofUrl = vehicleProofUrl.ifBlank { null },
                                licenceNumber = licenceNumber.trim().ifBlank { null },
                                licenceExpiry = licenceExpiry.trim().ifBlank { null },
                                drivingLicenceUrl = drivingLicenceUrl.ifBlank { null }
                            )
                            val res = apiClient.applyRiderProfile(req)
                            isLoading = false
                            if (res.isSuccess) {
                                res.getOrNull()?.let { onSuccess(it) }
                                onDismiss()
                            } else {
                                errorMessage = res.exceptionOrNull()?.localizedMessage ?: "Failed to submit vetting credentials"
                            }
                        }
                    },
                    isLoading = isLoading
                )
            }
        }
    }
}

@Composable
private fun DocumentUploadTile(
    label: String,
    url: String,
    isUploading: Boolean,
    onPickClick: () -> Unit
) {
    val isUploaded = url.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isUploaded) Color(0xFFF0FDF4) else InputBackground,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (isUploaded) BansiGreenDark else BorderLight,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !isUploading) { onPickClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = RoyalNavy
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Uploading...", fontSize = 12.sp, color = RoyalNavy, fontWeight = FontWeight.SemiBold)
            } else if (isUploaded) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Uploaded",
                    tint = BansiGreenDark,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BansiGreenDark)
                    Text(text = "Document Attached ✓", fontSize = 10.sp, color = BansiGreenDark)
                }
            } else {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = "Upload",
                    tint = RoyalNavy,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(text = "Tap to upload image", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }
    }
}
