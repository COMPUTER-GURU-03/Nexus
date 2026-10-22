package com.example.nexus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.FintechBorder
import com.example.ui.theme.FintechSurface1

/**
 * Reusable Card component adhering to the premium fintech aesthetic:
 * - Layered, slightly raised dark cards (#161616 to #1E1E1E)
 * - Large rounded corners (20-24px / 20-24dp)
 * - Thin subtle borders, no harsh shadows
 */
@Composable
fun FintechCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = FintechSurface1,
    borderColor: Color = FintechBorder,
    borderWidth: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(22.dp),
    contentPadding: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = Color.White.copy(alpha = 0.08f)),
            onClick = onClick
        )
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .clip(shape)
            .then(clickableModifier),
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Box(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}
