/*
 * OpenTune Project Original (2026)
 * Arturo254 (github.com/Arturo254)
 * Licensed Under GPL-3.0 | see git history for contributors
 */

package com.arturo254.opentune.ui.screens

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arturo254.opentune.R
import com.arturo254.opentune.ui.screens.settings.DarkMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

/**
 * Presentación de bienvenida de OpenTune.
 *
 * Se muestra una única vez (primera apertura de la app) y sigue la estructura de la
 * configuración inicial de un iPhone: una secuencia de saludos que se suceden, un recorrido
 * breve por las funciones y un último paso de personalización antes de entrar.
 *
 * La estética se mantiene dentro de Material 3 Expressive: formas redondeadas, tipografía
 * grande, color tonal dinámico, movimiento con curvas expresivas y micro-interacciones de
 * escala en lugar de ripples.
 */

/** Valor de acento que representa "usar el color dinámico del sistema". */
const val OnboardingDefaultAccent = "default"

private enum class OnboardingPhase { Greeting, Tour }

private enum class OnboardingHero { Discover, Lyrics, Canvas, Audio, Recognition, Offline, Stats, Setup }

private data class OnboardingPage(
    @StringRes val title: Int,
    @StringRes val body: Int,
    val hero: OnboardingHero,
)

/** Acentos disponibles en el paso de personalización (valores ARGB aceptados por el tema). */
private val OnboardingAccents = listOf(
    "#ED5564",
    "#4C8DF6",
    "#2FBF71",
    "#8B5CF6",
    "#F5A524",
)

private val Greetings = listOf(
    "Hola",
    "Hello",
    "こんにちは",
    "Bonjour",
    "Ciao",
    "Olá",
    "Hallo",
    "안녕하세요",
    "नमस्ते",
    "Привет",
    "你好",
    "Merhaba",
)

@Composable
fun OnboardingPresentation(
    onFinish: () -> Unit,
    appearance: DarkMode,
    onAppearanceChange: (DarkMode) -> Unit,
    selectedAccent: String,
    onAccentSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var phase by rememberSaveable { mutableStateOf(OnboardingPhase.Greeting) }
    var finishing by remember { mutableStateOf(false) }

    val contentAlpha by animateFloatAsState(
        targetValue = if (finishing) 0f else 1f,
        animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing),
        label = "onboarding_exit_alpha",
    )
    val contentScale by animateFloatAsState(
        targetValue = if (finishing) 1.05f else 1f,
        animationSpec = tween(durationMillis = 440, easing = FastOutSlowInEasing),
        label = "onboarding_exit_scale",
    )

    LaunchedEffect(finishing) {
        if (finishing) {
            delay(320)
            onFinish()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // El fondo se mantiene opaco: solo el contenido se desvanece al terminar, así el
        // traspaso hacia la app no deja ver el windowBackground.
        AuroraBackdrop(Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = contentAlpha
                    scaleX = contentScale
                    scaleY = contentScale
                }
        ) {
            AnimatedContent(
                targetState = phase,
                transitionSpec = {
                    (fadeIn(tween(420)) + scaleIn(initialScale = 1.03f, animationSpec = tween(420)))
                        .togetherWith(fadeOut(tween(240)))
                },
                label = "onboarding_phase",
            ) { current ->
                when (current) {
                    OnboardingPhase.Greeting -> GreetingStage(
                        onDone = { phase = OnboardingPhase.Tour },
                    )

                    OnboardingPhase.Tour -> TourStage(
                        appearance = appearance,
                        onAppearanceChange = onAppearanceChange,
                        selectedAccent = selectedAccent,
                        onAccentSelected = onAccentSelected,
                        onFinish = { finishing = true },
                        onSkip = { finishing = true },
                    )
                }
            }
        }
    }
}

/* --------------------------------------------------------------------------------------- */
/* Fondo                                                                                   */
/* --------------------------------------------------------------------------------------- */

