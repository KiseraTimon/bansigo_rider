// app/src/main/java/com/project/bansigo_rider/ui/screens/home/sections/HomeSlidingNoticeSection.kt
package com.project.bansigo_rider.ui.screens.home.sections

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.bansigo_rider.ui.theme.BansiGreen
import com.project.bansigo_rider.ui.theme.BansiGreenSubtle
import com.project.bansigo_rider.ui.theme.BorderLight
import com.project.bansigo_rider.ui.theme.RoyalNavy
import com.project.bansigo_rider.ui.theme.RoyalNavyLight
import com.project.bansigo_rider.ui.theme.SurfaceCard
import com.project.bansigo_rider.ui.theme.TextMuted
import com.project.bansigo_rider.ui.theme.TextPrimary
import com.project.bansigo_rider.ui.theme.TextSecondary

private data class OperationalNotice(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBg: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeSlidingNoticeSection(
    modifier: Modifier = Modifier
) {
    val notices = listOf(
        OperationalNotice(
            title = "4-Digit PIN Handover",
            subtitle = "Always request and validate customer's 4-digit secret PIN before releasing parcel items.",
            icon = Icons.Default.Key,
            iconColor = RoyalNavy,
            iconBg = RoyalNavyLight
        ),
        OperationalNotice(
            title = "COD Cash Remittance",
            subtitle = "All Cash-on-Delivery funds collected must be remitted at the Bansi Supermarket cashier daily.",
            icon = Icons.Default.AccountBalance,
            iconColor = Color(0xFFF59E0B),
            iconBg = Color(0xFFFEF3C7)
        ),
        OperationalNotice(
            title = "Guaranteed 70% Fee Share",
            subtitle = "You keep 70% of every delivery fee charged to the customer directly into your rider wallet.",
            icon = Icons.Default.LocalOffer,
            iconColor = BansiGreen,
            iconBg = BansiGreenSubtle
        ),
        OperationalNotice(
            title = "18+ Age Restricted Items",
            subtitle = "For tobacco and alcohol packages, verify the recipient's national ID before handover.",
            icon = Icons.Default.Security,
            iconColor = Color(0xFFEF4444),
            iconBg = Color(0xFFFFEBEE)
        ),
        OperationalNotice(
            title = "Cold-Chain Perishables",
            subtitle = "Ensure chilled dairy and fresh meats are stored inside the thermal insulated cargo box.",
            icon = Icons.Default.AcUnit,
            iconColor = Color(0xFF0284C7),
            iconBg = Color(0xFFE0F2FE)
        )
    )

    val pagerState = rememberPagerState(pageCount = { notices.size })

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val notice = notices[page]
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceCard,
                border = BorderStroke(1.dp, BorderLight),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(notice.iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = notice.icon,
                            contentDescription = notice.title,
                            tint = notice.iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = notice.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = notice.subtitle,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dot / Pill Indicator
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(notices.size) { index ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 18.dp else 6.dp,
                    animationSpec = tween(300),
                    label = "pill_width"
                )
                val color by animateColorAsState(
                    targetValue = if (isSelected) BansiGreen else BorderLight,
                    animationSpec = tween(300),
                    label = "pill_color"
                )

                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}
