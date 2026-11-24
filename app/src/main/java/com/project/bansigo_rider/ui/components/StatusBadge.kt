// app/src/main/java/com/project/bansigo_rider/ui/components/StatusBadge.kt
package com.project.bansigo_rider.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.core.models.ApprovalStatus
import com.project.bansigo_rider.core.models.DeliveryStatus
import com.project.bansigo_rider.core.models.RiderStatus
import com.project.bansigo_rider.ui.theme.*

@Composable
fun StatusBadge(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    dotColor: Color? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (dotColor != null) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(dotColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun RiderStatusBadge(status: RiderStatus, modifier: Modifier = Modifier) {
    val (bg, fg, dot) = when (status) {
        RiderStatus.AVAILABLE -> Triple(BansiGreenLight, BansiGreenDark, StatusAvailable)
        RiderStatus.BUSY -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), StatusBusy)
        RiderStatus.OFFLINE -> Triple(Color(0xFFF3F4F6), TextSecondary, StatusOffline)
    }
    StatusBadge(
        text = status.label,
        backgroundColor = bg,
        contentColor = fg,
        dotColor = dot,
        modifier = modifier
    )
}

@Composable
fun ApprovalStatusBadge(status: ApprovalStatus, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        ApprovalStatus.NEW -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1))
        ApprovalStatus.APPROVED -> Pair(BansiGreenLight, BansiGreenDark)
        ApprovalStatus.PENDING -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        ApprovalStatus.REJECTED -> Pair(BansiCrimsonLight, BansiCrimsonDark)
        ApprovalStatus.SUSPENDED -> Pair(Color(0xFFF3F4F6), TextSecondary)
    }
    StatusBadge(
        text = status.label,
        backgroundColor = bg,
        contentColor = fg,
        dotColor = fg,
        modifier = modifier
    )
}

@Composable
fun DeliveryStatusBadge(status: DeliveryStatus, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        DeliveryStatus.PENDING, DeliveryStatus.ASSIGNED -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        DeliveryStatus.ACCEPTED -> Pair(RoyalNavyLight, RoyalNavy)
        DeliveryStatus.PICKED_UP, DeliveryStatus.ARRIVED -> Pair(Color(0xFFE0E7FF), Color(0xFF3730A3))
        DeliveryStatus.DELIVERED -> Pair(BansiGreenLight, BansiGreenDark)
        DeliveryStatus.CANCELLED, DeliveryStatus.FAILED -> Pair(BansiCrimsonLight, BansiCrimsonDark)
    }
    StatusBadge(
        text = status.label.uppercase(),
        backgroundColor = bg,
        contentColor = fg,
        dotColor = fg,
        modifier = modifier
    )
}