@Composable
private fun AuroraBackdrop(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "onboarding_aurora")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15_000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "onboarding_drift",
    )

    Box(modifier) {
        AuroraBlob(
            color = scheme.primary.copy(alpha = 0.40f),
            size = 460.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-150).dp, y = (-160).dp)
                .graphicsLayer {
                    translationX = drift * 110.dp.toPx()
                    translationY = drift * 80.dp.toPx()
                },
            drift = drift,
        )

        AuroraBlob(
            color = scheme.tertiary.copy(alpha = 0.34f),
            size = 520.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 170.dp, y = 150.dp)
                .graphicsLayer {
                    translationX = -drift * 90.dp.toPx()
                    translationY = -drift * 120.dp.toPx()
                },
            drift = drift,
        )

        AuroraBlob(
            color = scheme.secondary.copy(alpha = 0.22f),
            size = 380.dp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = -60.dp, y = (-40).dp)
                .graphicsLayer {
                    translationY = (drift - 0.5f) * 220.dp.toPx()
                },
            drift = drift,
        )

        // Base tonal para que el texto inferior siempre tenga contraste suficiente.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.45f to Color.Transparent,
                        1f to scheme.surface.copy(alpha = 0.72f),
                    )
                )
        )
    }
}

@Composable
private fun AuroraBlob(
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    drift: Float,
) {
    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                val pulse = 1f + drift * 0.12f
                scaleX = pulse
                scaleY = pulse
            }
            .background(
                Brush.radialGradient(
                    colors = listOf(color, Color.Transparent),
                )
            )
    )
}

/* --------------------------------------------------------------------------------------- */
/* Fase 1 — Saludos                                                                        */
/* --------------------------------------------------------------------------------------- */

@Composable
private fun GreetingStage(onDone: () -> Unit) {
    var index by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (index < Greetings.lastIndex) {
            delay(190)
            index++
        }
        delay(560)
        done = true
    }
    LaunchedEffect(done) {
        if (done) onDone()
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            EqualizerMark()

            Spacer(Modifier.height(44.dp))

            AnimatedContent(
                targetState = index,
                transitionSpec = {
                    (fadeIn(tween(200)) + slideInVertically { it / 8 } + scaleIn(initialScale = 0.96f))
                        .togetherWith(fadeOut(tween(150)) + scaleOut(targetScale = 1.04f))
                },
                label = "onboarding_greeting",
            ) { current ->
                Text(
                    text = Greetings[current],
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.5).sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.onboarding_tagline),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        OnboardingSkipButton(
            visible = !done,
            onClick = onDone,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.safeDrawing),
        )
    }
}

@Composable
private fun EqualizerMark(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "onboarding_eq")
    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "onboarding_eq_wave",
    )

    Canvas(modifier = modifier.size(width = 104.dp, height = 40.dp)) {
        val bars = 5
        val step = size.width / (bars * 2f - 1f)
        val barWidth = step
        val brush = Brush.horizontalGradient(listOf(scheme.primary, scheme.tertiary))

        for (i in 0 until bars) {
            val energy = abs(sin(wave * 2f * PI.toFloat() + i * 0.82f))
            val height = (size.height * 0.26f) + (size.height * 0.74f) * energy
            drawRoundRect(
                brush = brush,
                topLeft = Offset(i * 2f * step, (size.height - height) / 2f),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
            )
        }
    }
}

/* --------------------------------------------------------------------------------------- */
/* Fase 2 — Recorrido + personalización                                                    */
/* --------------------------------------------------------------------------------------- */

