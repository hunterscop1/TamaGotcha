package com.example.ui.pet

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import com.example.data.model.EvolutionStage
import com.example.data.model.PetMood
import com.example.data.model.PetSpecies
import kotlin.math.sin

enum class PetAnimationState {
    IDLE,
    TALKING,
    EATING,
    SLEEPING,
    ALERT,
    CELEBRATING
}

@Composable
fun PetSpriteCanvas(
    species: PetSpecies,
    evolutionStage: EvolutionStage,
    animationState: PetAnimationState,
    happiness: Float,
    isSleeping: Boolean,
    mood: PetMood = PetMood.HAPPY,
    equippedHat: String = "hat_none",
    equippedAccessory: String = "acc_none",
    equippedPalette: String = "pal_default",
    equippedBackground: String = "bg_default",
    onPetClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "PetAnimation")

    val bounceY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isSleeping) 4f else if (animationState == PetAnimationState.ALERT || mood == PetMood.STRESSED) -14f else if (mood == PetMood.ECSTATIC) -16f else -9f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isSleeping) 1800 else if (animationState == PetAnimationState.ALERT || mood == PetMood.STRESSED) 300 else if (mood == PetMood.ECSTATIC) 400 else 650,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BounceY"
    )

    val scaleY by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isSleeping) 0.94f else 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSleeping) 1800 else 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScaleY"
    )

    val mouthOpenRatio by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 220, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Mouth"
    )

    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Particles"
    )

    Box(
        modifier = modifier
            .testTag("pet_sprite_container")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onPetClicked
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f + bounceY

            // 1. Draw LCD Scenery Background Theme
            drawLcdBackground(equippedBackground, size.width, size.height)

            // 2. Draw Ground Shadow
            drawOval(
                color = Color(0x2E000000),
                topLeft = Offset(cx - 52f, size.height * 0.76f),
                size = Size(104f, 18f)
            )

            // 3. Draw Pet Body
            val paletteTint = getPaletteTint(equippedPalette)

            if (evolutionStage == EvolutionStage.EGG) {
                drawEggCharacter(cx, cy, scaleY, paletteTint)
            } else {
                when (species) {
                    PetSpecies.STAR_BUNNY -> drawBunny(cx, cy, scaleY, animationState, isSleeping, mouthOpenRatio, evolutionStage, mood, paletteTint)
                    PetSpecies.CHIBI_DRAGON -> drawDragon(cx, cy, scaleY, animationState, isSleeping, mouthOpenRatio, evolutionStage, mood, paletteTint)
                    PetSpecies.COSMIC_KITTY -> drawCat(cx, cy, scaleY, animationState, isSleeping, mouthOpenRatio, evolutionStage, mood, paletteTint)
                    PetSpecies.ROBO_PUP -> drawRoboPup(cx, cy, scaleY, animationState, isSleeping, mouthOpenRatio, evolutionStage, mood, paletteTint)
                    PetSpecies.AXO_LOTL -> drawAxolotl(cx, cy, scaleY, animationState, isSleeping, mouthOpenRatio, evolutionStage, mood, paletteTint)
                    PetSpecies.GHOSTY_BOO -> drawGhost(cx, cy, scaleY, animationState, isSleeping, mouthOpenRatio, evolutionStage, mood, paletteTint)
                    PetSpecies.PENGUIN_PIP -> drawPenguin(cx, cy, scaleY, animationState, isSleeping, mouthOpenRatio, evolutionStage, mood, paletteTint)
                }
            }

            // 4. Draw Equipped Accessory
            drawEquippedAccessory(equippedAccessory, cx, cy, evolutionStage)

            // 5. Draw Equipped Hat
            drawEquippedHat(equippedHat, cx, cy, species, evolutionStage)

            // 6. Draw Mood & State Overlay Particle FX
            if (isSleeping) {
                drawZzzBubbles(cx + 45f, cy - 30f + particleOffset)
            } else if (animationState == PetAnimationState.ALERT || mood == PetMood.STRESSED) {
                drawUrgentAlertBadge(cx, cy - 72f)
            } else if (mood == PetMood.ECSTATIC || animationState == PetAnimationState.CELEBRATING || happiness > 85f) {
                drawHappySparkles(cx, cy - 50f + particleOffset)
            } else if (mood == PetMood.HUNGRY) {
                drawHungryTummyBubble(cx + 40f, cy - 25f + particleOffset)
            } else if (mood == PetMood.FOCUS_MODE) {
                drawFocusAura(cx, cy - 65f)
            }
        }
    }
}

private fun getPaletteTint(paletteId: String): Color? {
    return when (paletteId) {
        "pal_sakura" -> Color(0xFFFFC0CB)
        "pal_cyber" -> Color(0xFF67E8F9)
        "pal_lavender" -> Color(0xFFE9D5FF)
        "pal_gold" -> Color(0xFFFDE68A)
        "pal_shadow" -> Color(0xFF475569)
        else -> null
    }
}

