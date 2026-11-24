// app/src/main/java/com/project/bansigo_rider/ui/components/VectorIllustrations.kt
package com.project.bansigo_rider.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.project.bansigo_rider.ui.theme.*

/**
 * Personal Information Illustration (Registration Step 1)
 * Identity profile card with avatar, checkmark seal, and green accents.
 */
@Composable
fun PersonalInfoIllustration(
    modifier: Modifier = Modifier,
    width: Dp = 180.dp,
    height: Dp = 140.dp
) {
    Canvas(modifier = modifier.size(width, height)) {
        val w = this.size.width
        val h = this.size.height

        // 1. Soft Circular Background
        drawCircle(
            color = Color(0xFFF1F5F9),
            radius = w * 0.40f,
            center = Offset(w * 0.50f, h * 0.52f)
        )

        // 2. Decorative Green Leaf Branch (Left)
        val leafColor = BansiGreen
        drawOval(
            color = leafColor,
            topLeft = Offset(w * 0.18f, h * 0.32f),
            size = Size(14f, 22f)
        )
        drawOval(
            color = BansiGreenDark,
            topLeft = Offset(w * 0.14f, h * 0.44f),
            size = Size(16f, 10f)
        )

        // 3. Floating Orange Star (Right)
        val starPaint = Color(0xFFFB923C)
        drawCircle(color = starPaint, radius = 6f, center = Offset(w * 0.82f, h * 0.28f))
        drawCircle(color = Color(0xFFFEF3C7), radius = 3f, center = Offset(w * 0.82f, h * 0.28f))

        // 4. Identity Card Body
        val cardW = w * 0.48f
        val cardH = h * 0.58f
        val cardX = w * 0.26f
        val cardY = h * 0.24f

        // Card Drop Shadow
        drawRoundRect(
            color = Color(0xFFE2E8F0),
            topLeft = Offset(cardX + 4f, cardY + 5f),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(16f, 16f)
        )
        // White Card Surface
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cardX, cardY),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(16f, 16f),
            style = Fill
        )
        drawRoundRect(
            color = Color(0xFFCBD5E1),
            topLeft = Offset(cardX, cardY),
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(16f, 16f),
            style = Stroke(width = 2.5f)
        )

        // 5. Avatar Head and Shoulders inside card
        val avatarCenterX = cardX + cardW * 0.50f
        val avatarCenterY = cardY + cardH * 0.35f

        // Avatar Circle Background
        drawCircle(
            color = Color(0xFFE0E7FF),
            radius = 20f,
            center = Offset(avatarCenterX, avatarCenterY)
        )
        // Head
        drawCircle(
            color = RoyalNavy,
            radius = 9f,
            center = Offset(avatarCenterX, avatarCenterY - 4f)
        )
        // Torso / Shoulders
        val torsoPath = Path().apply {
            moveTo(avatarCenterX - 14f, avatarCenterY + 18f)
            quadraticTo(avatarCenterX, avatarCenterY + 8f, avatarCenterX + 14f, avatarCenterY + 18f)
            close()
        }
        drawPath(torsoPath, color = RoyalNavy)

        // 6. Text placeholder lines on Card
        drawRoundRect(
            color = Color(0xFF94A3B8),
            topLeft = Offset(cardX + cardW * 0.22f, cardY + cardH * 0.62f),
            size = Size(cardW * 0.56f, 6f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawRoundRect(
            color = Color(0xFFCBD5E1),
            topLeft = Offset(cardX + cardW * 0.30f, cardY + cardH * 0.76f),
            size = Size(cardW * 0.40f, 5f),
            cornerRadius = CornerRadius(2.5f, 2.5f)
        )

        // 7. Verified Badge Seal (Bottom Right of Card)
        val badgeX = cardX + cardW - 8f
        val badgeY = cardY + cardH - 8f
        drawCircle(
            color = BansiGreen,
            radius = 14f,
            center = Offset(badgeX, badgeY)
        )
        drawCircle(
            color = Color.White,
            radius = 14f,
            center = Offset(badgeX, badgeY),
            style = Stroke(width = 2.5f)
        )
        // Checkmark
        val checkPath = Path().apply {
            moveTo(badgeX - 6f, badgeY)
            lineTo(badgeX - 1.5f, badgeY + 4.5f)
            lineTo(badgeX + 6.5f, badgeY - 4.5f)
        }
        drawPath(
            checkPath,
            color = Color.White,
            style = Stroke(width = 3.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

/**
 * Contact Information Illustration (Registration Step 2)
 * Mobile phone with email envelope, communication signal, and friendly delivery accents.
 */
@Composable
fun ContactInfoIllustration(
    modifier: Modifier = Modifier,
    width: Dp = 180.dp,
    height: Dp = 140.dp
) {
    Canvas(modifier = modifier.size(width, height)) {
        val w = this.size.width
        val h = this.size.height

        // 1. Soft Circular Background
        drawCircle(
            color = Color(0xFFFFF7ED),
            radius = w * 0.40f,
            center = Offset(w * 0.50f, h * 0.52f)
        )

        // 2. Leaf accents
        drawOval(
            color = BansiGreen,
            topLeft = Offset(w * 0.16f, h * 0.40f),
            size = Size(14f, 20f)
        )
        drawOval(
            color = BansiGreenDark,
            topLeft = Offset(w * 0.80f, h * 0.35f),
            size = Size(12f, 18f)
        )

        // 3. Center Smartphone
        val phoneW = w * 0.36f
        val phoneH = h * 0.62f
        val phoneX = w * 0.32f
        val phoneY = h * 0.22f

        // Phone Frame
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(phoneX, phoneY),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(16f, 16f),
            style = Stroke(width = 4.5f)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(phoneX + 2.5f, phoneY + 2.5f),
            size = Size(phoneW - 5f, phoneH - 5f),
            cornerRadius = CornerRadius(14f, 14f),
            style = Fill
        )

        // Phone Top Notch
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(phoneX + phoneW * 0.32f, phoneY + 2.5f),
            size = Size(phoneW * 0.36f, 4f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // 4. Floating Email Envelope in foreground
        val envW = w * 0.32f
        val envH = h * 0.28f
        val envX = w * 0.46f
        val envY = h * 0.44f

        // Envelope Base
        drawRoundRect(
            color = BansiGreen,
            topLeft = Offset(envX, envY),
            size = Size(envW, envH),
            cornerRadius = CornerRadius(8f, 8f)
        )
        // Envelope Flap
        val flapPath = Path().apply {
            moveTo(envX, envY)
            lineTo(envX + envW * 0.5f, envY + envH * 0.55f)
            lineTo(envX + envW, envY)
            close()
        }
        drawPath(flapPath, color = BansiGreenDark)

        // 5. Message Notification Dot (Top right of envelope)
        drawCircle(
            color = Color(0xFFEF4444),
            radius = 7f,
            center = Offset(envX + envW - 2f, envY + 2f)
        )
        drawCircle(
            color = Color.White,
            radius = 2.5f,
            center = Offset(envX + envW - 2f, envY + 2f)
        )

        // 6. Signal Arcs from phone
        val arcCenter = Offset(phoneX + phoneW * 0.25f, phoneY + phoneH * 0.28f)
        drawCircle(
            color = AccentOrange.copy(alpha = 0.5f),
            radius = 16f,
            center = arcCenter,
            style = Stroke(width = 2.5f)
        )
        drawCircle(
            color = AccentOrange,
            radius = 8f,
            center = arcCenter,
            style = Stroke(width = 2.5f)
        )
    }
}

/**
 * Choose Password Illustration (Registration Step 3 - Matching Inspo 2)
 */
@Composable
fun ChoosePasswordIllustration(
    modifier: Modifier = Modifier,
    width: Dp = 190.dp,
    height: Dp = 150.dp
) {
    Canvas(modifier = modifier.size(width, height)) {
        val w = this.size.width
        val h = this.size.height

        // 1. Soft Circular Background
        drawCircle(
            color = Color(0xFFF1F5F9),
            radius = w * 0.38f,
            center = Offset(w * 0.50f, h * 0.50f)
        )

        // 2. Leafy Orange Branch in background right
        val leafPaint = Color(0xFFFB923C)
        val leafPath = Path().apply {
            moveTo(w * 0.72f, h * 0.36f)
            quadraticTo(w * 0.88f, h * 0.38f, w * 0.82f, h * 0.50f)
            quadraticTo(w * 0.74f, h * 0.54f, w * 0.72f, h * 0.46f)
        }
        drawPath(leafPath, color = leafPaint)

        // 3. Orange Padlock in background top right
        val lockLeft = w * 0.65f
        val lockTop = h * 0.14f
        val lockWidth = w * 0.20f
        val lockHeight = h * 0.22f

        // Shackle
        drawRoundRect(
            color = Color(0xFFEA580C),
            topLeft = Offset(lockLeft + lockWidth * 0.22f, lockTop - lockHeight * 0.32f),
            size = Size(lockWidth * 0.56f, lockHeight * 0.5f),
            cornerRadius = CornerRadius(10f, 10f),
            style = Stroke(width = 6f)
        )
        // Lock body
        drawRoundRect(
            color = Color(0xFFF97316),
            topLeft = Offset(lockLeft, lockTop),
            size = Size(lockWidth, lockHeight),
            cornerRadius = CornerRadius(10f, 10f)
        )
        // Keyhole
        drawCircle(
            color = Color.White,
            radius = 5f,
            center = Offset(lockLeft + lockWidth * 0.5f, lockTop + lockHeight * 0.40f)
        )
        drawRect(
            color = Color.White,
            topLeft = Offset(lockLeft + lockWidth * 0.46f, lockTop + lockHeight * 0.40f),
            size = Size(lockWidth * 0.08f, lockHeight * 0.24f)
        )

        // 4. Smartphone Center Display
        val phoneLeft = w * 0.34f
        val phoneTop = h * 0.18f
        val phoneW = w * 0.36f
        val phoneH = h * 0.66f

        drawRoundRect(
            color = Color(0xFF334155),
            topLeft = Offset(phoneLeft, phoneTop),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(16f, 16f),
            style = Stroke(width = 4.5f)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(phoneLeft + 2.5f, phoneTop + 2.5f),
            size = Size(phoneW - 5f, phoneH - 5f),
            cornerRadius = CornerRadius(14f, 14f),
            style = Fill
        )

        // Camera notch
        drawRoundRect(
            color = Color(0xFF334155),
            topLeft = Offset(phoneLeft + phoneW * 0.35f, phoneTop + 2.5f),
            size = Size(phoneW * 0.3f, 4f),
            cornerRadius = CornerRadius(2f, 2f)
        )

        // Orange password bullet dots on phone
        drawRoundRect(
            color = Color(0xFFF1F5F9),
            topLeft = Offset(phoneLeft + phoneW * 0.15f, phoneTop + phoneH * 0.38f),
            size = Size(phoneW * 0.7f, 10f),
            cornerRadius = CornerRadius(3f, 3f)
        )
        for (i in 0..4) {
            drawCircle(
                color = Color(0xFFEA580C),
                radius = 2.5f,
                center = Offset(phoneLeft + phoneW * 0.25f + (i * 7f), phoneTop + phoneH * 0.38f + 5f)
            )
        }

        // Green button inside phone screen
        drawRoundRect(
            color = BansiGreen,
            topLeft = Offset(phoneLeft + phoneW * 0.18f, phoneTop + phoneH * 0.60f),
            size = Size(phoneW * 0.64f, 11f),
            cornerRadius = CornerRadius(3.5f, 3.5f)
        )

        // 5. Thumbs Up Speech Bubble Left
        val bubbleX = w * 0.22f
        val bubbleY = h * 0.36f
        drawCircle(
            color = Color.White,
            radius = 15f,
            center = Offset(bubbleX, bubbleY)
        )
        drawCircle(
            color = Color(0xFFF97316),
            radius = 15f,
            center = Offset(bubbleX, bubbleY),
            style = Stroke(width = 2.5f)
        )
        // Thumbs-up glyph
        drawRoundRect(
            color = Color(0xFFF97316),
            topLeft = Offset(bubbleX - 5f, bubbleY - 4f),
            size = Size(9f, 8f),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawRoundRect(
            color = Color(0xFFF97316),
            topLeft = Offset(bubbleX - 2f, bubbleY - 8f),
            size = Size(4f, 5f),
            cornerRadius = CornerRadius(1.5f, 1.5f)
        )

        // 6. Ground baseline
        drawLine(
            color = Color(0xFFCBD5E1),
            start = Offset(w * 0.16f, h * 0.88f),
            end = Offset(w * 0.84f, h * 0.88f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Login Shield & Key Illustration
 */
@Composable
fun LoginIllustration(
    modifier: Modifier = Modifier,
    width: Dp = 180.dp,
    height: Dp = 140.dp
) {
    Canvas(modifier = modifier.size(width, height)) {
        val w = this.size.width
        val h = this.size.height

        // Soft Background
        drawCircle(
            color = Color(0xFFF1F5F9),
            radius = w * 0.38f,
            center = Offset(w * 0.50f, h * 0.52f)
        )

        // Shield in Royal Navy & Bansi Green
        val shieldCenter = Offset(w * 0.50f, h * 0.48f)
        val shieldPath = Path().apply {
            moveTo(shieldCenter.x - w * 0.22f, shieldCenter.y - h * 0.20f)
            lineTo(shieldCenter.x + w * 0.22f, shieldCenter.y - h * 0.20f)
            lineTo(shieldCenter.x + w * 0.22f, shieldCenter.y + h * 0.06f)
            quadraticTo(shieldCenter.x, shieldCenter.y + h * 0.26f, shieldCenter.x, shieldCenter.y + h * 0.28f)
            quadraticTo(shieldCenter.x, shieldCenter.y + h * 0.26f, shieldCenter.x - w * 0.22f, shieldCenter.y + h * 0.06f)
            close()
        }
        drawPath(shieldPath, color = Color(0xFFE9F7DA))
        drawPath(shieldPath, color = BansiGreen, style = Stroke(width = 4.5f))

        // Checkmark inside shield
        val checkPath = Path().apply {
            moveTo(shieldCenter.x - 14f, shieldCenter.y)
            lineTo(shieldCenter.x - 3f, shieldCenter.y + 12f)
            lineTo(shieldCenter.x + 18f, shieldCenter.y - 10f)
        }
        drawPath(checkPath, color = BansiGreen, style = Stroke(width = 5.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * Phone Verification Illustration (Matching Inspo 3 - media_1794386412878_0c9f0b49.png)
 */
@Composable
fun PhoneVerificationIllustration(
    modifier: Modifier = Modifier,
    width: Dp = 200.dp,
    height: Dp = 160.dp
) {
    Canvas(modifier = modifier.size(width, height)) {
        val w = this.size.width
        val h = this.size.height

        // 1. Organic soft warm background blob
        val bgBlob = Path().apply {
            moveTo(w * 0.25f, h * 0.25f)
            quadraticTo(w * 0.50f, h * 0.12f, w * 0.80f, h * 0.26f)
            quadraticTo(w * 0.95f, h * 0.50f, w * 0.85f, h * 0.68f)
            quadraticTo(w * 0.50f, h * 0.82f, w * 0.15f, h * 0.65f)
            quadraticTo(w * 0.08f, h * 0.40f, w * 0.25f, h * 0.25f)
            close()
        }
        drawPath(bgBlob, color = Color(0xFFFFF7ED))

        // 2. Leafy green branch on the Left
        val stemGreen = Color(0xFF4C8E0F)
        val leafGreen = Color(0xFF65B718)
        val stemPath = Path().apply {
            moveTo(w * 0.34f, h * 0.70f)
            quadraticTo(w * 0.26f, h * 0.45f, w * 0.35f, h * 0.20f)
        }
        drawPath(stemPath, color = stemGreen, style = Stroke(width = 3.5f, cap = StrokeCap.Round))

        val leafPositions = listOf(
            Offset(w * 0.35f, h * 0.22f),
            Offset(w * 0.31f, h * 0.28f),
            Offset(w * 0.36f, h * 0.34f),
            Offset(w * 0.29f, h * 0.40f),
            Offset(w * 0.34f, h * 0.47f),
            Offset(w * 0.28f, h * 0.54f),
            Offset(w * 0.33f, h * 0.62f)
        )
        for (pos in leafPositions) {
            drawOval(
                color = leafGreen,
                topLeft = Offset(pos.x - 7f, pos.y - 10f),
                size = Size(14f, 20f)
            )
        }

        // 3. Center Smartphone
        val phoneW = w * 0.34f
        val phoneH = h * 0.62f
        val phoneX = w * 0.35f
        val phoneY = h * 0.24f

        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(phoneX, phoneY),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(18f, 18f),
            style = Stroke(width = 5.5f)
        )

        drawRoundRect(
            color = Color(0xFFF97316),
            topLeft = Offset(phoneX + 3f, phoneY + 3f),
            size = Size(phoneW - 6f, phoneH - 6f),
            cornerRadius = CornerRadius(15f, 15f),
            style = Fill
        )

        // Shadow ellipse
        drawOval(
            color = Color(0xFFE2E8F0),
            topLeft = Offset(w * 0.24f, h * 0.88f),
            size = Size(w * 0.52f, 12f)
        )
    }
}

/**
 * Splash Page 1: Fleet Dispatch Bike Illustration
 */
@Composable
fun SplashFleetIllustration(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawOval(
            color = Color(0xFFE2E8F0),
            topLeft = Offset(w * 0.12f, h * 0.84f),
            size = Size(w * 0.76f, 16f)
        )

        val wheelRadius = w * 0.14f
        val rearWheel = Offset(w * 0.30f, h * 0.72f)
        val frontWheel = Offset(w * 0.72f, h * 0.72f)

        drawCircle(color = Color(0xFF1E293B), radius = wheelRadius, center = rearWheel)
        drawCircle(color = Color.White, radius = wheelRadius * 0.65f, center = rearWheel)
        drawCircle(color = Color(0xFF64748B), radius = wheelRadius * 0.25f, center = rearWheel)

        drawCircle(color = Color(0xFF1E293B), radius = wheelRadius, center = frontWheel)
        drawCircle(color = Color.White, radius = wheelRadius * 0.65f, center = frontWheel)
        drawCircle(color = Color(0xFF64748B), radius = wheelRadius * 0.25f, center = frontWheel)

        val framePath = Path().apply {
            moveTo(rearWheel.x, rearWheel.y)
            lineTo(w * 0.48f, h * 0.52f)
            lineTo(frontWheel.x, frontWheel.y)
            lineTo(w * 0.66f, h * 0.42f)
            lineTo(w * 0.48f, h * 0.52f)
        }
        drawPath(framePath, color = BansiGreen, style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawLine(
            color = Color(0xFF334155),
            start = Offset(w * 0.66f, h * 0.42f),
            end = Offset(w * 0.64f, h * 0.34f),
            strokeWidth = 7f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF0F172A),
            start = Offset(w * 0.60f, h * 0.34f),
            end = Offset(w * 0.68f, h * 0.34f),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )

        val boxLeft = w * 0.22f
        val boxTop = h * 0.36f
        val boxW = w * 0.24f
        val boxH = h * 0.24f

        drawRoundRect(
            color = RoyalNavy,
            topLeft = Offset(boxLeft, boxTop),
            size = Size(boxW, boxH),
            cornerRadius = CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = BansiGreen,
            topLeft = Offset(boxLeft, boxTop + boxH * 0.35f),
            size = Size(boxW, boxH * 0.30f),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }
}

/**
 * Splash Page 2: Smart Dijkstra Routing Illustration
 */
@Composable
fun SplashNavigationIllustration(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val mapPath = Path().apply {
            moveTo(w * 0.18f, h * 0.24f)
            lineTo(w * 0.42f, h * 0.18f)
            lineTo(w * 0.62f, h * 0.24f)
            lineTo(w * 0.84f, h * 0.18f)
            lineTo(w * 0.84f, h * 0.74f)
            lineTo(w * 0.62f, h * 0.80f)
            lineTo(w * 0.42f, h * 0.74f)
            lineTo(w * 0.18f, h * 0.80f)
            close()
        }
        drawPath(mapPath, color = Color(0xFFF1F5F9))
        drawPath(mapPath, color = Color(0xFFCBD5E1), style = Stroke(width = 3f))

        drawLine(color = Color(0xFFE2E8F0), start = Offset(w * 0.24f, h * 0.35f), end = Offset(w * 0.78f, h * 0.45f), strokeWidth = 8f, cap = StrokeCap.Round)
        drawLine(color = Color(0xFFE2E8F0), start = Offset(w * 0.35f, h * 0.65f), end = Offset(w * 0.65f, h * 0.30f), strokeWidth = 8f, cap = StrokeCap.Round)

        val routePath = Path().apply {
            moveTo(w * 0.26f, h * 0.62f)
            lineTo(w * 0.45f, h * 0.55f)
            lineTo(w * 0.52f, h * 0.38f)
            lineTo(w * 0.74f, h * 0.32f)
        }
        drawPath(routePath, color = BansiGreen, style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawCircle(color = RoyalNavy, radius = 9f, center = Offset(w * 0.26f, h * 0.62f))
        drawCircle(color = Color.White, radius = 4f, center = Offset(w * 0.26f, h * 0.62f))

        val pinCenter = Offset(w * 0.74f, h * 0.32f)
        drawCircle(color = BansiCrimson, radius = 13f, center = Offset(pinCenter.x, pinCenter.y - 12f))
        val pinPointer = Path().apply {
            moveTo(pinCenter.x - 9f, pinCenter.y - 8f)
            lineTo(pinCenter.x, pinCenter.y + 4f)
            lineTo(pinCenter.x + 9f, pinCenter.y - 8f)
            close()
        }
        drawPath(pinPointer, color = BansiCrimson)
        drawCircle(color = Color.White, radius = 5f, center = Offset(pinCenter.x, pinCenter.y - 12f))
    }
}

/**
 * Splash Page 3: Instant Earnings & Handover
 */
@Composable
fun SplashEarningsIllustration(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val shieldCenter = Offset(w * 0.50f, h * 0.46f)
        val shieldPath = Path().apply {
            moveTo(shieldCenter.x - w * 0.26f, shieldCenter.y - h * 0.22f)
            lineTo(shieldCenter.x + w * 0.26f, shieldCenter.y - h * 0.22f)
            lineTo(shieldCenter.x + w * 0.26f, shieldCenter.y + h * 0.08f)
            quadraticTo(shieldCenter.x, shieldCenter.y + h * 0.32f, shieldCenter.x, shieldCenter.y + h * 0.35f)
            quadraticTo(shieldCenter.x, shieldCenter.y + h * 0.32f, shieldCenter.x - w * 0.26f, shieldCenter.y + h * 0.08f)
            close()
        }
        drawPath(shieldPath, color = Color(0xFFE9F7DA))
        drawPath(shieldPath, color = BansiGreen, style = Stroke(width = 6f))

        val checkPath = Path().apply {
            moveTo(shieldCenter.x - 18f, shieldCenter.y)
            lineTo(shieldCenter.x - 4f, shieldCenter.y + 16f)
            lineTo(shieldCenter.x + 22f, shieldCenter.y - 14f)
        }
        drawPath(checkPath, color = BansiGreen, style = Stroke(width = 7f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val coinCenter = Offset(w * 0.72f, h * 0.68f)
        drawCircle(color = Color(0xFFF59E0B), radius = 24f, center = coinCenter)
        drawCircle(color = Color(0xFFD97706), radius = 24f, center = coinCenter, style = Stroke(width = 3.5f))
        drawCircle(color = Color(0xFFFEF3C7), radius = 17f, center = coinCenter)
    }
}

/**
 * Email Verification Code Entry Illustration
 */
@Composable
fun EmailVerificationIllustration(
    modifier: Modifier = Modifier,
    width: Dp = 200.dp,
    height: Dp = 160.dp
) {
    Canvas(modifier = modifier.size(width, height)) {
        val w = this.size.width
        val h = this.size.height

        // Soft organic background blob
        val bgBlob = Path().apply {
            moveTo(w * 0.20f, h * 0.25f)
            quadraticTo(w * 0.50f, h * 0.08f, w * 0.82f, h * 0.24f)
            quadraticTo(w * 0.96f, h * 0.50f, w * 0.80f, h * 0.72f)
            quadraticTo(w * 0.50f, h * 0.86f, w * 0.16f, h * 0.68f)
            quadraticTo(w * 0.06f, h * 0.42f, w * 0.20f, h * 0.25f)
            close()
        }
        drawPath(bgBlob, color = Color(0xFFEFF6FF))

        // Decorative leafy accent on left
        val stemPath = Path().apply {
            moveTo(w * 0.22f, h * 0.74f)
            quadraticTo(w * 0.15f, h * 0.52f, w * 0.24f, h * 0.30f)
        }
        drawPath(stemPath, color = Color(0xFF4C8E0F), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
        listOf(
            Offset(w * 0.24f, h * 0.32f),
            Offset(w * 0.19f, h * 0.42f),
            Offset(w * 0.23f, h * 0.52f),
            Offset(w * 0.18f, h * 0.62f)
        ).forEach { pos ->
            drawOval(
                color = Color(0xFF65B718),
                topLeft = Offset(pos.x - 7f, pos.y - 10f),
                size = Size(14f, 20f)
            )
        }

        // Main Envelope Body
        val envW = w * 0.52f
        val envH = h * 0.42f
        val envX = w * 0.27f
        val envY = h * 0.34f

        drawRoundRect(
            color = Color(0xFFFFFFFF),
            topLeft = Offset(envX, envY),
            size = Size(envW, envH),
            cornerRadius = CornerRadius(14f, 14f)
        )
        drawRoundRect(
            color = RoyalNavy,
            topLeft = Offset(envX, envY),
            size = Size(envW, envH),
            cornerRadius = CornerRadius(14f, 14f),
            style = Stroke(width = 5f)
        )

        // Envelope Flap
        val flapPath = Path().apply {
            moveTo(envX + 4f, envY + 6f)
            lineTo(envX + envW / 2f, envY + envH * 0.56f)
            lineTo(envX + envW - 4f, envY + 6f)
        }
        drawPath(
            flapPath,
            color = RoyalNavy,
            style = Stroke(width = 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Notification / OTP Badge on top-right of envelope
        val badgeCenter = Offset(envX + envW - 6f, envY + 6f)
        drawCircle(color = AccentOrange, radius = 20f, center = badgeCenter)
        drawCircle(color = Color.White, radius = 20f, center = badgeCenter, style = Stroke(width = 3.5f))
        // Three small dots inside badge representing 6-digit code
        drawCircle(color = Color.White, radius = 2.8f, center = Offset(badgeCenter.x - 7f, badgeCenter.y))
        drawCircle(color = Color.White, radius = 2.8f, center = badgeCenter)
        drawCircle(color = Color.White, radius = 2.8f, center = Offset(badgeCenter.x + 7f, badgeCenter.y))

        // Ground shadow
        drawOval(
            color = Color(0xFFE2E8F0),
            topLeft = Offset(w * 0.24f, h * 0.85f),
            size = Size(w * 0.54f, 12f)
        )
    }
}

/**
 * Email Verified Confirmation Success Illustration
 */
@Composable
fun EmailVerifiedSuccessIllustration(
    modifier: Modifier = Modifier,
    width: Dp = 220.dp,
    height: Dp = 190.dp
) {
    Canvas(modifier = modifier.size(width, height)) {
        val w = this.size.width
        val h = this.size.height

        // Soft emerald organic background blob
        val bgBlob = Path().apply {
            moveTo(w * 0.22f, h * 0.20f)
            quadraticTo(w * 0.52f, h * 0.06f, w * 0.82f, h * 0.22f)
            quadraticTo(w * 0.94f, h * 0.48f, w * 0.78f, h * 0.74f)
            quadraticTo(w * 0.48f, h * 0.88f, w * 0.18f, h * 0.70f)
            quadraticTo(w * 0.06f, h * 0.42f, w * 0.22f, h * 0.20f)
            close()
        }
        drawPath(bgBlob, color = Color(0xFFECFDF5))

        // Outer soft halo ring
        val center = Offset(w * 0.50f, h * 0.46f)
        drawCircle(
            color = Color(0xFFD1FAE5),
            radius = w * 0.26f,
            center = center
        )

        // Crisp white card / seal
        drawCircle(
            color = Color(0xFFFFFFFF),
            radius = w * 0.20f,
            center = center
        )
        drawCircle(
            color = BansiGreen,
            radius = w * 0.20f,
            center = center,
            style = Stroke(width = 6f)
        )

        // Inner emerald badge
        drawCircle(
            color = BansiGreen,
            radius = w * 0.145f,
            center = center
        )

        // Bold white checkmark
        val checkPath = Path().apply {
            moveTo(center.x - w * 0.065f, center.y + h * 0.005f)
            lineTo(center.x - w * 0.015f, center.y + h * 0.060f)
            lineTo(center.x + w * 0.075f, center.y - h * 0.055f)
        }
        drawPath(
            checkPath,
            color = Color.White,
            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Celebratory sparkle accents
        drawCircle(color = BansiGreen, radius = 6f, center = Offset(w * 0.22f, h * 0.26f))
        drawCircle(color = Color(0xFFF59E0B), radius = 5f, center = Offset(w * 0.80f, h * 0.28f))
        drawCircle(color = RoyalNavy, radius = 4.5f, center = Offset(w * 0.78f, h * 0.66f))
        drawCircle(color = Color(0xFF10B981), radius = 5f, center = Offset(w * 0.24f, h * 0.66f))

        // Ground shadow
        drawOval(
            color = Color(0xFFE2E8F0),
            topLeft = Offset(w * 0.26f, h * 0.86f),
            size = Size(w * 0.48f, 12f)
        )
    }
}