@Composable
private fun TourStage(
    appearance: DarkMode,
    onAppearanceChange: (DarkMode) -> Unit,
    selectedAccent: String,
    onAccentSelected: (String) -> Unit,
    onFinish: () -> Unit,
    onSkip: () -> Unit,
) {
    val pages = listOf(
        OnboardingPage(
            title = R.string.onboarding_page_discover_title,
            body = R.string.onboarding_page_discover_body,
            hero = OnboardingHero.Discover,
        ),
        OnboardingPage(
            title = R.string.onboarding_page_lyrics_title,
            body = R.string.onboarding_page_lyrics_body,
            hero = OnboardingHero.Lyrics,
        ),
        OnboardingPage(
            title = R.string.onboarding_page_canvas_title,
            body = R.string.onboarding_page_canvas_body,
            hero = OnboardingHero.Canvas,
        ),
        OnboardingPage(
            title = R.string.onboarding_page_audio_title,
            body = R.string.onboarding_page_audio_body,
            hero = OnboardingHero.Audio,
        ),
        OnboardingPage(
            title = R.string.onboarding_page_recognition_title,
            body = R.string.onboarding_page_recognition_body,
            hero = OnboardingHero.Recognition,
        ),
        OnboardingPage(
            title = R.string.onboarding_page_offline_title,
            body = R.string.onboarding_page_offline_body,
            hero = OnboardingHero.Offline,
        ),
        OnboardingPage(
            title = R.string.onboarding_page_stats_title,
            body = R.string.onboarding_page_stats_body,
            hero = OnboardingHero.Stats,
        ),
        OnboardingPage(
            title = R.string.onboarding_page_setup_title,
            body = R.string.onboarding_page_setup_body,
            hero = OnboardingHero.Setup,
        ),
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // Espacio reservado para que el salto de "Saltar" no desplace el contenido.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            OnboardingSkipButton(visible = !isLast, onClick = onSkip)
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { pageIndex ->
            val offset =
                (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
            val page = pages[pageIndex]

            if (page.hero == OnboardingHero.Setup) {
                SetupPageContent(
                    title = stringResource(page.title),
                    body = stringResource(page.body),
                    appearance = appearance,
                    onAppearanceChange = onAppearanceChange,
                    selectedAccent = selectedAccent,
                    onAccentSelected = onAccentSelected,
                    offset = offset,
                )
            } else {
                TourPageContent(
                    title = stringResource(page.title),
                    body = stringResource(page.body),
                    hero = page.hero,
                    offset = offset,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(top = 12.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PageIndicator(count = pages.size, current = pagerState.currentPage)

            Spacer(Modifier.height(22.dp))

            OnboardingPrimaryButton(
                label = if (isLast) {
                    stringResource(R.string.onboarding_start)
                } else {
                    stringResource(R.string.onboarding_continue)
                },
                onClick = {
                    if (isLast) {
                        onFinish()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
            )
        }
    }
}

@Composable
private fun TourPageContent(
    title: String,
    body: String,
    hero: OnboardingHero,
    offset: Float,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = -offset * 56.dp.toPx()
                alpha = (1f - abs(offset) * 0.7f).coerceIn(0f, 1f)
            }
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(Modifier.size(232.dp), contentAlignment = Alignment.Center) {
            when (hero) {
                OnboardingHero.Discover -> SonarHero()
                OnboardingHero.Lyrics -> LyricsHero()
                OnboardingHero.Canvas -> CanvasHero()
                OnboardingHero.Audio -> CrossfadeHero()
                OnboardingHero.Recognition -> RecognitionHero()
                OnboardingHero.Offline -> OfflineHero()
                OnboardingHero.Stats -> StatsHero()
                OnboardingHero.Setup -> Unit
            }
        }

        Spacer(Modifier.height(40.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SetupPageContent(
    title: String,
    body: String,
    appearance: DarkMode,
    onAppearanceChange: (DarkMode) -> Unit,
    selectedAccent: String,
    onAccentSelected: (String) -> Unit,
    offset: Float,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = -offset * 56.dp.toPx()
                alpha = (1f - abs(offset) * 0.7f).coerceIn(0f, 1f)
            }
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
            .padding(top = 12.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(32.dp))

        Column(Modifier.fillMaxWidth()) {
            SectionLabel(stringResource(R.string.onboarding_appearance))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OnboardingChoicePill(
                    label = stringResource(R.string.onboarding_appearance_system),
                    selected = appearance == DarkMode.AUTO,
                    onClick = { onAppearanceChange(DarkMode.AUTO) },
                    modifier = Modifier.weight(1f),
                )
                OnboardingChoicePill(
                    label = stringResource(R.string.onboarding_appearance_light),
                    selected = appearance == DarkMode.OFF,
                    onClick = { onAppearanceChange(DarkMode.OFF) },
                    modifier = Modifier.weight(1f),
                )
                OnboardingChoicePill(
                    label = stringResource(R.string.onboarding_appearance_dark),
                    selected = appearance == DarkMode.ON,
                    onClick = { onAppearanceChange(DarkMode.ON) },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(26.dp))

            SectionLabel(stringResource(R.string.onboarding_accent_color))
            Spacer(Modifier.height(14.dp))

            val autoBrush = Brush.sweepGradient(
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.tertiary,
                    MaterialTheme.colorScheme.secondary,
                    MaterialTheme.colorScheme.primary,
                )
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AccentSwatch(
                    brush = autoBrush,
                    selected = selectedAccent == OnboardingDefaultAccent,
                    onClick = { onAccentSelected(OnboardingDefaultAccent) },
                )

                OnboardingAccents.forEach { value ->
                    val color = Color(android.graphics.Color.parseColor(value))
                    AccentSwatch(
                        brush = Brush.linearGradient(listOf(color, color)),
                        selected = selectedAccent.equals(value, ignoreCase = true),
                        onClick = { onAccentSelected(value) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.2.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun OnboardingChoicePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(stiffness = 550f, dampingRatio = 0.75f),
        label = "pill_scale",
    )
    val container by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "pill_container",
    )
    val content by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "pill_content",
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(container)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = content,
            maxLines = 1,
        )
    }
}

@Composable
private fun AccentSwatch(
    brush: Brush,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = spring(stiffness = 550f, dampingRatio = 0.7f),
        label = "swatch_scale",
    )
    val ringColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onSurface
        } else {
            Color.Transparent
        },
        label = "swatch_ring",
    )

    Box(
        modifier = Modifier
            .size(46.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(ringColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(brush),
        )
    }
}

/* --------------------------------------------------------------------------------------- */
/* Indicador de página y botón principal                                                   */
/* --------------------------------------------------------------------------------------- */

@Composable
private fun OnboardingSkipButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(150)),
        modifier = modifier,
    ) {
        TextButton(onClick = onClick) {
            Text(stringResource(R.string.onboarding_skip))
        }
    }
}

@Composable
private fun PageIndicator(count: Int, current: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            val selected = index == current
            val dotWidth by animateDpAsState(
                targetValue = if (selected) 26.dp else 8.dp,
                animationSpec = spring(stiffness = 500f, dampingRatio = 0.7f),
                label = "indicator_width_$index",
            )
            val dotColor by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                },
                label = "indicator_color_$index",
            )

            Box(
                modifier = Modifier
                    .size(width = dotWidth, height = 8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

@Composable
private fun OnboardingPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(stiffness = 550f, dampingRatio = 0.75f),
        label = "cta_scale",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
        )
    }
}

