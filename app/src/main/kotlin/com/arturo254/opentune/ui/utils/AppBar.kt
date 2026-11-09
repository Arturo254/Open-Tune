/*
 * OpenTune Project Original (2026)
 * Arturo254 (github.com/Arturo254)
 * Licensed Under GPL-3.0 | see git history for contributors
 */



package com.arturo254.opentune.ui.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appBarScrollBehavior(
    state: TopAppBarState = rememberTopAppBarState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay(),
): TopAppBarScrollBehavior =
    AppBarScrollBehavior(
        state = state,
        snapAnimationSpec = snapAnimationSpec,
        flingAnimationSpec = flingAnimationSpec,
        canScroll = canScroll,
    )

@ExperimentalMaterial3Api
class AppBarScrollBehavior(
    override val state: TopAppBarState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean = { true },
) : TopAppBarScrollBehavior {
    override val isPinned: Boolean = true
    override var nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (!canScroll()) return Offset.Zero
                state.contentOffset += consumed.y
                if (state.heightOffset == 0f || state.heightOffset == state.heightOffsetLimit) {
                    if (consumed.y == 0f && available.y > 0f) {
                        // Reset the total content offset to zero when scrolling all the way down.
                        // This will eliminate some float precision inaccuracies.
                        state.contentOffset = 0f
                    }
                }
                state.heightOffset = (state.heightOffset + consumed.y).coerceIn(state.heightOffsetLimit, 0f)
                return Offset.Zero
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
suspend fun TopAppBarState.resetHeightOffset() {
    if (heightOffset != 0f) {
        animate(
            initialValue = heightOffset,
            targetValue = 0f,
        ) { value, _ ->
            heightOffset = value
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Colores y scrim de la TopAppBar
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Color de contenido estándar de la barra superior cuando el fondo es plano
 * (`surface`). Garantiza contraste legible tanto en tema claro como oscuro.
 */
@Composable
fun appBarContentColor(): Color = MaterialTheme.colorScheme.onSurface

/**
 * Colores de una `TopAppBar` totalmente transparente: el contenedor no pinta
 * nada para que el contenido (la carátula del hero) se extienda por debajo de
 * la barra, mientras que el título y los iconos usan [contentColor].
 *
 * @param contentColor tinte aplicado al título, la navegación y las acciones.
 */
@Composable
fun transparentTopAppBarColors(contentColor: Color): TopAppBarColors =
    TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
        titleContentColor = contentColor,
        navigationIconContentColor = contentColor,
        actionIconContentColor = contentColor,
    )

/**
 * Scrim de la barra superior: degradado oscuro que se desvanece hacia abajo
 * para que los iconos claros sigan siendo legibles cuando la carátula pasa por
 * debajo de una barra transparente. Se dibuja por debajo del contenido de la
 * barra, de modo que nunca tapa el título ni los iconos.
 */
fun Modifier.appBarScrim(): Modifier =
    drawWithCache {
        val brush = Brush.verticalGradient(
            0f to Color.Black.copy(alpha = 0.45f),
            0.6f to Color.Black.copy(alpha = 0.18f),
            1f to Color.Transparent,
        )
        onDrawWithContent {
            drawRect(brush)
            drawContent()
        }
    }