private fun DrawScope.drawLcdBackground(bgId: String, w: Float, h: Float) {
    when (bgId) {
        "bg_study" -> {
            // Bookshelf line
            drawLine(Color(0x333F2C1D), Offset(10f, h * 0.72f), Offset(w - 10f, h * 0.72f), strokeWidth = 3f)
            // Books
            drawRect(Color(0x448B5A2B), Offset(20f, h * 0.58f), Size(12f, h * 0.14f))
            drawRect(Color(0x442E6B4F), Offset(34f, h * 0.55f), Size(14f, h * 0.17f))
            drawRect(Color(0x44B85D19), Offset(50f, h * 0.60f), Size(11f, h * 0.12f))
            // Clock on wall
            drawCircle(Color(0x33000000), radius = 14f, center = Offset(w - 35f, 35f), style = Stroke(width = 2f))
            drawLine(Color(0x33000000), Offset(w - 35f, 35f), Offset(w - 35f, 26f), strokeWidth = 2f)
            drawLine(Color(0x33000000), Offset(w - 35f, 35f), Offset(w - 28f, 35f), strokeWidth = 2f)
        }
        "bg_cyber" -> {
            // Neon grid lines
            for (i in 1..4) {
                val y = h * 0.65f + (i * 18f)
                drawLine(Color(0x2200F0FF), Offset(0f, y), Offset(w, y), strokeWidth = 1.5f)
            }
            // Pixel stars
            drawCircle(Color(0x4400F0FF), radius = 2.5f, center = Offset(30f, 30f))
            drawCircle(Color(0x4400F0FF), radius = 3f, center = Offset(w - 40f, 45f))
            drawCircle(Color(0x4400F0FF), radius = 2f, center = Offset(w * 0.4f, 25f))
        }
        "bg_zen" -> {
            // Bamboo stalks
            drawLine(Color(0x332E5D38), Offset(25f, 15f), Offset(25f, h * 0.75f), strokeWidth = 4f)
            drawLine(Color(0x332E5D38), Offset(w - 25f, 15f), Offset(w - 25f, h * 0.75f), strokeWidth = 4f)
            // Bamboo segments
            drawCircle(Color(0x442E5D38), radius = 4f, center = Offset(25f, 45f))
            drawCircle(Color(0x442E5D38), radius = 4f, center = Offset(25f, 90f))
            drawCircle(Color(0x442E5D38), radius = 4f, center = Offset(w - 25f, 50f))
        }
        "bg_cosmic" -> {
            // Planet with ring
            drawCircle(Color(0x339333EA), radius = 16f, center = Offset(w - 40f, 40f))
            drawOval(Color(0x33C084FC), topLeft = Offset(w - 60f, 36f), size = Size(40f, 8f), style = Stroke(width = 2f))
            // Constellation dots
            drawCircle(Color(0x55FDE047), radius = 2f, center = Offset(30f, 25f))
            drawCircle(Color(0x55FDE047), radius = 2f, center = Offset(50f, 35f))
            drawCircle(Color(0x55FDE047), radius = 2.5f, center = Offset(40f, 55f))
        }
        "bg_arcade" -> {
            // Retro 8-bit clouds
            drawOval(Color(0x33000000), topLeft = Offset(20f, 25f), size = Size(35f, 14f))
            drawOval(Color(0x33000000), topLeft = Offset(w - 60f, 35f), size = Size(40f, 16f))
            // Pixel ground dots
            for (x in 15..(w.toInt() - 15) step 20) {
                drawCircle(Color(0x22000000), radius = 1.5f, center = Offset(x.toFloat(), h * 0.78f))
            }
        }
    }
}

private fun DrawScope.drawEggCharacter(cx: Float, cy: Float, scaleY: Float, tint: Color?) {
    val eggColor = tint ?: Color(0xFFFFFBEB)
    val eggSpot = Color(0xFFFDE68A)
    val eggOutline = Color(0xFF78350F)

    drawOval(
        color = eggColor,
        topLeft = Offset(cx - 36f, cy - 45f * scaleY),
        size = Size(72f, 90f * scaleY)
    )
    drawOval(
        color = eggOutline,
        topLeft = Offset(cx - 36f, cy - 45f * scaleY),
        size = Size(72f, 90f * scaleY),
        style = Stroke(width = 4f)
    )
    drawCircle(eggSpot, radius = 10f, center = Offset(cx - 12f, cy - 10f))
    drawCircle(eggSpot, radius = 7f, center = Offset(cx + 15f, cy + 12f))

    drawCircle(eggOutline, radius = 3.5f, center = Offset(cx - 10f, cy + 4f))
    drawCircle(eggOutline, radius = 3.5f, center = Offset(cx + 10f, cy + 4f))
    drawCircle(Color(0xFFF472B6), radius = 4f, center = Offset(cx - 18f, cy + 8f))
    drawCircle(Color(0xFFF472B6), radius = 4f, center = Offset(cx + 18f, cy + 8f))
}