/* --------------------------------------------------------------------------------------- */
/* Ilustraciones                                                                           */
/* --------------------------------------------------------------------------------------- */

@Composable
private fun SonarHero(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "hero_sonar")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "hero_sonar_sweep",
    )
    val breathe by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "hero_sonar_breathe",
    )

    Canvas(
        modifier = modifier
            .size(210.dp)
            .graphicsLayer {
                scaleX = breathe
                scaleY = breathe
            }
    ) {
        val center = this.center
        val maxRadius = size.minDimension / 2f

        repeat(3) { ring ->
            val progress = (sweep + ring / 3f) % 1f
            drawCircle(
                color = scheme.primary.copy(alpha = (1f - progress) * 0.5f),
                radius = maxRadius * (0.34f + 0.66f * progress),
                center = center,
                style = Stroke(width = 2.dp.toPx()),
            )
        }

        val coreRadius = maxRadius * 0.30f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(scheme.tertiary, scheme.primary),
                center = center,
                radius = coreRadius * 1.7f,
            ),
            radius = coreRadius,
            center = center,
        )

        val bars = 4
        val barWidth = coreRadius * 0.20f
        val spacing = coreRadius * 0.46f
        val startX = center.x - (bars - 1) * spacing / 2f
        for (i in 0 until bars) {
            val energy = abs(sin(sweep * 2f * PI.toFloat() + i * 1.05f))
            val height = coreRadius * (0.5f + 0.85f * energy)
            drawRoundRect(
                color = scheme.onPrimary.copy(alpha = 0.94f),
                topLeft = Offset(startX + i * spacing - barWidth / 2f, center.y - height / 2f),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
            )
        }
    }
}

