// app/src/main/java/com/project/bansigo_rider/ui/components/OpenStreetMapViewer.kt
package com.project.bansigo_rider.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.MotionEvent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Straight
import androidx.compose.material.icons.filled.TurnLeft
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material.icons.filled.TurnSlightLeft
import androidx.compose.material.icons.filled.TurnSlightRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.project.bansigo_rider.core.models.GeoPoint
import com.project.bansigo_rider.core.models.NavigationStep
import com.project.bansigo_rider.ui.theme.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.File
import org.osmdroid.util.GeoPoint as OsmGeoPoint

@Composable
fun OpenStreetMapViewer(
    pickupLocation: GeoPoint,
    dropoffLocation: GeoPoint,
    riderLocation: GeoPoint? = null,
    otherDropoffs: List<GeoPoint> = emptyList(),
    routeWaypoints: List<GeoPoint> = emptyList(),
    currentStep: NavigationStep? = null,
    estimatedMinutes: Int? = null,
    distanceM: Int? = null,
    routingAlgorithm: String? = null,
    isExpanded: Boolean = false,
    onToggleExpand: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showFullScreen by remember { mutableStateOf(false) }

    OpenStreetMapContent(
        context = context,
        pickupLocation = pickupLocation,
        dropoffLocation = dropoffLocation,
        riderLocation = riderLocation,
        otherDropoffs = otherDropoffs,
        routeWaypoints = routeWaypoints,
        currentStep = currentStep,
        estimatedMinutes = estimatedMinutes,
        distanceM = distanceM,
        routingAlgorithm = routingAlgorithm,
        isExpanded = isExpanded,
        onToggleExpand = onToggleExpand ?: { showFullScreen = !showFullScreen },
        modifier = modifier
    )

    if (showFullScreen && onToggleExpand == null) {
        Dialog(
            onDismissRequest = { showFullScreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                OpenStreetMapContent(
                    context = context,
                    pickupLocation = pickupLocation,
                    dropoffLocation = dropoffLocation,
                    riderLocation = riderLocation,
                    otherDropoffs = otherDropoffs,
                    routeWaypoints = routeWaypoints,
                    currentStep = currentStep,
                    estimatedMinutes = estimatedMinutes,
                    distanceM = distanceM,
                    routingAlgorithm = routingAlgorithm,
                    isExpanded = true,
                    onToggleExpand = { showFullScreen = false },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun OpenStreetMapContent(
    context: Context,
    pickupLocation: GeoPoint,
    dropoffLocation: GeoPoint,
    riderLocation: GeoPoint?,
    otherDropoffs: List<GeoPoint>,
    routeWaypoints: List<GeoPoint>,
    currentStep: NavigationStep?,
    estimatedMinutes: Int?,
    distanceM: Int?,
    routingAlgorithm: String?,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showShadedCorridor by remember { mutableStateOf(true) }
    var hasFittedBounds by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    remember(context) {
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            osmdroidBasePath = File(context.cacheDir, "osmdroid")
            osmdroidTileCache = File(context.cacheDir, "osmdroid/tiles")
        }
        true
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            minZoomLevel = 4.0
            maxZoomLevel = 20.0
            // Allow smooth panning inside ScrollView / LazyColumn
            setOnTouchListener { view, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        view.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        view.parent?.requestDisallowInterceptTouchEvent(false)
                    }
                }
                false
            }
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    fun fitAllMarkers() {
        val allPts = buildList {
            add(OsmGeoPoint(pickupLocation.latitude, pickupLocation.longitude))
            add(OsmGeoPoint(dropoffLocation.latitude, dropoffLocation.longitude))
            riderLocation?.let { add(OsmGeoPoint(it.latitude, it.longitude)) }
            otherDropoffs.forEach { add(OsmGeoPoint(it.latitude, it.longitude)) }
            routeWaypoints.forEach { add(OsmGeoPoint(it.latitude, it.longitude)) }
        }
        if (allPts.size >= 2) {
            try {
                val box = BoundingBox.fromGeoPoints(allPts)
                mapView.post {
                    try {
                        mapView.zoomToBoundingBox(box, true, 96)
                    } catch (_: Exception) {
                        mapView.controller.setCenter(box.centerWithDateLine)
                        mapView.controller.setZoom(15.0)
                    }
                }
            } catch (_: Exception) {
                mapView.controller.setCenter(allPts.first())
                mapView.controller.setZoom(15.0)
            }
        } else if (allPts.isNotEmpty()) {
            mapView.controller.setCenter(allPts.first())
            mapView.controller.setZoom(16.0)
        }
    }

    val containerModifier = if (isExpanded) {
        modifier.fillMaxSize()
    } else {
        modifier
            .fillMaxWidth()
            .height(310.dp)
            .clip(RoundedCornerShape(18.dp))
    }

    Box(
        modifier = containerModifier.background(Color(0xFFE8EFEA))
    ) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize(),
            update = { mv ->
                mv.overlays.clear()

                val pts = if (routeWaypoints.size >= 2) {
                    routeWaypoints.map { OsmGeoPoint(it.latitude, it.longitude) }
                } else {
                    listOf(
                        OsmGeoPoint(pickupLocation.latitude, pickupLocation.longitude),
                        OsmGeoPoint(dropoffLocation.latitude, dropoffLocation.longitude)
                    )
                }

                if (showShadedCorridor) {
                    val haloLine = Polyline(mv).apply {
                        setPoints(pts)
                        outlinePaint.color = RoyalNavy.copy(alpha = 0.22f).toArgb()
                        outlinePaint.strokeWidth = 22f
                        outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
                        outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND
                    }
                    mv.overlays.add(haloLine)
                }

                val mainLine = Polyline(mv).apply {
                    setPoints(pts)
                    outlinePaint.color = RoyalNavy.toArgb()
                    outlinePaint.strokeWidth = 10f
                    outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
                    outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND
                }
                mv.overlays.add(mainLine)

                // Other batched dropoffs
                otherDropoffs.forEachIndexed { index, pt ->
                    val marker = Marker(mv).apply {
                        position = OsmGeoPoint(pt.latitude, pt.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        title = "Stop #${index + 2}: ${pt.name}"
                        icon = createPinDrawable(context, Color(0xFFF59E0B).toArgb(), SurfaceLight.toArgb(), 34)
                    }
                    mv.overlays.add(marker)
                }

                // Store pickup marker
                val storeMarker = Marker(mv).apply {
                    position = OsmGeoPoint(pickupLocation.latitude, pickupLocation.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    title = "Pickup: ${pickupLocation.name}"
                    icon = createPinDrawable(context, RoyalNavy.toArgb(), BansiGreen.toArgb(), 40)
                }
                mv.overlays.add(storeMarker)

                // Primary customer dropoff marker
                val dropMarker = Marker(mv).apply {
                    position = OsmGeoPoint(dropoffLocation.latitude, dropoffLocation.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    title = "Dropoff: ${dropoffLocation.name}"
                    icon = createPinDrawable(context, BansiCrimson.toArgb(), SurfaceLight.toArgb(), 44)
                }
                mv.overlays.add(dropMarker)

                // Live rider GPS marker
                riderLocation?.let { loc ->
                    val riderMarker = Marker(mv).apply {
                        position = OsmGeoPoint(loc.latitude, loc.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        title = "Your Live Position"
                        icon = createPinDrawable(context, BansiGreenDark.toArgb(), SurfaceLight.toArgb(), 42)
                    }
                    mv.overlays.add(riderMarker)
                }

                if (!hasFittedBounds) {
                    hasFittedBounds = true
                    fitAllMarkers()
                }
                mv.invalidate()
            }
        )

        // Top Navigation HUD / Turn Maneuver Overlay
        if (currentStep != null || distanceM != null) {
            val cleanStreet = currentStep?.streetName
                ?.takeIf { it.isNotBlank() && it.lowercase() != "null" }
                ?: "Migori Road Network"
            val cleanInstruction = currentStep?.instruction
                ?.takeIf { it.isNotBlank() && it.lowercase() != "null" }
                ?: "Follow OpenStreetMap Route"

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = if (isExpanded) 36.dp else 10.dp)
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(14.dp),
                color = RoyalNavyDark.copy(alpha = 0.94f),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = maneuverIconFor(currentStep?.type ?: "straight"),
                                contentDescription = null,
                                tint = BansiGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = cleanInstruction,
                                color = SurfaceLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = cleanStreet,
                                color = SurfaceLight.copy(alpha = 0.78f),
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }

                    if (distanceM != null) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (distanceM >= 1000) String.format("%.1f km", distanceM / 1000.0) else "$distanceM m",
                                color = BansiGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            if (estimatedMinutes != null) {
                                Text(
                                    text = "$estimatedMinutes min",
                                    color = SurfaceLight.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Map Controls: Expand/Collapse, Shaded Corridor, Center, Zoom +, Zoom -, External Nav
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = if (isExpanded) 28.dp else 10.dp)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MapControlButton(
                icon = if (isExpanded) Icons.Default.CloseFullscreen else Icons.Default.OpenInFull,
                contentDescription = if (isExpanded) "Collapse Map" else "Expand Map",
                tint = RoyalNavy,
                onClick = onToggleExpand
            )

            MapControlButton(
                icon = Icons.Default.Layers,
                contentDescription = "Toggle Shaded Corridor",
                tint = if (showShadedCorridor) BansiGreen else TextSecondary,
                onClick = { showShadedCorridor = !showShadedCorridor }
            )

            MapControlButton(
                icon = Icons.Default.MyLocation,
                contentDescription = "Center Map",
                onClick = { fitAllMarkers() }
            )

            MapControlButton(
                icon = Icons.Default.Add,
                contentDescription = "Zoom In",
                onClick = { mapView.controller.zoomIn() }
            )

            MapControlButton(
                icon = Icons.Default.Remove,
                contentDescription = "Zoom Out",
                onClick = { mapView.controller.zoomOut() }
            )

            MapControlButton(
                icon = Icons.Default.Directions,
                contentDescription = "Open in Maps App",
                tint = BansiCrimson,
                onClick = {
                    openExternalMap(
                        context = context,
                        destLat = dropoffLocation.latitude,
                        destLon = dropoffLocation.longitude,
                        destLabel = dropoffLocation.name
                    )
                }
            )
        }

        // Bottom Left: Attribution & Multi-Order Legend
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = if (isExpanded) 28.dp else 8.dp)
                .align(Alignment.BottomStart),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (otherDropoffs.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.65f)
                ) {
                    Text(
                        text = "• Selected Drop-off (Red)  • Other Stops (${otherDropoffs.size} Orange)",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "© OpenStreetMap contributors",
                    color = Color.White,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

private fun maneuverIconFor(type: String): ImageVector {
    return when (type.lowercase()) {
        "turn_left", "sharp_left" -> Icons.Default.TurnLeft
        "turn_right", "sharp_right" -> Icons.Default.TurnRight
        "slight_left" -> Icons.Default.TurnSlightLeft
        "slight_right" -> Icons.Default.TurnSlightRight
        "straight" -> Icons.Default.Straight
        else -> Icons.Default.NearMe
    }
}

private fun createPinDrawable(
    context: Context,
    outerColor: Int,
    innerColor: Int,
    sizePx: Int
): BitmapDrawable {
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val radius = sizePx / 2f

    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
    }
    canvas.drawCircle(radius, radius, radius, borderPaint)

    val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = outerColor
        style = Paint.Style.FILL
    }
    canvas.drawCircle(radius, radius, radius * 0.82f, outerPaint)

    val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = innerColor
        style = Paint.Style.FILL
    }
    canvas.drawCircle(radius, radius, radius * 0.36f, innerPaint)

    return BitmapDrawable(context.resources, bitmap)
}

@Composable
private fun MapControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = RoyalNavy
) {
    Surface(
        shape = CircleShape,
        color = SurfaceLight,
        shadowElevation = 2.dp,
        modifier = modifier.size(34.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

private fun openExternalMap(
    context: Context,
    destLat: Double,
    destLon: Double,
    destLabel: String
) {
    try {
        val uri = Uri.parse("geo:$destLat,$destLon?q=$destLat,$destLon(${Uri.encode(destLabel)})")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$destLat,$destLon")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        } catch (_: Exception) {}
    }
}