private fun DrawScope.drawBunny(
    cx: Float,
    cy: Float,
    scaleY: Float,
    animState: PetAnimationState,
    isSleeping: Boolean,
    mouthRatio: Float,
    stage: EvolutionStage,
    mood: PetMood,
    tint: Color?
) {
    val bodyColor = tint ?: Color(0xFFFFFFFF)
    val earInnerColor = Color(0xFFFFB6C1)
    val outlineColor = Color(0xFF1E293B)
    val blushColor = Color(0xFFFB7185)
    val bodyRadius = if (stage == EvolutionStage.ADULT || stage == EvolutionStage.MYTHIC) 46f else 38f

    // Ears
    val earPathL = Path().apply {
        moveTo(cx - 24f, cy - bodyRadius + 10f)
        cubicTo(cx - 32f, cy - bodyRadius - 55f, cx - 14f, cy - bodyRadius - 60f, cx - 8f, cy - bodyRadius + 5f)
        close()
    }
    val earPathR = Path().apply {
        moveTo(cx + 8f, cy - bodyRadius + 5f)
        cubicTo(cx + 14f, cy - bodyRadius - 60f, cx + 32f, cy - bodyRadius - 55f, cx + 24f, cy - bodyRadius + 10f)
        close()
    }

    drawPath(earPathL, bodyColor)
    drawPath(earPathL, outlineColor, style = Stroke(width = 4f))
    drawPath(earPathR, bodyColor)
    drawPath(earPathR, outlineColor, style = Stroke(width = 4f))

    drawOval(earInnerColor, topLeft = Offset(cx - 22f, cy - bodyRadius - 40f), size = Size(10f, 35f))
    drawOval(earInnerColor, topLeft = Offset(cx + 12f, cy - bodyRadius - 40f), size = Size(10f, 35f))

    // Body
    drawCircle(bodyColor, radius = bodyRadius, center = Offset(cx, cy))
    drawCircle(outlineColor, radius = bodyRadius, center = Offset(cx, cy), style = Stroke(width = 4f))

    // Cheeks
    drawCircle(blushColor, radius = 6f, center = Offset(cx - 22f, cy + 8f))
    drawCircle(blushColor, radius = 6f, center = Offset(cx + 22f, cy + 8f))

    // Eyes
    drawEyesByMood(cx, cy, isSleeping, mood, outlineColor)

    // Mouth
    drawMouthByMood(cx, cy, animState, mouthRatio, mood, outlineColor)

    // Paws
    drawCircle(bodyColor, radius = 8f, center = Offset(cx - 16f, cy + bodyRadius - 4f))
    drawCircle(outlineColor, radius = 8f, center = Offset(cx - 16f, cy + bodyRadius - 4f), style = Stroke(width = 3f))
    drawCircle(bodyColor, radius = 8f, center = Offset(cx + 16f, cy + bodyRadius - 4f))
    drawCircle(outlineColor, radius = 8f, center = Offset(cx + 16f, cy + bodyRadius - 4f), style = Stroke(width = 3f))
}

private fun DrawScope.drawDragon(
    cx: Float,
    cy: Float,
    scaleY: Float,
    animState: PetAnimationState,
    isSleeping: Boolean,
    mouthRatio: Float,
    stage: EvolutionStage,
    mood: PetMood,
    tint: Color?
) {
    val bodyColor = tint ?: Color(0xFF34D399)
    val bellyColor = Color(0xFFFEF08A)
    val hornColor = Color(0xFFF97316)
    val outlineColor = Color(0xFF064E3B)
    val bodyRadius = 40f

    // Horns
    val hornL = Path().apply {
        moveTo(cx - 20f, cy - bodyRadius + 5f)
        lineTo(cx - 34f, cy - bodyRadius - 25f)
        lineTo(cx - 10f, cy - bodyRadius)
        close()
    }
    val hornR = Path().apply {
        moveTo(cx + 10f, cy - bodyRadius)
        lineTo(cx + 34f, cy - bodyRadius - 25f)
        lineTo(cx + 20f, cy - bodyRadius + 5f)
        close()
    }
    drawPath(hornL, hornColor)
    drawPath(hornL, outlineColor, style = Stroke(width = 3.5f))
    drawPath(hornR, hornColor)
    drawPath(hornR, outlineColor, style = Stroke(width = 3.5f))

    // Wings
    val wingL = Path().apply {
        moveTo(cx - bodyRadius + 5f, cy - 5f)
        lineTo(cx - bodyRadius - 30f, cy - 25f)
        lineTo(cx - bodyRadius - 20f, cy + 10f)
        close()
    }
    val wingR = Path().apply {
        moveTo(cx + bodyRadius - 5f, cy - 5f)
        lineTo(cx + bodyRadius + 30f, cy - 25f)
        lineTo(cx + bodyRadius + 20f, cy + 10f)
        close()
    }
    drawPath(wingL, Color(0xFF10B981))
    drawPath(wingL, outlineColor, style = Stroke(width = 3f))
    drawPath(wingR, Color(0xFF10B981))
    drawPath(wingR, outlineColor, style = Stroke(width = 3f))

    // Body & Belly
    drawCircle(bodyColor, radius = bodyRadius, center = Offset(cx, cy))
    drawCircle(outlineColor, radius = bodyRadius, center = Offset(cx, cy), style = Stroke(width = 4f))
    drawOval(bellyColor, topLeft = Offset(cx - 20f, cy + 5f), size = Size(40f, 32f))

    drawEyesByMood(cx, cy, isSleeping, mood, outlineColor)
    drawMouthByMood(cx, cy, animState, mouthRatio, mood, outlineColor)
}