@Composable
private fun LyricsHero(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "hero_lyrics")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3_100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "hero_lyrics_progress",
    )

    Canvas(modifier = modifier.size(width = 224.dp, height = 160.dp)) {
        val rows = 3
        val rowHeight = 20.dp.toPx()
        val gap = 28.dp.toPx()
        val totalHeight = rows * rowHeight + (rows - 1) * gap
        val startY = (size.height - totalHeight) / 2f
        val fractions = listOf(0.66f, 1f, 0.5f)
        val radius = CornerRadius(rowHeight / 2f, rowHeight / 2f)

        for (row in 0 until rows) {
            val rowWidth = size.width * fractions[row]
            val left = (size.width - rowWidth) / 2f
            val top = startY + row * (rowHeight + gap)
            val isActive = row == 1

            drawRoundRect(
                color = scheme.onSurfaceVariant.copy(alpha = if (isActive) 0.20f else 0.12f),
                topLeft = Offset(left, top),
                size = Size(rowWidth, rowHeight),
                cornerRadius = radius,
            )

            if (isActive) {
                clipRect(
                    left = left,
                    top = top,
                    right = left + rowWidth * progress,
                    bottom = top + rowHeight,
                ) {
                    drawRoundRect(
                        color = scheme.primary,
                        topLeft = Offset(left, top),
                        size = Size(rowWidth, rowHeight),
                        cornerRadius = radius,
                    )
                }
            }
        }
    }
}

@Composable
private fun CrossfadeHero(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "hero_crossfade")
    val mix by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3_400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "hero_crossfade_mix",
    )

    Canvas(modifier = modifier.size(width = 248.dp, height = 150.dp)) {
        val middle = size.height / 2f
        val amplitude = size.height * 0.36f
        val steps = 96
        val outgoing = 1f - mix
        val incoming = mix

        drawLine(
            color = scheme.outlineVariant.copy(alpha = 0.5f),
            start = Offset(0f, middle),
            end = Offset(size.width, middle),
            strokeWidth = 1.dp.toPx(),
        )

        fun wave(sign: Int, level: Float, phase: Float): Path {
            val path = Path()
            for (step in 0..steps) {
                val fraction = step / steps.toFloat()
                val x = fraction * size.width
                val y = middle + sign * amplitude * level * sin(fraction * 3f * PI.toFloat() + phase)
                if (step == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            return path
        }

        drawPath(
            path = wave(sign = -1, level = outgoing, phase = 0f),
            color = scheme.primary.copy(alpha = 0.28f + 0.72f * outgoing),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
        )
        drawPath(
            path = wave(sign = 1, level = incoming, phase = 0.6f),
            color = scheme.tertiary.copy(alpha = 0.28f + 0.72f * incoming),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun CanvasHero(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "hero_canvas")
    val swing by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3_400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "hero_canvas_swing",
    )
    val sheen by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "hero_canvas_sheen",
    )

    Canvas(
        modifier = modifier
            .size(206.dp)
            .graphicsLayer { rotationZ = swing * 4f }
    ) {
        val center = this.center
        val side = size.minDimension * 0.60f
        val left = center.x - side / 2f
        val top = center.y - side / 2f

        drawCircle(
            color = scheme.secondary.copy(alpha = 0.28f),
            radius = side * 0.74f,
            center = center,
        )

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(scheme.primary, scheme.tertiary),
                start = Offset(left, top),
                end = Offset(left + side, top + side),
            ),
            topLeft = Offset(left, top),
            size = Size(side, side),
            cornerRadius = CornerRadius(side * 0.24f, side * 0.24f),
        )

        // Barrido de luz sobre la portada para dar sensación de movimiento.
        clipRect(left = left, top = top, right = left + side, bottom = top + side) {
            val bandWidth = side * 0.42f
            val bandX = left - bandWidth + (side + bandWidth * 2f) * sheen
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.28f),
                        Color.Transparent,
                    ),
                    start = Offset(bandX, top),
                    end = Offset(bandX + bandWidth, top + side),
                ),
                topLeft = Offset(bandX, top),
                size = Size(bandWidth, side),
            )
        }
    }
}

