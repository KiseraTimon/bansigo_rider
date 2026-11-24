// app/src/main/java/com/project/bansigo_rider/ui/components/OpenStreetMapViewer.kt
package com.project.bansigo_rider.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.config.AppConfig
import com.project.bansigo_rider.core.models.GeoPoint
import com.project.bansigo_rider.core.models.NavigationStep
import com.project.bansigo_rider.ui.theme.*
import kotlin.math.*

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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Interactive zoom and pan
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }
    var showShadedCorridor by remember { mutableStateOf(true) }

    // Pulsing beacon animation for rider position
    val infiniteTransition = rememberInfiniteTransition(label = "rider_pulse")
    val pulseFraction by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Center calculation covering pickup, primary dropoff, other active dropoffs, and rider
    val allPoints = remember(pickupLocation, dropoffLocation, riderLocation, otherDropoffs, routeWaypoints) {
        mutableListOf<GeoPoint>().apply {
            add(pickupLocation)
            add(dropoffLocation)
            riderLocation?.let { add(it) }
            addAll(otherDropoffs)
            addAll(routeWaypoints)
        }
    }

    val minLat = allPoints.minOfOrNull { it.latitude } ?: AppConfig.SUPERMARKET_LATITUDE
    val maxLat = allPoints.maxOfOrNull { it.latitude } ?: AppConfig.SUPERMARKET_LATITUDE
    val minLon = allPoints.minOfOrNull { it.longitude } ?: AppConfig.SUPERMARKET_LONGITUDE
    val maxLon = allPoints.maxOfOrNull { it.longitude } ?: AppConfig.SUPERMARKET_LONGITUDE

    val centerLat = (minLat + maxLat) / 2.0
    val centerLon = (minLon + maxLon) / 2.0

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFE8EFEA))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    zoomScale = (zoomScale * zoom).coerceIn(0.5f, 4.0f)
                    panOffsetX += pan.x
                    panOffsetY += pan.y
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val latSpan = max(0.007, (maxLat - minLat) * 1.55)
            val lonSpan = max(0.007, (maxLon - minLon) * 1.55)

            fun projectToCanvas(lat: Double, lon: Double): Offset {
                val relX = ((lon - centerLon) / lonSpan).toFloat()
                val relY = ((centerLat - lat) / latSpan).toFloat()

                val screenX = (canvasWidth / 2f) + (relX * canvasWidth * zoomScale) + panOffsetX
                val screenY = (canvasHeight / 2f) + (relY * canvasHeight * zoomScale) + panOffsetY
                return Offset(screenX, screenY)
            }

            // 1. Draw OpenStreetMap style road canvas grid
            drawMapGrid(canvasWidth, canvasHeight, panOffsetX, panOffsetY, zoomScale)

            // 2. Draw Dijkstra / A* route with SHADED corridor
            if (routeWaypoints.size >= 2) {
                val routePath = Path()
                val first = projectToCanvas(routeWaypoints[0].latitude, routeWaypoints[0].longitude)
                routePath.moveTo(first.x, first.y)

                for (i in 1 until routeWaypoints.size) {
                    val pt = projectToCanvas(routeWaypoints[i].latitude, routeWaypoints[i].longitude)
                    routePath.lineTo(pt.x, pt.y)
                }

                if (showShadedCorridor) {
                    // Layer A: Wide outer soft translucent shaded corridor
                    drawPath(
                        path = routePath,
                        color = RoyalNavy.copy(alpha = 0.12f),
                        style = Stroke(
                            width = 28f * zoomScale.coerceIn(0.7f, 1.6f),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Layer B: Medium shaded buffer corridor
                    drawPath(
                        path = routePath,
                        color = RoyalNavy.copy(alpha = 0.25f),
                        style = Stroke(
                            width = 14f * zoomScale.coerceIn(0.7f, 1.5f),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Layer C: Core road route polyline
                drawPath(
                    path = routePath,
                    color = RoyalNavy,
                    style = Stroke(
                        width = 6f * zoomScale.coerceIn(0.8f, 1.4f),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Layer D: Center dash accent for directional visibility
                drawPath(
                    path = routePath,
                    color = BansiGreenLight,
                    style = Stroke(
                        width = 2f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                    )
                )
            } else {
                // Fallback direct path
                val p1 = projectToCanvas(pickupLocation.latitude, pickupLocation.longitude)
                val p2 = projectToCanvas(dropoffLocation.latitude, dropoffLocation.longitude)

                if (showShadedCorridor) {
                    drawLine(
                        color = RoyalNavy.copy(alpha = 0.15f),
                        start = p1,
                        end = p2,
                        strokeWidth = 20f,
                        cap = StrokeCap.Round
                    )
                }
                drawLine(
                    color = RoyalNavy,
                    start = p1,
                    end = p2,
                    strokeWidth = 4.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 10f))
                )
            }

            // 3. Draw Other Active Drop-Off Points (when rider has multiple deliveries)
            otherDropoffs.forEachIndexed { index, otherPt ->
                val otherPos = projectToCanvas(otherPt.latitude, otherPt.longitude)
                // Numbered multi-delivery pin
                drawCircle(
                    color = Color(0xFFF59E0B),
                    radius = 11f * zoomScale.coerceIn(0.8f, 1.25f),
                    center = otherPos
                )
                drawCircle(
                    color = SurfaceLight,
                    radius = 5f * zoomScale.coerceIn(0.8f, 1.25f),
                    center = otherPos
                )
            }

            // 4. Draw Bansi Supermarket Pickup Dock Marker
            val storePos = projectToCanvas(pickupLocation.latitude, pickupLocation.longitude)
            drawCircle(
                color = RoyalNavy,
                radius = 13f * zoomScale.coerceIn(0.8f, 1.3f),
                center = storePos
            )
            drawCircle(
                color = SurfaceLight,
                radius = 6f * zoomScale.coerceIn(0.8f, 1.3f),
                center = storePos
            )
            drawCircle(
                color = BansiGreen,
                radius = 3f * zoomScale.coerceIn(0.8f, 1.3f),
                center = storePos
            )

            // 5. Draw Primary Customer Drop-off Marker
            val customerPos = projectToCanvas(dropoffLocation.latitude, dropoffLocation.longitude)
            // Outer beacon ring
            drawCircle(
                color = BansiCrimson.copy(alpha = 0.25f),
                radius = 18f * zoomScale.coerceIn(0.8f, 1.3f),
                center = customerPos
            )
            drawCircle(
                color = BansiCrimson,
                radius = 13f * zoomScale.coerceIn(0.8f, 1.3f),
                center = customerPos
            )
            drawCircle(
                color = SurfaceLight,
                radius = 5.5f * zoomScale.coerceIn(0.8f, 1.3f),
                center = customerPos
            )

            // 6. Draw Live Rider Position Marker with pulsing beacon
            val activeRiderPos = riderLocation?.let { projectToCanvas(it.latitude, it.longitude) } ?: storePos
            drawCircle(
                color = BansiGreen.copy(alpha = 0.25f * (2f - pulseFraction).coerceIn(0.1f, 0.4f)),
                radius = 24f * pulseFraction * zoomScale.coerceIn(0.8f, 1.3f),
                center = activeRiderPos
            )
            drawCircle(
                color = BansiGreenDark,
                radius = 10f * zoomScale.coerceIn(0.8f, 1.3f),
                center = activeRiderPos
            )
            drawCircle(
                color = SurfaceLight,
                radius = 4f * zoomScale.coerceIn(0.8f, 1.3f),
                center = activeRiderPos
            )
        }

        // Top Navigation HUD / Turn Maneuver Overlay
        if (currentStep != null || distanceM != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
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
                                imageVector = Icons.Default.NearMe,
                                contentDescription = null,
                                tint = BansiGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = currentStep?.instruction ?: "Follow OpenStreetMap Route",
                                color = SurfaceLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = currentStep?.streetName?.ifBlank { "Migori Road Network" } ?: "Migori Road Network",
                                color = SurfaceLight.copy(alpha = 0.75f),
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

        // Top Right: Routing Algorithm Badge
        routingAlgorithm?.let { algo ->
            Surface(
                modifier = Modifier
                    .padding(top = 60.dp, end = 10.dp)
                    .align(Alignment.TopEnd),
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Text(
                    text = algo.uppercase(),
                    color = BansiGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
        }

        // Bottom Map Controls: Center, Toggle Shaded Route, Zoom +, Zoom -, External Nav
        Column(
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Toggle Shaded Route Corridor
            MapControlButton(
                icon = Icons.Default.Layers,
                contentDescription = "Toggle Shaded Corridor",
                tint = if (showShadedCorridor) BansiGreen else TextSecondary,
                onClick = { showShadedCorridor = !showShadedCorridor }
            )

            // Recenter
            MapControlButton(
                icon = Icons.Default.MyLocation,
                contentDescription = "Center Map",
                onClick = {
                    zoomScale = 1f
                    panOffsetX = 0f
                    panOffsetY = 0f
                }
            )

            // Zoom In
            MapControlButton(
                icon = Icons.Default.Add,
                contentDescription = "Zoom In",
                onClick = { zoomScale = (zoomScale * 1.25f).coerceAtMost(4.0f) }
            )

            // Zoom Out
            MapControlButton(
                icon = Icons.Default.Remove,
                contentDescription = "Zoom Out",
                onClick = { zoomScale = (zoomScale / 1.25f).coerceAtLeast(0.5f) }
            )

            // Open in External Map App (Google Maps, OsmAnd, Maps.me)
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
                .padding(8.dp)
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

@Composable
private fun MapControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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

private fun DrawScope.drawMapGrid(
    width: Float,
    height: Float,
    panX: Float,
    panY: Float,
    scale: Float
) {
    val spacing = 45f * scale.coerceIn(0.7f, 2.0f)
    val gridColor = Color(0xFFD6E2D9)

    var x = (panX % spacing)
    while (x < width) {
        if (x >= 0) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
        }
        x += spacing
    }

    var y = (panY % spacing)
    while (y < height) {
        if (y >= 0) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }
        y += spacing
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