private fun DrawScope.drawCat(
    cx: Float,
    cy: Float,
    scaleY: Float,
    animState: PetAnimationState,
    isSleeping: Boolean,
    mouthRatio: Float,
    stage: EvolutionStage,
    mood: PetMood,
    tint: Color?
) {
    val bodyColor = tint ?: Color(0xFFFFEDD5)
    val earInner = Color(0xFFF472B6)
    val outlineColor = Color(0xFF431407)
    val bodyRadius = 39f

    // Ears
    val earL = Path().apply {
        moveTo(cx - 28f, cy - bodyRadius + 12f)
        lineTo(cx - 32f, cy - bodyRadius - 28f)
        lineTo(cx - 8f, cy - bodyRadius + 2f)
        close()
    }
    val earR = Path().apply {
        moveTo(cx + 8f, cy - bodyRadius + 2f)
        lineTo(cx + 32f, cy - bodyRadius - 28f)
        lineTo(cx + 28f, cy - bodyRadius + 12f)
        close()
    }
    drawPath(earL, bodyColor)
    drawPath(earL, outlineColor, style = Stroke(width = 3.5f))
    drawPath(earR, bodyColor)
    drawPath(earR, outlineColor, style = Stroke(width = 3.5f))

    drawPath(Path().apply {
        moveTo(cx - 24f, cy - bodyRadius + 8f); lineTo(cx - 28f, cy - bodyRadius - 18f); lineTo(cx - 12f, cy - bodyRadius + 2f); close()
    }, earInner)
    drawPath(Path().apply {
        moveTo(cx + 12f, cy - bodyRadius + 2f); lineTo(cx + 28f, cy - bodyRadius - 18f); lineTo(cx + 24f, cy - bodyRadius + 8f); close()
    }, earInner)

    // Body
    drawCircle(bodyColor, radius = bodyRadius, center = Offset(cx, cy))
    drawCircle(outlineColor, radius = bodyRadius, center = Offset(cx, cy), style = Stroke(width = 4f))

    // Whiskers
    drawLine(outlineColor, Offset(cx - 25f, cy + 4f), Offset(cx - 45f, cy + 1f), strokeWidth = 2.5f)
    drawLine(outlineColor, Offset(cx - 25f, cy + 10f), Offset(cx - 43f, cy + 12f), strokeWidth = 2.5f)
    drawLine(outlineColor, Offset(cx + 25f, cy + 4f), Offset(cx + 45f, cy + 1f), strokeWidth = 2.5f)
    drawLine(outlineColor, Offset(cx + 25f, cy + 10f), Offset(cx + 43f, cy + 12f), strokeWidth = 2.5f)

    drawEyesByMood(cx, cy, isSleeping, mood, outlineColor)
    drawMouthByMood(cx, cy, animState, mouthRatio, mood, outlineColor)
}

private fun DrawScope.drawRoboPup(
    cx: Float,
    cy: Float,
    scaleY: Float,
    animState: PetAnimationState,
    isSleeping: Boolean,
    mouthRatio: Float,
    stage: EvolutionStage,
    mood: PetMood,
    tint: Color?
) {
    val bodyColor = tint ?: Color(0xFFE2E8F0)
    val earColor = Color(0xFF60A5FA)
    val screenColor = Color(0xFF0F172A)
    val visorGlow = Color(0xFF38BDF8)
    val outlineColor = Color(0xFF1E293B)

    // Antenna
    drawLine(outlineColor, Offset(cx, cy - 40f), Offset(cx, cy - 58f), strokeWidth = 3f)
    drawCircle(Color(0xFFEF4444), radius = 6f, center = Offset(cx, cy - 60f))

    // Ears
    drawRoundRect(earColor, topLeft = Offset(cx - 48f, cy - 30f), size = Size(14f, 35f), cornerRadius = CornerRadius(6f, 6f))
    drawRoundRect(outlineColor, topLeft = Offset(cx - 48f, cy - 30f), size = Size(14f, 35f), cornerRadius = CornerRadius(6f, 6f), style = Stroke(width = 3f))
    drawRoundRect(earColor, topLeft = Offset(cx + 34f, cy - 30f), size = Size(14f, 35f), cornerRadius = CornerRadius(6f, 6f))
    drawRoundRect(outlineColor, topLeft = Offset(cx + 34f, cy - 30f), size = Size(14f, 35f), cornerRadius = CornerRadius(6f, 6f), style = Stroke(width = 3f))

    // Head
    drawRoundRect(bodyColor, topLeft = Offset(cx - 38f, cy - 40f), size = Size(76f, 76f), cornerRadius = CornerRadius(18f, 18f))
    drawRoundRect(outlineColor, topLeft = Offset(cx - 38f, cy - 40f), size = Size(76f, 76f), cornerRadius = CornerRadius(18f, 18f), style = Stroke(width = 4f))

    // Visor
    drawRoundRect(screenColor, topLeft = Offset(cx - 28f, cy - 22f), size = Size(56f, 34f), cornerRadius = CornerRadius(8f, 8f))

    if (isSleeping) {
        drawLine(visorGlow, Offset(cx - 20f, cy - 6f), Offset(cx - 8f, cy - 6f), strokeWidth = 3f)
        drawLine(visorGlow, Offset(cx + 8f, cy - 6f), Offset(cx + 20f, cy - 6f), strokeWidth = 3f)
    } else {
        drawCircle(visorGlow, radius = 5f, center = Offset(cx - 14f, cy - 6f))
        drawCircle(visorGlow, radius = 5f, center = Offset(cx + 14f, cy - 6f))
    }
}