@Composable
private fun RecognitionHero(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "hero_recognition")
    val ripple by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "hero_recognition_ripple",
    )
    val bob by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "hero_recognition_bob",
    )

    Canvas(modifier = modifier.size(206.dp)) {
        val center = this.center
        val unit = size.minDimension
        val micWidth = unit * 0.22f
        val micHeight = unit * 0.34f
        val micCenter = Offset(center.x - unit * 0.04f, center.y - unit * 0.02f)

        repeat(3) { ring ->
            val progress = (ripple + ring / 3f) % 1f
            drawCircle(
                color = scheme.primary.copy(alpha = (1f - progress) * 0.30f),
                radius = micWidth * (1.1f + 2.1f * progress),
                center = micCenter,
                style = Stroke(width = 2.dp.toPx()),
            )
        }

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(scheme.primary, scheme.tertiary),
                startY = micCenter.y - micHeight / 2f,
                endY = micCenter.y + micHeight / 2f,
            ),
            topLeft = Offset(micCenter.x - micWidth / 2f, micCenter.y - micHeight / 2f),
            size = Size(micWidth, micHeight),
            cornerRadius = CornerRadius(micWidth / 2f, micWidth / 2f),
        )

        val cradleRadius = micWidth * 0.78f
        drawArc(
            color = scheme.onSurfaceVariant.copy(alpha = 0.5f),
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(micCenter.x - cradleRadius, micCenter.y - cradleRadius * 0.55f),
            size = Size(cradleRadius * 2f, cradleRadius * 2f),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
        )

        // Nota musical flotante junto al micrófono.
        val noteX = center.x + unit * 0.26f
        val noteY = center.y - unit * 0.18f + bob * unit * 0.02f
        drawCircle(
            color = scheme.tertiary,
            radius = unit * 0.045f,
            center = Offset(noteX, noteY),
        )
        drawLine(
            color = scheme.tertiary,
            start = Offset(noteX + unit * 0.045f, noteY),
            end = Offset(noteX + unit * 0.045f, noteY - unit * 0.16f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun OfflineHero(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "hero_offline")
    val progress by transition.animateFloat(
        initialValue = 0.08f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "hero_offline_progress",
    )

    Canvas(modifier = modifier.size(200.dp)) {
        val center = this.center
        val unit = size.minDimension
        val ringRadius = unit * 0.30f
        val ringCenter = Offset(center.x, center.y - unit * 0.04f)
        val ringStroke = 7.dp.toPx()

        drawCircle(
            color = scheme.onSurfaceVariant.copy(alpha = 0.16f),
            radius = ringRadius,
            center = ringCenter,
            style = Stroke(width = ringStroke),
        )
        drawArc(
            color = scheme.primary,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = Offset(ringCenter.x - ringRadius, ringCenter.y - ringRadius),
            size = Size(ringRadius * 2f, ringRadius * 2f),
            style = Stroke(width = ringStroke, cap = StrokeCap.Round),
        )

        val arrowTop = ringCenter.y - unit * 0.14f
        val arrowBottom = ringCenter.y + unit * 0.09f
        drawLine(
            color = scheme.primary,
            start = Offset(ringCenter.x, arrowTop),
            end = Offset(ringCenter.x, arrowBottom),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = scheme.primary,
            start = Offset(ringCenter.x - unit * 0.075f, arrowBottom - unit * 0.075f),
            end = Offset(ringCenter.x, arrowBottom),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = scheme.primary,
            start = Offset(ringCenter.x + unit * 0.075f, arrowBottom - unit * 0.075f),
            end = Offset(ringCenter.x, arrowBottom),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawLine(
            color = scheme.tertiary,
            start = Offset(ringCenter.x - unit * 0.115f, ringCenter.y + unit * 0.17f),
            end = Offset(ringCenter.x + unit * 0.115f, ringCenter.y + unit * 0.17f),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun StatsHero(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val transition = rememberInfiniteTransition(label = "hero_stats")
    val grow by transition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "hero_stats_grow",
    )

    Canvas(modifier = modifier.size(width = 224.dp, height = 168.dp)) {
        val bars = 6
        val gap = size.width * 0.045f
        val barWidth = (size.width - gap * (bars + 1)) / bars
        val fractions = listOf(0.42f, 0.68f, 0.52f, 0.88f, 0.62f, 0.96f)
        val baseline = size.height - 4.dp.toPx()

        drawLine(
            color = scheme.onSurfaceVariant.copy(alpha = 0.25f),
            start = Offset(0f, baseline),
            end = Offset(size.width, baseline),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )

        for (i in 0 until bars) {
            val height = size.height * fractions[i] * grow
            val left = gap + i * (barWidth + gap)
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(scheme.primary, scheme.tertiary),
                    startY = baseline - height,
                    endY = baseline,
                ),
                topLeft = Offset(left, baseline - height),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
            )
        }
    }
}
