// app/src/main/java/com/project/bansigo_rider/ui/screens/options/sections/OptionsBusinessWizardSection.kt
package com.project.bansigo_rider.ui.screens.options.sections

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.RiderApplyRequest
import com.project.bansigo_rider.core.models.RiderProfile
import com.project.bansigo_rider.core.models.VehicleType
import com.project.bansigo_rider.core.network.ApiClient
import com.project.bansigo_rider.ui.components.BansiPrimaryButton
import com.project.bansigo_rider.ui.components.BansiTextField
import com.project.bansigo_rider.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OptionsBusinessWizardSection(
    apiClient: ApiClient,
    currentProfile: RiderProfile?,
    onBackClick: () -> Unit,
    onSuccess: (RiderProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var currentStep by remember { mutableIntStateOf(1) } // 1..4 (4 = Preview & Submit)

    var nationalId by remember { mutableStateOf(currentProfile?.nationalIdNumber.orEmpty()) }
    var vehicleType by remember { mutableStateOf(currentProfile?.vehicleType ?: VehicleType.MOTORCYCLE) }
    var vehiclePlate by remember { mutableStateOf(currentProfile?.vehiclePlate.orEmpty()) }
    var vehicleDescription by remember { mutableStateOf(currentProfile?.vehicleDescription.orEmpty()) }
    var licenceNumber by remember { mutableStateOf(currentProfile?.licenceNumber.orEmpty()) }
    var licenceExpiry by remember { mutableStateOf(currentProfile?.licenceExpiry.orEmpty()) }

    var idCardFrontUrl by remember { mutableStateOf(currentProfile?.idCardFrontUrl.orEmpty()) }
    var idCardBackUrl by remember { mutableStateOf(currentProfile?.idCardBackUrl.orEmpty()) }
    var drivingLicenceUrl by remember { mutableStateOf(currentProfile?.drivingLicenceUrl.orEmpty()) }
    var vehicleProofUrl by remember { mutableStateOf(currentProfile?.vehicleProofUrl.orEmpty()) }

    var idFrontPreview by remember { mutableStateOf<ImageBitmap?>(null) }
    var idBackPreview by remember { mutableStateOf<ImageBitmap?>(null) }
    var vehicleProofPreview by remember { mutableStateOf<ImageBitmap?>(null) }
    var licencePreview by remember { mutableStateOf<ImageBitmap?>(null) }

    var uploadingDocType by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    BackHandler {
        if (currentStep > 1) {
            currentStep -= 1
            errorMessage = null
        } else {
            onBackClick()
        }
    }

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

                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                val fileName = "${docType}_${System.currentTimeMillis()}.jpg"
                val res = apiClient.uploadRiderDocument(docType, fileName, bytes, "image/jpeg")
                uploadingDocType = null

                if (res.isSuccess) {
                    val url = res.getOrNull().orEmpty()
                    when (docType) {
                        "id_front" -> {
                            idCardFrontUrl = url
                            if (bitmap != null) idFrontPreview = bitmap
                        }
                        "id_back" -> {
                            idCardBackUrl = url
                            if (bitmap != null) idBackPreview = bitmap
                        }
                        "licence" -> {
                            drivingLicenceUrl = url
                            if (bitmap != null) licencePreview = bitmap
                        }
                        "vehicle_proof" -> {
                            vehicleProofUrl = url
                            if (bitmap != null) vehicleProofPreview = bitmap
                        }
                    }
                } else {
                    errorMessage = res.exceptionOrNull()?.message ?: "Upload failed"
                }
            } catch (e: Exception) {
                uploadingDocType = null
                errorMessage = e.message ?: "Error processing document"
            }
        }
    }

    val idFrontLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uploadUri(it, "id_front") }
    }
    val idBackLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uploadUri(it, "id_back") }
    }
    val vehicleProofLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uploadUri(it, "vehicle_proof") }
    }
    val licenceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uploadUri(it, "licence") }
    }

    val stepTitle = when (currentStep) {
        1 -> "Step 1 of 4: National ID"
        2 -> "Step 2 of 4: Vehicle & Logbook"
        3 -> "Step 3 of 4: Driving Licence"
        else -> "Step 4 of 4: Preview & Submit"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        OptionsSubPageTopBar(
            title = "Business Verification",
            subtitle = stepTitle,
            onBackClick = {
                if (currentStep > 1) {
                    currentStep -= 1
                    errorMessage = null
                } else {
                    onBackClick()
                }
            }
        )

        LinearProgressIndicator(
            progress = { currentStep / 4f },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = BansiGreenDark,
            trackColor = BorderLight
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!errorMessage.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BansiCrimsonLight, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = errorMessage!!,
                        color = BansiCrimsonDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            when (currentStep) {
                1 -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "National Identification",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy
                            )
                            Text(
                                text = "Enter your Kenyan National ID number and upload clear photos of both sides.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            BansiTextField(
                                value = nationalId,
                                onValueChange = { nationalId = it.filter { ch -> ch.isDigit() } },
                                label = "National ID Number *",
                                placeholder = "e.g. 12345678",
                                leadingIcon = Icons.Default.Badge
                            )

                            WizardDocumentUploadRow(
                                label = "National ID Card (Front) *",
                                uploadedUrl = idCardFrontUrl,
                                previewBitmap = idFrontPreview,
                                isUploading = uploadingDocType == "id_front",
                                onUploadClick = { idFrontLauncher.launch("image/*") }
                            )

                            WizardDocumentUploadRow(
                                label = "National ID Card (Back) *",
                                uploadedUrl = idCardBackUrl,
                                previewBitmap = idBackPreview,
                                isUploading = uploadingDocType == "id_back",
                                onUploadClick = { idBackLauncher.launch("image/*") }
                            )
                        }
                    }
                }

                2 -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Fleet Vehicle & Logbook",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy
                            )

                            Text(
                                text = "Select Vehicle Type",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                VehicleType.entries.forEach { type ->
                                    val selected = type == vehicleType
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (selected) RoyalNavy else InputBackground,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable { vehicleType = type }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selected) Color.White else TextSecondary
                                        )
                                    }
                                }
                            }

                            if (vehicleType != VehicleType.BICYCLE) {
                                BansiTextField(
                                    value = vehiclePlate,
                                    onValueChange = { vehiclePlate = it.uppercase() },
                                    label = "Registration Number Plate *",
                                    placeholder = "e.g. KMDG 123A",
                                    leadingIcon = Icons.Default.TwoWheeler
                                )
                            }

                            BansiTextField(
                                value = vehicleDescription,
                                onValueChange = { vehicleDescription = it },
                                label = "Vehicle Description",
                                placeholder = "e.g. Boxer 150 Black with insulated box",
                                leadingIcon = Icons.Default.Info
                            )

                            WizardDocumentUploadRow(
                                label = "Logbook / Vehicle Proof *",
                                uploadedUrl = vehicleProofUrl,
                                previewBitmap = vehicleProofPreview,
                                isUploading = uploadingDocType == "vehicle_proof",
                                onUploadClick = { vehicleProofLauncher.launch("image/*") }
                            )
                        }
                    }
                }

                3 -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Driving Licence",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy
                            )

                            if (vehicleType == VehicleType.BICYCLE) {
                                Text(
                                    text = "Bicycle riders do not require a driving licence. You can proceed directly to preview.",
                                    fontSize = 12.sp,
                                    color = BansiGreenDark,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                BansiTextField(
                                    value = licenceNumber,
                                    onValueChange = { licenceNumber = it.uppercase() },
                                    label = "Driving Licence Number *",
                                    placeholder = "e.g. DL-98765432",
                                    leadingIcon = Icons.Default.DirectionsCar
                                )

                                BansiTextField(
                                    value = licenceExpiry,
                                    onValueChange = { licenceExpiry = it },
                                    label = "Licence Expiry Date (YYYY-MM-DD) *",
                                    placeholder = "e.g. 2028-12-31",
                                    leadingIcon = Icons.Default.CalendarToday
                                )

                                WizardDocumentUploadRow(
                                    label = "Driving Licence Photo *",
                                    uploadedUrl = drivingLicenceUrl,
                                    previewBitmap = licencePreview,
                                    isUploading = uploadingDocType == "licence",
                                    onUploadClick = { licenceLauncher.launch("image/*") }
                                )
                            }
                        }
                    }
                }

                4 -> {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Review Your Submission",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalNavy
                            )
                            Text(
                                text = "Confirm all details and uploaded documents before sending them to Migori dispatch administration.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            HorizontalDivider(color = BorderLight)

                            PreviewFieldRow("National ID Number", nationalId)
                            PreviewDocStatusRow("National ID Front", idCardFrontUrl, idFrontPreview)
                            PreviewDocStatusRow("National ID Back", idCardBackUrl, idBackPreview)

                            HorizontalDivider(color = BorderLight)

                            PreviewFieldRow("Fleet Vehicle Type", vehicleType.label)
                            if (vehicleType != VehicleType.BICYCLE) {
                                PreviewFieldRow("Registration Number", vehiclePlate)
                            }
                            PreviewFieldRow("Vehicle Description", vehicleDescription.ifBlank { "None provided" })
                            PreviewDocStatusRow("Logbook / Vehicle Proof", vehicleProofUrl, vehicleProofPreview)

                            if (vehicleType != VehicleType.BICYCLE) {
                                HorizontalDivider(color = BorderLight)
                                PreviewFieldRow("Driver's Licence No.", licenceNumber)
                                PreviewFieldRow("Licence Expiry Date", licenceExpiry)
                                PreviewDocStatusRow("Driver's Licence Photo", drivingLicenceUrl, licencePreview)
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceLight)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentStep > 1) {
                OutlinedButton(
                    onClick = {
                        currentStep -= 1
                        errorMessage = null
                    },
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Previous", fontWeight = FontWeight.SemiBold, color = RoyalNavy)
                }
            }

            if (currentStep < 4) {
                BansiPrimaryButton(
                    text = if (currentStep == 3) "Preview Submission" else "Next Step",
                    onClick = {
                        errorMessage = null
                        when (currentStep) {
                            1 -> {
                                if (!nationalId.matches(Regex("^\\d{6,10}$"))) {
                                    errorMessage = "Enter a valid 6–10 digit National ID number"
                                    return@BansiPrimaryButton
                                }
                                if (idCardFrontUrl.isBlank() || idCardBackUrl.isBlank()) {
                                    errorMessage = "Please upload both front and back photos of your National ID"
                                    return@BansiPrimaryButton
                                }
                                currentStep = 2
                            }
                            2 -> {
                                if (vehicleType != VehicleType.BICYCLE && vehiclePlate.isBlank()) {
                                    errorMessage = "Vehicle registration plate is required"
                                    return@BansiPrimaryButton
                                }
                                if (vehicleProofUrl.isBlank()) {
                                    errorMessage = "Please upload your logbook or vehicle proof"
                                    return@BansiPrimaryButton
                                }
                                currentStep = 3
                            }
                            3 -> {
                                if (vehicleType != VehicleType.BICYCLE) {
                                    if (licenceNumber.isBlank() || !licenceExpiry.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$"))) {
                                        errorMessage = "Enter licence number and expiry date (YYYY-MM-DD)"
                                        return@BansiPrimaryButton
                                    }
                                    if (drivingLicenceUrl.isBlank()) {
                                        errorMessage = "Please upload a photo of your driving licence"
                                        return@BansiPrimaryButton
                                    }
                                }
                                currentStep = 4
                            }
                        }
                    },
                    enabled = uploadingDocType == null,
                    modifier = Modifier.weight(1f)
                )
            } else {
                BansiPrimaryButton(
                    text = "Submit for Verification",
                    isLoading = isSubmitting,
                    onClick = {
                        isSubmitting = true
                        errorMessage = null
                        scope.launch {
                            val res = apiClient.applyRiderProfile(
                                RiderApplyRequest(
                                    nationalIdNumber = nationalId.trim(),
                                    idCardFrontUrl = idCardFrontUrl.ifBlank { null },
                                    idCardBackUrl = idCardBackUrl.ifBlank { null },
                                    vehicleType = vehicleType,
                                    vehiclePlate = if (vehicleType == VehicleType.BICYCLE) null else vehiclePlate.trim(),
                                    vehicleDescription = vehicleDescription.trim().ifBlank { null },
                                    vehicleProofUrl = vehicleProofUrl.ifBlank { null },
                                    licenceNumber = if (vehicleType == VehicleType.BICYCLE) null else licenceNumber.trim(),
                                    licenceExpiry = if (vehicleType == VehicleType.BICYCLE) null else licenceExpiry.trim(),
                                    drivingLicenceUrl = if (vehicleType == VehicleType.BICYCLE) null else drivingLicenceUrl.ifBlank { null }
                                )
                            )
                            isSubmitting = false
                            if (res.isSuccess) {
                                res.getOrNull()?.let { onSuccess(it) }
                            } else {
                                errorMessage = res.exceptionOrNull()?.message ?: "Failed to submit application"
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WizardDocumentUploadRow(
    label: String,
    uploadedUrl: String,
    previewBitmap: ImageBitmap?,
    isUploading: Boolean,
    onUploadClick: () -> Unit
) {
    val isUploaded = uploadedUrl.isNotBlank()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isUploaded) Color(0xFFF0FDF4) else InputBackground, RoundedCornerShape(12.dp))
            .border(1.dp, if (isUploaded) BansiGreenDark else BorderLight, RoundedCornerShape(12.dp))
            .clickable(enabled = !isUploading) { onUploadClick() }
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = if (isUploaded) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = if (isUploaded) BansiGreenDark else RoyalNavy,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isUploading) "Uploading..." else if (isUploaded) "Uploaded • Tap to replace" else "Tap to select photo",
                        fontSize = 11.sp,
                        color = if (isUploaded) BansiGreenDark else TextSecondary
                    )
                }
            }
            if (isUploading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = RoyalNavy)
            }
        }

        if (previewBitmap != null) {
            Image(
                bitmap = previewBitmap,
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
private fun PreviewFieldRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
private fun PreviewDocStatusRow(label: String, url: String, previewBitmap: ImageBitmap?) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 12.sp, color = TextSecondary)
            Text(
                text = if (url.isNotBlank()) "Attached" else "Missing",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (url.isNotBlank()) BansiGreenDark else BansiCrimsonDark
            )
        }
        if (previewBitmap != null) {
            Image(
                bitmap = previewBitmap,
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}