private fun DrawScope.drawAxolotl(
    cx: Float,
    cy: Float,
    scaleY: Float,
    animState: PetAnimationState,
    isSleeping: Boolean,
    mouthRatio: Float,
    stage: EvolutionStage,
    mood: PetMood,
    tint: Color?
) {
    val bodyColor = tint ?: Color(0xFFFFC0CB)
    val frillColor = Color(0xFFF43F5E)
    val outlineColor = Color(0xFF881337)
    val bodyRadius = 38f

    // Axolotl side frills (3 on left, 3 on right)
    for (i in -1..1) {
        val yOff = i * 14f
        drawOval(frillColor, topLeft = Offset(cx - bodyRadius - 22f, cy - 10f + yOff), size = Size(24f, 10f))
        drawOval(outlineColor, topLeft = Offset(cx - bodyRadius - 22f, cy - 10f + yOff), size = Size(24f, 10f), style = Stroke(width = 2.5f))
        drawOval(frillColor, topLeft = Offset(cx + bodyRadius - 2f, cy - 10f + yOff), size = Size(24f, 10f))
        drawOval(outlineColor, topLeft = Offset(cx + bodyRadius - 2f, cy - 10f + yOff), size = Size(24f, 10f), style = Stroke(width = 2.5f))
    }

    // Body
    drawCircle(bodyColor, radius = bodyRadius, center = Offset(cx, cy))
    drawCircle(outlineColor, radius = bodyRadius, center = Offset(cx, cy), style = Stroke(width = 4f))

    // Cheeks
    drawCircle(Color(0xFFFB7185), radius = 6f, center = Offset(cx - 20f, cy + 8f))
    drawCircle(Color(0xFFFB7185), radius = 6f, center = Offset(cx + 20f, cy + 8f))

    drawEyesByMood(cx, cy, isSleeping, mood, outlineColor)
    drawMouthByMood(cx, cy, animState, mouthRatio, mood, outlineColor)
}

private fun DrawScope.drawGhost(
    cx: Float,
    cy: Float,
    scaleY: Float,
    animState: PetAnimationState,
    isSleeping: Boolean,
    mouthRatio: Float,
    stage: EvolutionStage,
    mood: PetMood,
    tint: Color?
) {
    val bodyColor = tint ?: Color(0xFFF8FAFC)
    val outlineColor = Color(0xFF334155)

    // Ghost floating body with wavy skirt bottom
    val ghostPath = Path().apply {
        moveTo(cx - 36f, cy + 25f)
        cubicTo(cx - 38f, cy - 42f, cx + 38f, cy - 42f, cx + 36f, cy + 25f)
        // Wavy bottom
        quadraticTo(cx + 24f, cy + 42f, cx + 12f, cy + 25f)
        quadraticTo(cx, cy + 42f, cx - 12f, cy + 25f)
        quadraticTo(cx - 24f, cy + 42f, cx - 36f, cy + 25f)
        close()
    }
    drawPath(ghostPath, bodyColor)
    drawPath(ghostPath, outlineColor, style = Stroke(width = 4f))

    // Tiny floating ghost arms
    drawOval(bodyColor, topLeft = Offset(cx - 45f, cy + 2f), size = Size(16f, 10f))
    drawOval(outlineColor, topLeft = Offset(cx - 45f, cy + 2f), size = Size(16f, 10f), style = Stroke(width = 3f))
    drawOval(bodyColor, topLeft = Offset(cx + 29f, cy + 2f), size = Size(16f, 10f))
    drawOval(outlineColor, topLeft = Offset(cx + 29f, cy + 2f), size = Size(16f, 10f), style = Stroke(width = 3f))

    drawEyesByMood(cx, cy - 6f, isSleeping, mood, outlineColor)
    drawMouthByMood(cx, cy - 6f, animState, mouthRatio, mood, outlineColor)
}

private fun DrawScope.drawPenguin(
    cx: Float,
    cy: Float,
    scaleY: Float,
    animState: PetAnimationState,
    isSleeping: Boolean,
    mouthRatio: Float,
    stage: EvolutionStage,
    mood: PetMood,
    tint: Color?
) {
    val bodyColor = tint ?: Color(0xFF1E293B)
    val bellyColor = Color(0xFFFFFFFF)
    val beakColor = Color(0xFFF97316)
    val outlineColor = Color(0xFF0F172A)
    val bodyRadius = 38f

    // Flippers
    drawOval(bodyColor, topLeft = Offset(cx - 48f, cy - 8f), size = Size(16f, 32f))
    drawOval(outlineColor, topLeft = Offset(cx - 48f, cy - 8f), size = Size(16f, 32f), style = Stroke(width = 3f))
    drawOval(bodyColor, topLeft = Offset(cx + 32f, cy - 8f), size = Size(16f, 32f))
    drawOval(outlineColor, topLeft = Offset(cx + 32f, cy - 8f), size = Size(16f, 32f), style = Stroke(width = 3f))

    // Main Body
    drawCircle(bodyColor, radius = bodyRadius, center = Offset(cx, cy))
    drawCircle(outlineColor, radius = bodyRadius, center = Offset(cx, cy), style = Stroke(width = 4f))

    // White belly
    drawOval(bellyColor, topLeft = Offset(cx - 24f, cy - 14f), size = Size(48f, 50f))

    // Feet
    drawOval(beakColor, topLeft = Offset(cx - 26f, cy + bodyRadius - 6f), size = Size(18f, 10f))
    drawOval(beakColor, topLeft = Offset(cx + 8f, cy + bodyRadius - 6f), size = Size(18f, 10f))

    drawEyesByMood(cx, cy - 6f, isSleeping, mood, outlineColor)

    // Beak
    val beak = Path().apply {
        moveTo(cx - 8f, cy + 2f)
        lineTo(cx + 8f, cy + 2f)
        lineTo(cx, cy + 12f)
        close()
    }
    drawPath(beak, beakColor)
    drawPath(beak, outlineColor, style = Stroke(width = 2.5f))
}

