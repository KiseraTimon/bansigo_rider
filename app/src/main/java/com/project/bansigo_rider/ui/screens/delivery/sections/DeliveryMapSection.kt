// app/src/main/java/com/project/bansigo_rider/ui/screens/delivery/sections/DeliveryMapSection.kt
package com.project.bansigo_rider.ui.screens.delivery.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.DeliveryDetail
import com.project.bansigo_rider.core.models.GeoPoint
import com.project.bansigo_rider.core.models.NavigationStep
import com.project.bansigo_rider.ui.components.OpenStreetMapViewer
import com.project.bansigo_rider.ui.theme.*

@Composable
fun DeliveryMapSection(
    delivery: DeliveryDetail,
    riderLocation: GeoPoint?,
    otherDropoffs: List<GeoPoint> = emptyList(),
    currentStep: NavigationStep? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Route,
                        contentDescription = null,
                        tint = RoyalNavy,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "OpenStreetMap Road Routing",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalNavy
                    )
                }

                // Algorithm badge from backend
                val algo = delivery.routingAlgorithm ?: "dijkstra"
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RoyalNavyLight
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.AltRoute,
                            contentDescription = null,
                            tint = RoyalNavyDark,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = algo.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalNavyDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OpenStreetMapViewer(
                pickupLocation = delivery.pickupLocation,
                dropoffLocation = delivery.dropoffLocation,
                riderLocation = riderLocation,
                otherDropoffs = otherDropoffs,
                routeWaypoints = delivery.routeWaypoints,
                currentStep = currentStep,
                estimatedMinutes = delivery.estimatedDurationS?.let { it / 60 },
                distanceM = delivery.estimatedDistanceM,
                routingAlgorithm = delivery.routingAlgorithm
            )
        }
    }
}
