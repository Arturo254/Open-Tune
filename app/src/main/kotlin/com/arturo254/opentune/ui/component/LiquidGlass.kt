package com.arturo254.opentune.ui.component

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.arturo254.opentune.ui.theme.FrostedGlassTokens
import com.arturo254.opentune.ui.theme.LocalFrostedGlass
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import kotlin.math.sign
import com.kyant.backdrop.backdrops.layerBackdrop as nativeBackdrop

typealias PlatformBackdrop = LayerBackdrop

val LocalBackdrop = staticCompositionLocalOf<PlatformBackdrop?> { null }

/** Radio de desenfoque por defecto del vidrio (dp). */
private val DefaultGlassBlurRadius = 8.dp

/**
 * A partir de este tamaño (en dp) se activa la refracción tipo lente. En superficies
 * pequeñas (chips, iconos) la refracción es imperceptible y sólo cuesta GPU, por lo que
 * se omite.
 */
private val LensMinDimension = 120.dp

@Composable
fun rememberBackdrop(): PlatformBackdrop = rememberLayerBackdrop {
    drawRect(Color.Black)
    drawContent()
}

fun Modifier.layerBackdrop(backdrop: PlatformBackdrop): Modifier = this.nativeBackdrop(backdrop)

/**
 * Dibuja una superficie de vidrio líquido sobre [backdrop].
 *
 * Optimizaciones frente a la versión anterior:
 *  - El tinte de la superficie ([scrimColor]) se recibe como parámetro para poder adaptarse
 *    al tema en lugar de usar negro fijo.
 *  - La refracción (efecto lente) sólo se calcula en superficies grandes.
 *  - El velo de la superficie y su intensidad se derivan de [surfaceAlpha] mediante un
 *    degradado, lo que mejora la legibilidad del texto sin perder translucidez.
 *
 * @param blurRadius radio base del desenfoque. Aumenta con [luminanceAnimation] para dar
 *   sensación de vidrio más profundo cuando la superficie está resaltada.
 * @param scrimColor color del velo que da contraste al contenido (normalmente el color de
 *   superficie del tema).
 */
fun Modifier.drawBackdropCustomShape(
    backdrop: PlatformBackdrop,
    layer: GraphicsLayer? = null,
    luminanceAnimation: Float = 0.3f,
    shape: Shape,
    disableBlur: Boolean = false,
    fallbackColor: Color = Color(0xFF121212),
    surfaceAlpha: Float = 0.82f,
    blurRadius: Dp = DefaultGlassBlurRadius,
    scrimColor: Color = Color.Black,
    borderStroke: BorderStroke? = BorderStroke(
        1.dp,
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.22f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    ),
): Modifier {
    // Curva de luminancia: [-1, 1] con énfasis cuadrático en los extremos.
    val l = (luminanceAnimation * 2f - 1f).let { sign(it) * it * it }
    val lensEnabled = shape is CornerBasedShape
    val scrimAlpha = (surfaceAlpha * 0.55f).coerceIn(0f, 1f)

    val base = this.drawBackdrop(
        backdrop = backdrop,
        effects = {
            val baseBlurPx = blurRadius.toPx()
            vibrancy()
            colorControls(
                brightness =
                    if (l > 0f) {
                        lerp(0.1f, 0.5f, l)
                    } else {
                        lerp(0.1f, -0.2f, -l)
                    },
                contrast =
                    if (l > 0f) {
                        lerp(1f, 0f, l)
                    } else {
                        1f
                    },
                saturation = 1.5f,
            )
            blur(
                if (l > 0f) {
                    lerp(baseBlurPx, baseBlurPx * 2f, l)
                } else {
                    lerp(baseBlurPx, baseBlurPx * 0.25f, -l)
                },
            )
            if (lensEnabled && size.minDimension > LensMinDimension.toPx()) {
                runCatching {
                    lens(24f.dp.toPx(), size.minDimension / 2f, true)
                }
            }
        },
        onDrawBackdrop = { drawBackdrop ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !disableBlur) {
                drawBackdrop()
            }
        },
        shape = { shape },
        onDrawSurface = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !disableBlur) {
                // Vidrio: brillo especular arriba y velo progresivo hacia abajo.
                drawRect(
                    Brush.verticalGradient(
                        0f to Color.White.copy(alpha = scrimAlpha * 0.14f),
                        0.4f to scrimColor.copy(alpha = scrimAlpha * 0.55f),
                        1f to scrimColor.copy(alpha = scrimAlpha),
                    )
                )
            } else {
                // Respaldo para Android 11 o inferior (o blur desactivado): velo opaco.
                drawRect(fallbackColor.copy(alpha = surfaceAlpha))
            }
        }
    )
    return if (borderStroke != null) {
        base.border(borderStroke, shape)
    } else {
        base
    }
}

@Composable
fun Modifier.frostedGlassSurface(
    shape: Shape = RectangleShape,
    luminanceAnimation: Float = 0.3f,
    customFallbackColor: Color? = null,
    blurRadius: Dp = DefaultGlassBlurRadius,
): Modifier {
    val frostedGlass = LocalFrostedGlass.current
    if (!frostedGlass.isEnabled) return this

    val backdrop = LocalBackdrop.current
    val fallback = customFallbackColor ?: frostedGlass.fallbackSurfaceColor
    // El velo se adapta al tema: oscuro sobre tema oscuro, claro sobre tema claro.
    val scheme = MaterialTheme.colorScheme
    val scrimColor = if (scheme.surface.luminance() < 0.5f) Color.Black else Color.White

    return if (frostedGlass.isBlurSupported && backdrop != null) {
        this.drawBackdropCustomShape(
            backdrop = backdrop,
            luminanceAnimation = luminanceAnimation,
            shape = shape,
            disableBlur = !frostedGlass.isBlurSupported,
            fallbackColor = fallback,
            surfaceAlpha = frostedGlass.surfaceAlpha,
            blurRadius = blurRadius,
            scrimColor = scrimColor,
            borderStroke = frostedGlass.borderStroke,
        )
    } else {
        this
            .clip(shape)
            .background(fallback.copy(alpha = frostedGlass.surfaceAlpha))
            .border(frostedGlass.borderStroke, shape)
    }
}