private fun DrawScope.drawEyesByMood(cx: Float, cy: Float, isSleeping: Boolean, mood: PetMood, outlineColor: Color) {
    if (isSleeping) {
        val eyeL = Path().apply { moveTo(cx - 18f, cy - 2f); quadraticTo(cx - 12f, cy - 7f, cx - 6f, cy - 2f) }
        val eyeR = Path().apply { moveTo(cx + 6f, cy - 2f); quadraticTo(cx + 12f, cy - 7f, cx + 18f, cy - 2f) }
        drawPath(eyeL, outlineColor, style = Stroke(width = 3.5f))
        drawPath(eyeR, outlineColor, style = Stroke(width = 3.5f))
        return
    }

    when (mood) {
        PetMood.ECSTATIC -> {
            // Star eyes
            drawStarCrown(cx - 13f, cy - 2f, Color(0xFFF59E0B))
            drawStarCrown(cx + 13f, cy - 2f, Color(0xFFF59E0B))
        }
        PetMood.TIRED -> {
            // Half closed sleepy eyes
            drawLine(outlineColor, Offset(cx - 18f, cy - 2f), Offset(cx - 8f, cy - 2f), strokeWidth = 3.5f)
            drawLine(outlineColor, Offset(cx + 8f, cy - 2f), Offset(cx + 18f, cy - 2f), strokeWidth = 3.5f)
        }
        PetMood.SAD -> {
            // Tearful drooping eyes
            drawOval(outlineColor, topLeft = Offset(cx - 18f, cy - 4f), size = Size(9f, 11f))
            drawOval(outlineColor, topLeft = Offset(cx + 9f, cy - 4f), size = Size(9f, 11f))
            // Tear drop
            drawCircle(Color(0xFF38BDF8), radius = 2.5f, center = Offset(cx - 19f, cy + 9f))
        }
        PetMood.FOCUS_MODE -> {
            // Fierce focused sharp eyes
            drawLine(outlineColor, Offset(cx - 19f, cy - 6f), Offset(cx - 8f, cy - 2f), strokeWidth = 3f)
            drawLine(outlineColor, Offset(cx + 19f, cy - 6f), Offset(cx + 8f, cy - 2f), strokeWidth = 3f)
            drawCircle(outlineColor, radius = 3.5f, center = Offset(cx - 12f, cy - 2f))
            drawCircle(outlineColor, radius = 3.5f, center = Offset(cx + 12f, cy - 2f))
        }
        else -> {
            // Big shiny anime eyes
            drawOval(outlineColor, topLeft = Offset(cx - 18f, cy - 8f), size = Size(9f, 13f))
            drawOval(outlineColor, topLeft = Offset(cx + 9f, cy - 8f), size = Size(9f, 13f))
            drawCircle(Color.White, radius = 2.5f, center = Offset(cx - 15f, cy - 5f))
            drawCircle(Color.White, radius = 2.5f, center = Offset(cx + 12f, cy - 5f))
        }
    }
}

private fun DrawScope.drawMouthByMood(
    cx: Float,
    cy: Float,
    animState: PetAnimationState,
    mouthRatio: Float,
    mood: PetMood,
    outlineColor: Color
) {
    if (animState == PetAnimationState.TALKING || animState == PetAnimationState.EATING) {
        drawOval(
            Color(0xFFE11D48),
            topLeft = Offset(cx - 5f, cy + 7f),
            size = Size(10f, 6f + 6f * mouthRatio)
        )
        return
    }

    when (mood) {
        PetMood.HUNGRY -> {
            // Open whining mouth
            drawOval(Color(0xFFE11D48), topLeft = Offset(cx - 4f, cy + 8f), size = Size(8f, 6f))
        }
        PetMood.SAD -> {
            // Frown :(
            val frown = Path().apply {
                moveTo(cx - 6f, cy + 11f)
                quadraticTo(cx, cy + 6f, cx + 6f, cy + 11f)
            }
            drawPath(frown, outlineColor, style = Stroke(width = 2.5f))
        }
        else -> {
            // Happy :3 smile
            val mouth = Path().apply {
                moveTo(cx - 7f, cy + 8f)
                quadraticTo(cx - 3f, cy + 12f, cx, cy + 8f)
                quadraticTo(cx + 3f, cy + 12f, cx + 7f, cy + 8f)
            }
            drawPath(mouth, outlineColor, style = Stroke(width = 2.5f))
        }
    }
}

private fun DrawScope.drawEquippedHat(
    hatId: String,
    cx: Float,
    cy: Float,
    species: PetSpecies,
    stage: EvolutionStage
) {
    val topY = cy - 42f
    when (hatId) {
        "hat_wizard" -> {
            val hatPath = Path().apply {
                moveTo(cx - 28f, topY + 4f)
                lineTo(cx + 28f, topY + 4f)
                lineTo(cx + 8f, topY - 45f)
                lineTo(cx - 15f, topY - 40f)
                close()
            }
            drawPath(hatPath, Color(0xFF6D28D9))
            drawPath(hatPath, Color(0xFF3B0764), style = Stroke(width = 3f))
            // Gold buckle band
            drawRect(Color(0xFFFBBF24), Offset(cx - 20f, topY - 4f), Size(40f, 6f))
        }
        "hat_crown" -> {
            val crownPath = Path().apply {
                moveTo(cx - 22f, topY + 4f)
                lineTo(cx - 24f, topY - 20f)
                lineTo(cx - 10f, topY - 10f)
                lineTo(cx, topY - 25f)
                lineTo(cx + 10f, topY - 10f)
                lineTo(cx + 24f, topY - 20f)
                lineTo(cx + 22f, topY + 4f)
                close()
            }
            drawPath(crownPath, Color(0xFFF59E0B))
            drawPath(crownPath, Color(0xFF78350F), style = Stroke(width = 2.5f))
            drawCircle(Color(0xFFDC2626), radius = 2.5f, center = Offset(cx, topY - 16f))
        }
        "hat_chef" -> {
            drawRoundRect(Color.White, Offset(cx - 18f, topY - 6f), Size(36f, 10f), CornerRadius(2f, 2f))
            drawCircle(Color.White, radius = 16f, center = Offset(cx, topY - 16f))
            drawCircle(Color.White, radius = 12f, center = Offset(cx - 14f, topY - 12f))
            drawCircle(Color.White, radius = 12f, center = Offset(cx + 14f, topY - 12f))
            drawRoundRect(Color(0xFF475569), Offset(cx - 18f, topY - 6f), Size(36f, 10f), CornerRadius(2f, 2f), style = Stroke(width = 2f))
        }
        "hat_flower" -> {
            drawCircle(Color(0xFFFB7185), radius = 7f, center = Offset(cx + 20f, topY - 4f))
            drawCircle(Color(0xFFFDE047), radius = 3f, center = Offset(cx + 20f, topY - 4f))
        }
        "hat_cowboy" -> {
            drawOval(Color(0xFF854D0E), topLeft = Offset(cx - 36f, topY - 4f), size = Size(72f, 12f))
            drawRoundRect(Color(0xFF713F12), topLeft = Offset(cx - 18f, topY - 22f), size = Size(36f, 20f), cornerRadius = CornerRadius(6f, 6f))
        }
        "hat_headset" -> {
            // Headband arc
            drawArc(
                color = Color(0xFF0F172A),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(cx - 40f, topY - 8f),
                size = Size(80f, 70f),
                style = Stroke(width = 5f)
            )
            // Left & Right earcups
            drawRoundRect(Color(0xFF38BDF8), Offset(cx - 48f, cy - 20f), Size(12f, 24f), CornerRadius(4f, 4f))
            drawRoundRect(Color(0xFF38BDF8), Offset(cx + 36f, cy - 20f), Size(12f, 24f), CornerRadius(4f, 4f))
        }
        "hat_halo" -> {
            drawOval(
                Color(0xFFFBBF24),
                topLeft = Offset(cx - 24f, topY - 26f),
                size = Size(48f, 12f),
                style = Stroke(width = 3.5f)
            )
        }
        "hat_bandana" -> {
            drawRect(Color(0xFFDC2626), Offset(cx - 32f, topY + 4f), Size(64f, 8f))
            drawLine(Color(0xFFDC2626), Offset(cx + 30f, topY + 8f), Offset(cx + 45f, topY + 24f), strokeWidth = 5f)
        }
    }
}

private fun DrawScope.drawEquippedAccessory(accId: String, cx: Float, cy: Float, stage: EvolutionStage) {
    val handX = cx + 36f
    val handY = cy + 18f

    when (accId) {
        "acc_glasses" -> {
            drawCircle(Color(0xFF1E293B), radius = 8f, center = Offset(cx - 14f, cy - 4f), style = Stroke(width = 2.5f))
            drawCircle(Color(0xFF1E293B), radius = 8f, center = Offset(cx + 14f, cy - 4f), style = Stroke(width = 2.5f))
            drawLine(Color(0xFF1E293B), Offset(cx - 6f, cy - 4f), Offset(cx + 6f, cy - 4f), strokeWidth = 2.5f)
        }
        "acc_monocle" -> {
            drawCircle(Color(0xFFD97706), radius = 9f, center = Offset(cx + 13f, cy - 4f), style = Stroke(width = 2.5f))
            drawLine(Color(0xFFD97706), Offset(cx + 20f, cy + 4f), Offset(cx + 26f, cy + 20f), strokeWidth = 1.5f)
        }
        "acc_trophy" -> {
            drawOval(Color(0xFFFBBF24), topLeft = Offset(handX - 8f, handY - 14f), size = Size(16f, 18f))
            drawRect(Color(0xFF78350F), Offset(handX - 6f, handY + 4f), Size(12f, 6f))
        }
        "acc_coffee" -> {
            drawRoundRect(Color(0xFFF8FAFC), Offset(handX - 6f, handY - 8f), Size(14f, 16f), CornerRadius(3f, 3f))
            drawRect(Color(0xFF78350F), Offset(handX - 5f, handY - 7f), Size(12f, 4f))
            // Steam
            drawLine(Color(0x99FFFFFF), Offset(handX, handY - 10f), Offset(handX - 2f, handY - 18f), strokeWidth = 2f)
        }
        "acc_sword" -> {
            drawLine(Color(0xFF94A3B8), Offset(handX - 4f, handY + 8f), Offset(handX + 16f, handY - 24f), strokeWidth = 4f)
            drawLine(Color(0xFFD97706), Offset(handX - 10f, handY + 2f), Offset(handX + 2f, handY + 12f), strokeWidth = 3f)
        }
        "acc_wand" -> {
            drawLine(Color(0xFF9333EA), Offset(handX - 2f, handY + 8f), Offset(handX + 14f, handY - 20f), strokeWidth = 3f)
            drawStarCrown(handX + 14f, handY - 20f, Color(0xFFFBBF24))
        }
        "acc_boba" -> {
            drawRoundRect(Color(0xFFEDE9FE), Offset(handX - 6f, handY - 8f), Size(14f, 18f), CornerRadius(3f, 3f))
            drawLine(Color(0xFF8B5CF6), Offset(handX + 1f, handY - 14f), Offset(handX + 1f, handY + 6f), strokeWidth = 2.5f)
            drawCircle(Color(0xFF1E1B4B), radius = 1.5f, center = Offset(handX - 2f, handY + 5f))
            drawCircle(Color(0xFF1E1B4B), radius = 1.5f, center = Offset(handX + 3f, handY + 6f))
        }
    }
}

private fun DrawScope.drawStarCrown(cx: Float, cy: Float, color: Color) {
    val starPath = Path().apply {
        moveTo(cx, cy - 9f)
        lineTo(cx + 3f, cy - 3f)
        lineTo(cx + 9f, cy - 3f)
        lineTo(cx + 4f, cy + 2f)
        lineTo(cx + 6f, cy + 8f)
        lineTo(cx, cy + 4f)
        lineTo(cx - 6f, cy + 8f)
        lineTo(cx - 4f, cy + 2f)
        lineTo(cx - 9f, cy - 3f)
        lineTo(cx - 3f, cy - 3f)
        close()
    }
    drawPath(starPath, color)
}

private fun DrawScope.drawZzzBubbles(cx: Float, cy: Float) {
    drawCircle(Color(0x99A5B4FC), radius = 4f, center = Offset(cx - 6f, cy + 12f))
    drawCircle(Color(0xCCA5B4FC), radius = 7f, center = Offset(cx + 4f, cy))
    drawCircle(Color(0xFF818CF8), radius = 10f, center = Offset(cx + 16f, cy - 16f))
}

private fun DrawScope.drawUrgentAlertBadge(cx: Float, cy: Float) {
    drawCircle(Color(0xFFEF4444), radius = 16f, center = Offset(cx, cy))
    drawCircle(Color.White, radius = 16f, center = Offset(cx, cy), style = Stroke(width = 2.5f))
    drawLine(Color.White, Offset(cx, cy - 8f), Offset(cx, cy + 2f), strokeWidth = 3.5f)
    drawCircle(Color.White, radius = 2f, center = Offset(cx, cy + 6f))
}

private fun DrawScope.drawHappySparkles(cx: Float, cy: Float) {
    val heartL = Path().apply {
        moveTo(cx - 35f, cy)
        cubicTo(cx - 42f, cy - 8f, cx - 46f, cy + 2f, cx - 35f, cy + 10f)
        cubicTo(cx - 24f, cy + 2f, cx - 28f, cy - 8f, cx - 35f, cy)
        close()
    }
    val heartR = Path().apply {
        moveTo(cx + 35f, cy - 6f)
        cubicTo(cx + 28f, cy - 14f, cx + 24f, cy - 4f, cx + 35f, cy + 4f)
        cubicTo(cx + 46f, cy - 4f, cx + 42f, cy - 14f, cx + 35f, cy - 6f)
        close()
    }
    drawPath(heartL, Color(0xFFF43F5E))
    drawPath(heartR, Color(0xFFFB7185))
}

private fun DrawScope.drawHungryTummyBubble(cx: Float, cy: Float) {
    drawCircle(Color(0xFFF59E0B), radius = 12f, center = Offset(cx, cy))
    drawCircle(Color.White, radius = 12f, center = Offset(cx, cy), style = Stroke(width = 2f))
    // Tiny fork/spoon
    drawLine(Color.White, Offset(cx - 4f, cy - 6f), Offset(cx - 4f, cy + 6f), strokeWidth = 2f)
    drawLine(Color.White, Offset(cx + 4f, cy - 6f), Offset(cx + 4f, cy + 6f), strokeWidth = 2f)
}

private fun DrawScope.drawFocusAura(cx: Float, cy: Float) {
    // Focus target reticle
    drawCircle(Color(0xFF0D9488), radius = 14f, center = Offset(cx, cy), style = Stroke(width = 2f))
    drawCircle(Color(0xFF14B8A6), radius = 5f, center = Offset(cx, cy))
}
