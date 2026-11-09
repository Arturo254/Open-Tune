/*
 * OpenTune Project Original (2026)
 * Arturo254 (github.com/Arturo254)
 * Licensed Under GPL-3.0 | see git history for contributors
 */

package com.arturo254.opentune.ui.screens

import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.size.Size
import coil3.toBitmap
import com.arturo254.opentune.LocalDatabase
import com.arturo254.opentune.LocalDownloadUtil
import com.arturo254.opentune.LocalPlayerAwareWindowInsets
import com.arturo254.opentune.LocalPlayerConnection
import com.arturo254.opentune.R
import com.arturo254.opentune.constants.AppBarHeight
import com.arturo254.opentune.constants.HideExplicitKey
import com.arturo254.opentune.db.entities.Album
import com.arturo254.opentune.extensions.togglePlayPause
import com.arturo254.opentune.innertube.YouTube
import com.arturo254.opentune.playback.ExoDownloadService
import com.arturo254.opentune.playback.queues.LocalAlbumRadio
import com.arturo254.opentune.ui.component.IconButton
import com.arturo254.opentune.ui.component.LocalMenuState
import com.arturo254.opentune.ui.component.NavigationTitle
import com.arturo254.opentune.ui.component.SongListItem
import com.arturo254.opentune.ui.component.YouTubeGridItem
import com.arturo254.opentune.ui.component.shimmer.ButtonPlaceholder
import com.arturo254.opentune.ui.component.shimmer.ListItemPlaceHolder
import com.arturo254.opentune.ui.component.shimmer.ShimmerHost
import com.arturo254.opentune.ui.component.shimmer.TextPlaceholder
import com.arturo254.opentune.ui.menu.AlbumMenu
import com.arturo254.opentune.ui.menu.SelectionSongMenu
import com.arturo254.opentune.ui.menu.SongMenu
import com.arturo254.opentune.ui.menu.YouTubeAlbumMenu
import com.arturo254.opentune.ui.utils.ItemWrapper
import com.arturo254.opentune.ui.utils.appBarContentColor
import com.arturo254.opentune.ui.utils.backToMain
import com.arturo254.opentune.ui.utils.transparentTopAppBarColors
import com.arturo254.opentune.utils.makeTimeString
import com.arturo254.opentune.utils.rememberPreference
import com.arturo254.opentune.viewmodels.AlbumUiState
import com.arturo254.opentune.viewmodels.AlbumViewModel
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

// ============================================================================
// CONSTANTES DE DISEÑO (Material 3 Expressive)
// ============================================================================

private val ExpressiveCorner = 28.dp
private val ListOuterCorner = 24.dp
private val ListInnerCorner = 6.dp
private val ListGap = 2.dp
private val ScreenHorizontalPadding = 16.dp
private val ActionButtonSize = 52.dp

// ============================================================================
// HERO DEL ÁLBUM: CARÁTULA COMO OBJETO (FUNDA + VINILO)
// ============================================================================

private val HeroCoverSize = 236.dp
private val HeroVinylSize = 214.dp
private val HeroVinylPeek = 54.dp
private val HeroCoverCorner = 30.dp

/**
 * Cabecera de la pantalla de álbum.
 *
 * En vez de una portada a sangre con un degradado tonal de fondo, la carátula
 * se presenta como un objeto físico: un vinilo que asoma por detrás —y que gira
 * mientras el álbum suena— sobre el que se apoya la funda con su sombra y un
 * reflejo sutil. El conjunto reacciona al scroll con un parallax suave.
 */
@Composable
private fun AlbumHero(
    title: String,
    thumbnailUrl: String?,
    systemBarsTopPadding: Dp,
    scrollProgress: Float,
    isActive: Boolean,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    actions: @Composable () -> Unit,
) {
    // Entrada: la funda se asienta y el vinilo se desliza hacia fuera.
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { revealed = true }
    val reveal by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "albumHeroReveal",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(systemBarsTopPadding + AppBarHeight + 16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HeroCoverSize)
                .graphicsLayer {
                    translationY = -scrollProgress * 48.dp.toPx()
                    val scale = 1f - scrollProgress * 0.08f
                    scaleX = scale
                    scaleY = scale
                    alpha = (1f - scrollProgress * 0.85f).coerceIn(0f, 1f)
                },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(HeroCoverSize + HeroVinylPeek)
                    .height(HeroCoverSize),
            ) {
                // Vinilo: sale de detrás de la funda a medida que entra el hero.
                VinylDisc(
                    thumbnailUrl = thumbnailUrl,
                    spinning = isActive && isPlaying,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (HeroCoverSize - HeroVinylSize) + HeroVinylPeek * reveal)
                        .size(HeroVinylSize)
                        .graphicsLayer { alpha = reveal },
                )

                // Funda: la carátula propiamente dicha.
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(HeroCoverSize)
                        .graphicsLayer {
                            val scale = 0.92f + 0.08f * reveal
                            scaleX = scale
                            scaleY = scale
                            rotationZ = (1f - reveal) * -5f
                        }
                        .shadow(
                            elevation = 20.dp,
                            shape = RoundedCornerShape(HeroCoverCorner),
                            clip = false,
                            ambientColor = Color.Black.copy(alpha = 0.40f),
                            spotColor = Color.Black.copy(alpha = 0.52f),
                        )
                        .clip(RoundedCornerShape(HeroCoverCorner))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                    contentAlignment = Alignment.Center,
                ) {
                    if (thumbnailUrl != null) {
                        AsyncImage(
                            model = thumbnailUrl,
                            contentDescription = title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.album),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(72.dp),
                        )
                    }

                    // Reflejo diagonal: da volumen al cartón de la funda.
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.16f),
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.12f),
                                    ),
                                ),
                            ),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        content()

        Spacer(modifier = Modifier.height(18.dp))

        actions()

        Spacer(modifier = Modifier.height(12.dp))
    }
}

/**
 * Vinilo dibujado por completo —surcos, brillo y canto— con la propia portada
 * como sello central. Gira de forma continua mientras el álbum suena y se
 * detiene sin saltos al pausarlo.
 */
@Composable
private fun VinylDisc(
    thumbnailUrl: String?,
    spinning: Boolean,
    modifier: Modifier = Modifier,
) {
    val discColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.92f)
    val grooveColor = MaterialTheme.colorScheme.surface
    val labelColor = MaterialTheme.colorScheme.surfaceContainerHighest

    val rotation = remember { Animatable(0f) }
    LaunchedEffect(spinning) {
        if (spinning) {
            while (true) {
                rotation.animateTo(
                    targetValue = rotation.value + 360f,
                    animationSpec = tween(durationMillis = 8_000, easing = LinearEasing),
                )
            }
        }
    }

    Box(
        modifier = modifier.graphicsLayer { rotationZ = rotation.value % 360f },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            drawCircle(color = discColor, radius = radius)

            var groove = radius * 0.42f
            val step = radius * 0.036f
            while (groove < radius * 0.97f) {
                drawCircle(
                    color = grooveColor.copy(alpha = 0.12f),
                    radius = groove,
                    style = Stroke(width = 1f),
                )
                groove += step
            }

            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        grooveColor.copy(alpha = 0.22f),
                        Color.Transparent,
                        Color.Transparent,
                    ),
                ),
                radius = radius,
            )
            drawCircle(
                color = grooveColor.copy(alpha = 0.18f),
                radius = radius - 1f,
                style = Stroke(width = 2.dp.toPx()),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize(0.38f)
                .clip(CircleShape)
                .background(labelColor),
            contentAlignment = Alignment.Center,
        ) {
            if (thumbnailUrl != null) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
            )
        }
    }
}

// ============================================================================
// DESCRIPCIÓN DEL ÁLBUM ("Acerca del álbum")
// ============================================================================

/**
 * Devuelve la nota editorial del álbum (la descripción que YouTube Music
 * muestra en la ficha del lanzamiento), o null si el lanzamiento no tiene una.
 * Si la petición falla (por ejemplo en un álbum que sólo existe en local) la
 * tarjeta simplemente no se muestra.
 */
private suspend fun fetchAlbumAbout(albumBrowseId: String): String? =
    YouTube.albumDescription(albumBrowseId)
        .getOrNull()
        ?.takeIf { it.isNotBlank() }

// ============================================================================
// FUNCIONES DE DESCARGA
// ============================================================================

/**
 * Descarga la carátula del álbum en la calidad especificada.
 * Todo el trabajo pesado (decodificación, compresión, I/O) corre en Dispatchers.IO.
 * @param quality Calidad: 512 (baja), 768 (media), 1024 (alta)
 */
suspend fun downloadAlbumCover(
    context: android.content.Context,
    imageUrl: String?,
    albumTitle: String,
    quality: Int = 1024,
): Boolean = withContext(Dispatchers.IO) {
    if (imageUrl.isNullOrEmpty()) return@withContext false

    try {
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(Size(quality, quality))
            .allowHardware(false)
            .build()

        val result = context.imageLoader.execute(request)
        val bitmap = result.image?.toBitmap() ?: return@withContext false

        val qualityLabel = when (quality) {
            512 -> "baja"
            768 -> "media"
            1024 -> "alta"
            else -> "custom"
        }

        val safeTitle = albumTitle.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        val filename = "${safeTitle}_${qualityLabel}_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/OpenTune")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val imageUri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return@withContext false

        context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 95, outputStream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            context.contentResolver.update(imageUri, contentValues, null, null)
        }

        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Timber.e(e, "downloadAlbumCover failed")
        false
    }
}

// ============================================================================
// COMPONENTES EXPRESSIVE REUTILIZABLES
// ============================================================================

/**
 * Botón tonal circular que "morfea" su forma al presionarse (círculo -> squircle),
 * con spring rebotón, siguiendo el lenguaje de movimiento de Material 3 Expressive.
 */
@Composable
private fun ExpressiveIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val corner by animateDpAsState(
        targetValue = if (pressed) 16.dp else ActionButtonSize / 2,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "iconButtonCorner"
    )

    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier.size(ActionButtonSize),
        shape = RoundedCornerShape(corner.coerceAtLeast(0.dp)),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        interactionSource = interactionSource,
        content = content
    )
}

/** Chip informativo con color propio (primary / secondary / tertiary container). */
@Composable
private fun InfoChip(
    @DrawableRes iconRes: Int,
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Tarjeta de estado (vacío / error / sin resultados) con contenedor tonal y esquinas grandes. */
@Composable
private fun StateMessageCard(
    title: String,
    description: String,
    @DrawableRes iconRes: Int,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenHorizontalPadding, vertical = 16.dp),
        shape = RoundedCornerShape(ExpressiveCorner),
        color = containerColor,
        contentColor = contentColor,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = contentColor.copy(alpha = 0.12f),
                contentColor = contentColor,
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(32.dp),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
            )
            actions()
        }
    }
}

// ----------------------------------------------------------------------------
// Búsqueda dentro del álbum
// ----------------------------------------------------------------------------

@Composable
private fun AlbumSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenHorizontalPadding, vertical = 8.dp)
            .focusRequester(focusRequester),
        placeholder = { Text("Buscar en este álbum") },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    onLongClick = {}
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = null,
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(ExpressiveCorner),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    )
}

// ----------------------------------------------------------------------------
// Acerca del álbum
// ----------------------------------------------------------------------------

@Composable
private fun AlbumAboutCard(
    text: String,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable(text) { mutableStateOf(false) }
    var overflowing by remember(text) { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenHorizontalPadding, vertical = 8.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
        shape = RoundedCornerShape(ListOuterCorner),
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Acerca del álbum",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (expanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { layout ->
                    if (!expanded) overflowing = layout.hasVisualOverflow
                },
            )
            if (overflowing || expanded) {
                Text(
                    text = if (expanded) "Ver menos" else "Ver más",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------
// Insignias de formato de audio (códec · bitrate · frecuencia)
// ----------------------------------------------------------------------------

private fun formatSampleRate(hz: Int): String {
    val khz = hz / 1000f
    return if (khz % 1f == 0f) "${khz.toInt()} kHz" else "%.1f kHz".format(khz)
}

@Composable
private fun AudioFormatBadges(
    codec: String,
    bitrateKbps: Int,
    sampleRateHz: Int?,
    modifier: Modifier = Modifier,
) {
    val codecLabel = when {
        codec.contains("opus", ignoreCase = true) -> "OPUS"
        codec.contains("mp4a", ignoreCase = true) -> "AAC"
        codec.contains("flac", ignoreCase = true) -> "FLAC"
        codec.contains("vorbis", ignoreCase = true) -> "VORBIS"
        else -> codec.substringBefore('.').uppercase()
    }
    val labels = buildList {
        if (codecLabel.isNotBlank()) add(codecLabel)
        if (bitrateKbps > 0) add("$bitrateKbps kbps")
        sampleRateHz?.takeIf { it > 0 }?.let { add(formatSampleRate(it)) }
    }
    if (labels.isEmpty()) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        labels.forEach { label ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}

// ============================================================================
// DIÁLOGO DE CALIDAD DE CARÁTULA
// ============================================================================

private data class QualityOptionData(
    val pixels: Int,
    val label: String,
    val approxSize: String,
)

private val QualityOptions = listOf(
    QualityOptionData(512, "Baja", "~50 KB"),
    QualityOptionData(768, "Media", "~100 KB"),
    QualityOptionData(1024, "Alta", "~200 KB"),
)

/**
 * Cada opción es una tarjeta que lidera con sus dimensiones en píxeles: lo que se
 * elige es "qué tan grande", no un punto de radio. El estado seleccionado es la
 * tarjeta completa iluminándose.
 */
@Composable
private fun QualityOptionCard(
    option: QualityOptionData,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "qualityContainer"
    )
    val corner by animateDpAsState(
        targetValue = if (selected) 28.dp else 16.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "qualityCorner"
    )
    val tileColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        },
        label = "qualityTile"
    )
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(corner.coerceAtLeast(0.dp)),
        color = containerColor,
        contentColor = contentColor,
        border = if (selected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(tileColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option.pixels.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${option.pixels} × ${option.pixels} · ${option.approxSize}",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f),
                )
            }

            if (selected) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun QualitySelectionDialog(
    onDismiss: () -> Unit,
    onQualitySelected: (Int) -> Unit,
) {
    var selectedQuality by remember { mutableStateOf(1024) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(ExpressiveCorner),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        icon = {
            Icon(
                painter = painterResource(R.drawable.download),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            Text(
                text = "Guardar carátula",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Elige una resolución. La imagen se guarda en Pictures/OpenTune.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                QualityOptions.forEach { option ->
                    QualityOptionCard(
                        option = option,
                        selected = selectedQuality == option.pixels,
                        onSelect = { selectedQuality = option.pixels },
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onQualitySelected(selectedQuality)
                    onDismiss()
                },
                shape = RoundedCornerShape(percent = 50),
            ) {
                Text(stringResource(R.string.download))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

// ============================================================================
// PANTALLA
// ============================================================================

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: AlbumViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val menuState = LocalMenuState.current
    val database = LocalDatabase.current
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return

    val scope = rememberCoroutineScope()

    val isPlaying by playerConnection.isPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val currentFormat by playerConnection.currentFormat.collectAsState(initial = null)

    val playlistId by viewModel.playlistId.collectAsState()
    val albumWithSongs by viewModel.albumWithSongs.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val otherVersions by viewModel.otherVersions.collectAsState()
    val hideExplicit by rememberPreference(key = HideExplicitKey, defaultValue = false)

    // Nota editorial del álbum, cuando el lanzamiento tiene una
    val albumBrowseId = albumWithSongs?.album?.id
    var albumAbout by remember(albumBrowseId) { mutableStateOf<String?>(null) }
    LaunchedEffect(albumBrowseId) {
        val id = albumBrowseId ?: return@LaunchedEffect
        albumAbout = withContext(Dispatchers.IO) {
            runCatching { fetchAlbumAbout(id) }.getOrNull()
        }
    }

    // System bars padding
    val systemBarsTopPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()

    val surfaceColor = MaterialTheme.colorScheme.surface

    // Estados para descarga de carátula
    var downloadingCover by remember { mutableStateOf(false) }
    var showQualityDialog by remember { mutableStateOf(false) }

    val wrappedSongs = remember(albumWithSongs, hideExplicit) {
        val filteredSongs = if (hideExplicit) {
            albumWithSongs?.songs?.filter { !it.song.explicit } ?: emptyList()
        } else {
            albumWithSongs?.songs ?: emptyList()
        }
        filteredSongs.map { item -> ItemWrapper(item) }.toMutableStateList()
    }

    LaunchedEffect(uiState, wrappedSongs.size) {
        Timber.d(
            "AlbumScreen: state=$uiState | albumNonNull=${albumWithSongs != null} | " +
                    "dbSongs=${albumWithSongs?.songs?.size ?: "null"} | " +
                    "wrappedSongs=${wrappedSongs.size} | otherVersions=${otherVersions.size}"
        )
    }

    var selection by remember { mutableStateOf(false) }

    // Búsqueda dentro del álbum
    var searching by rememberSaveable(albumBrowseId) { mutableStateOf(false) }
    var query by rememberSaveable(albumBrowseId) { mutableStateOf("") }
    val closeSearch = {
        searching = false
        query = ""
    }
    BackHandler(enabled = searching && !selection) { closeSearch() }

    if (selection) {
        BackHandler {
            selection = false
        }
    }

    // Canciones visibles conservando su índice real para la numeración
    val visibleSongs = remember(wrappedSongs, query) {
        val q = query.trim()
        wrappedSongs.withIndex().filter { (_, wrapper) ->
            q.isEmpty() ||
                    wrapper.item.song.title.contains(q, ignoreCase = true) ||
                    wrapper.item.artists.any { it.name.contains(q, ignoreCase = true) }
        }
    }

    val downloadUtil = LocalDownloadUtil.current
    var downloadState by remember { mutableStateOf(Download.STATE_STOPPED) }

    LaunchedEffect(albumWithSongs) {
        val songs = albumWithSongs?.songs?.map { it.id }
        if (songs.isNullOrEmpty()) return@LaunchedEffect
        downloadUtil.downloads.collect { downloads ->
            downloadState =
                if (songs.all { downloads[it]?.state == Download.STATE_COMPLETED }) {
                    Download.STATE_COMPLETED
                } else if (songs.all {
                        downloads[it]?.state == Download.STATE_QUEUED ||
                                downloads[it]?.state == Download.STATE_DOWNLOADING ||
                                downloads[it]?.state == Download.STATE_COMPLETED
                    }
                ) {
                    Download.STATE_DOWNLOADING
                } else {
                    Download.STATE_STOPPED
                }
        }
    }

    // State for LazyColumn to track scroll
    val lazyListState = rememberLazyListState()

    // Al abrir la búsqueda, lleva la lista al campo de búsqueda
    LaunchedEffect(searching) {
        if (searching) {
            runCatching { lazyListState.animateScrollToItem(1) }
        }
    }

    // Progreso del scroll del hero: 0 mientras está arriba del todo, 1 cuando
    // ya ha salido de pantalla. Alimenta el parallax de la carátula.
    val heroScrollProgress by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex == 0) {
                (lazyListState.firstVisibleItemScrollOffset / 640f).coerceIn(0f, 1f)
            } else {
                1f
            }
        }
    }

    val showTopBarTitle by remember {
        derivedStateOf { lazyListState.firstVisibleItemIndex > 0 }
    }

    // La app bar va siempre con fondo transparente sobre el color de superficie,
    // así que basta con el color de contenido habitual.
    val appBarTint = appBarContentColor()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceColor),
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        ) {
            // Muestra el contenido en cuanto hay canciones (aunque la red siga cargando
            // o haya fallado al refrescar), o cuando el estado ya es Content.
            val albumData = albumWithSongs
            val showContent = albumData != null &&
                    (albumData.songs.isNotEmpty() || uiState == AlbumUiState.Content)

            if (albumData != null && showContent) {
                // -------------------------------------------------------
                // Hero
                // -------------------------------------------------------
                item(key = "hero") {
                    AlbumHero(
                        title = albumData.album.title,
                        thumbnailUrl = albumData.album.thumbnailUrl,
                        systemBarsTopPadding = systemBarsTopPadding,
                        scrollProgress = heroScrollProgress,
                        isActive = mediaMetadata?.album?.id == albumData.album.id,
                        isPlaying = isPlaying,
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = albumData.album.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.primary
                                            ).toSpanStyle()
                                        ) {
                                            albumData.artists.fastForEachIndexed { index, artist ->
                                                val link = LinkAnnotation.Clickable(artist.id) {
                                                    navController.navigate("artist/${artist.id}")
                                                }
                                                withLink(link) {
                                                    append(artist.name)
                                                }
                                                if (index != albumData.artists.lastIndex) {
                                                    append(", ")
                                                }
                                            }
                                        }
                                    },
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = ScreenHorizontalPadding),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    albumData.album.year?.let { year ->
                                        InfoChip(
                                            iconRes = R.drawable.calendar_today,
                                            text = year.toString(),
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        )
                                    }

                                    InfoChip(
                                        iconRes = R.drawable.music_note,
                                        text = pluralStringResource(
                                            R.plurals.n_song,
                                            wrappedSongs.size,
                                            wrappedSongs.size
                                        ),
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    )

                                    val totalDuration = albumData.songs.sumOf { it.song.duration }
                                    if (totalDuration > 0) {
                                        InfoChip(
                                            iconRes = R.drawable.timer,
                                            text = makeTimeString(totalDuration * 1000L),
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                        )
                                    }
                                }

                                // Formato de audio, solo mientras suena una canción de este álbum
                                if (mediaMetadata?.album?.id == albumData.album.id) {
                                    currentFormat?.let { format ->
                                        Spacer(modifier = Modifier.height(10.dp))
                                        AudioFormatBadges(
                                            codec = format.codecs,
                                            bitrateKbps = format.bitrate / 1000,
                                            sampleRateHz = format.sampleRate,
                                        )
                                    }
                                }
                            }
                        },
                        actions = {
                            val isLiked = albumData.album.bookmarkedAt != null

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = ScreenHorizontalPadding),
                                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Me gusta (con animación de rebote)
                                ExpressiveIconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        database.query {
                                            update(albumData.album.toggleLike())
                                        }
                                    },
                                    containerColor = if (isLiked)
                                        MaterialTheme.colorScheme.errorContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceContainerHigh,
                                    contentColor = if (isLiked)
                                        MaterialTheme.colorScheme.onErrorContainer
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                ) {
                                    AnimatedContent(
                                        targetState = isLiked,
                                        transitionSpec = {
                                            (scaleIn(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            ) + fadeIn()) togetherWith (scaleOut() + fadeOut())
                                        },
                                        label = "likeIcon"
                                    ) { liked ->
                                        Icon(
                                            painter = painterResource(
                                                if (liked) R.drawable.favorite else R.drawable.favorite_border
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                // Reproducir: botón principal en forma de píldora que morfea
                                val playInteraction = remember { MutableInteractionSource() }
                                val playPressed by playInteraction.collectIsPressedAsState()
                                val playCorner by animateDpAsState(
                                    targetValue = if (playPressed) 18.dp else ActionButtonSize / 2,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    label = "playCorner"
                                )

                                Button(
                                    onClick = {
                                        playerConnection.service.getAutomix(playlistId)
                                        playerConnection.playQueue(
                                            LocalAlbumRadio(albumData),
                                        )
                                    },
                                    shape = RoundedCornerShape(playCorner.coerceAtLeast(0.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                    interactionSource = playInteraction,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(ActionButtonSize)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.play),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.play),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }

                                // Aleatorio
                                ExpressiveIconButton(
                                    onClick = {
                                        playerConnection.service.getAutomix(playlistId)
                                        playerConnection.playQueue(
                                            LocalAlbumRadio(albumData.copy(songs = albumData.songs.shuffled())),
                                        )
                                    },
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.shuffle),
                                        contentDescription = stringResource(R.string.shuffle),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                // Descarga (con estado animado)
                                ExpressiveIconButton(
                                    onClick = {
                                        when (downloadState) {
                                            Download.STATE_COMPLETED,
                                            Download.STATE_DOWNLOADING -> {
                                                albumData.songs.forEach { song ->
                                                    DownloadService.sendRemoveDownload(
                                                        context,
                                                        ExoDownloadService::class.java,
                                                        song.id,
                                                        false,
                                                    )
                                                }
                                            }

                                            else -> {
                                                albumData.songs.forEach { song ->
                                                    val downloadRequest =
                                                        DownloadRequest
                                                            .Builder(song.id, song.id.toUri())
                                                            .setCustomCacheKey(song.id)
                                                            .setData(song.song.title.toByteArray())
                                                            .build()
                                                    DownloadService.sendAddDownload(
                                                        context,
                                                        ExoDownloadService::class.java,
                                                        downloadRequest,
                                                        false,
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    containerColor = if (downloadState == Download.STATE_COMPLETED)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceContainerHigh,
                                    contentColor = if (downloadState == Download.STATE_COMPLETED)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant,
                                ) {
                                    AnimatedContent(
                                        targetState = downloadState,
                                        transitionSpec = {
                                            (scaleIn(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            ) + fadeIn()) togetherWith (scaleOut() + fadeOut())
                                        },
                                        label = "downloadIcon"
                                    ) { state ->
                                        when (state) {
                                            Download.STATE_COMPLETED -> {
                                                Icon(
                                                    painter = painterResource(R.drawable.offline),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }

                                            Download.STATE_DOWNLOADING -> {
                                                CircularProgressIndicator(
                                                    strokeWidth = 2.dp,
                                                    modifier = Modifier.size(24.dp),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            else -> {
                                                Icon(
                                                    painter = painterResource(R.drawable.download),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Más opciones
                                ExpressiveIconButton(
                                    onClick = {
                                        menuState.show {
                                            AlbumMenu(
                                                originalAlbum = Album(
                                                    albumData.album,
                                                    albumData.artists
                                                ),
                                                navController = navController,
                                                onDismiss = menuState::dismiss,
                                            )
                                        }
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.more_vert),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    )
                }

                // -------------------------------------------------------
                // Acerca del álbum (oculto mientras se busca)
                // -------------------------------------------------------
                albumAbout?.takeIf { it.isNotBlank() && !searching }?.let { about ->
                    item(key = "about_album") {
                        AlbumAboutCard(text = about)
                    }
                }

                item(key = "songs_header") {
                    NavigationTitle(
                        title = stringResource(R.string.songs),
                    )
                }

                // -------------------------------------------------------
                // Campo de búsqueda
                // -------------------------------------------------------
                if (searching) {
                    item(key = "search") {
                        AlbumSearchField(
                            query = query,
                            onQueryChange = { query = it },
                        )
                    }
                }

                // -------------------------------------------------------
                // Lista de canciones segmentada (esquinas grandes en los
                // extremos y pequeñas entre ítems)
                // -------------------------------------------------------
                if (wrappedSongs.isEmpty()) {
                    item(key = "songs_empty") {
                        StateMessageCard(
                            title = stringResource(R.string.empty_album),
                            description = stringResource(R.string.empty_album_desc),
                            iconRes = R.drawable.album,
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                } else if (visibleSongs.isEmpty()) {
                    item(key = "no_matches") {
                        StateMessageCard(
                            title = "Sin resultados",
                            description = "Nada coincide con “${query.trim()}”",
                            iconRes = R.drawable.search,
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                } else {
                    itemsIndexed(
                        items = visibleSongs,
                        key = { _, entry -> entry.value.item.id },
                    ) { position, entry ->
                        val index = entry.index
                        val songWrapper = entry.value
                        val lastPosition = visibleSongs.lastIndex

                        val itemShape = when {
                            lastPosition == 0 -> RoundedCornerShape(ListOuterCorner)
                            position == 0 -> RoundedCornerShape(
                                topStart = ListOuterCorner,
                                topEnd = ListOuterCorner,
                                bottomEnd = ListInnerCorner,
                                bottomStart = ListInnerCorner,
                            )

                            position == lastPosition -> RoundedCornerShape(
                                topStart = ListInnerCorner,
                                topEnd = ListInnerCorner,
                                bottomEnd = ListOuterCorner,
                                bottomStart = ListOuterCorner,
                            )

                            else -> RoundedCornerShape(ListInnerCorner)
                        }

                        val isItemSelected = songWrapper.isSelected && selection
                        val itemContainer by animateColorAsState(
                            targetValue = if (isItemSelected)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceContainer,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                            label = "songContainer"
                        )

                        Box(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth()
                                .padding(
                                    start = ScreenHorizontalPadding,
                                    end = ScreenHorizontalPadding,
                                    bottom = if (position == lastPosition) 0.dp else ListGap,
                                )
                                .clip(itemShape)
                                .background(itemContainer)
                        ) {
                            SongListItem(
                                song = songWrapper.item,
                                albumIndex = index + 1,
                                isActive = songWrapper.item.id == mediaMetadata?.id,
                                isPlaying = isPlaying,
                                showInLibraryIcon = true,
                                trailingContent = {
                                    IconButton(
                                        onClick = {
                                            menuState.show {
                                                SongMenu(
                                                    originalSong = songWrapper.item,
                                                    navController = navController,
                                                    onDismiss = menuState::dismiss,
                                                )
                                            }
                                        },
                                        onLongClick = {}
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.more_vert),
                                            contentDescription = null,
                                        )
                                    }
                                },
                                isSelected = isItemSelected,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            if (!selection) {
                                                if (songWrapper.item.id == mediaMetadata?.id) {
                                                    playerConnection.player.togglePlayPause()
                                                } else {
                                                    // Índice real dentro del álbum completo: sigue
                                                    // siendo correcto con búsqueda o ocultar explícitas.
                                                    val startIndex = albumData.songs
                                                        .indexOfFirst { it.id == songWrapper.item.id }
                                                        .coerceAtLeast(0)
                                                    playerConnection.service.getAutomix(playlistId)
                                                    playerConnection.playQueue(
                                                        LocalAlbumRadio(albumData, startIndex = startIndex),
                                                    )
                                                }
                                            } else {
                                                songWrapper.isSelected = !songWrapper.isSelected
                                            }
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            if (!selection) {
                                                selection = true
                                            }
                                            wrappedSongs.forEach { it.isSelected = false }
                                            songWrapper.isSelected = true
                                        },
                                    ),
                            )
                        }
                    }

                    // El lanzamiento cierra con su duración total
                    if (!searching) {
                        item(key = "footer") {
                            val total = albumData.songs.sumOf { it.song.duration }
                            Text(
                                text = buildList {
                                    add(
                                        pluralStringResource(
                                            R.plurals.n_song,
                                            wrappedSongs.size,
                                            wrappedSongs.size
                                        )
                                    )
                                    if (total > 0) add(makeTimeString(total * 1000L))
                                }.joinToString(" • "),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(
                                    start = 28.dp,
                                    end = 28.dp,
                                    top = 16.dp,
                                    bottom = 8.dp
                                ),
                            )
                        }
                    }
                }

                // -------------------------------------------------------
                // Otras versiones
                // -------------------------------------------------------
                if (otherVersions.isNotEmpty()) {
                    item(key = "other_versions_header") {
                        NavigationTitle(
                            title = stringResource(R.string.other_versions),
                        )
                    }
                    item(key = "other_versions_list") {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            items(
                                items = otherVersions.distinctBy { it.id },
                                key = { it.id },
                            ) { item ->
                                YouTubeGridItem(
                                    item = item,
                                    isActive = mediaMetadata?.album?.id == item.id,
                                    isPlaying = isPlaying,
                                    coroutineScope = scope,
                                    modifier = Modifier
                                        .combinedClickable(
                                            onClick = { navController.navigate("album/${item.id}") },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    YouTubeAlbumMenu(
                                                        albumItem = item,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }
                        }
                    }
                }
            } else {
                when (val state = uiState) {
                    AlbumUiState.Loading,
                    AlbumUiState.Content -> {
                        item(key = "shimmer") {
                            ShimmerHost {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = systemBarsTopPadding + AppBarHeight),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 8.dp, bottom = 20.dp)
                                            .size(240.dp)
                                            .shimmer()
                                            .clip(RoundedCornerShape(ExpressiveCorner))
                                            .background(MaterialTheme.colorScheme.onSurface)
                                    )

                                    TextPlaceholder(
                                        height = 28.dp,
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .padding(horizontal = 32.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    TextPlaceholder(
                                        height = 20.dp,
                                        modifier = Modifier.fillMaxWidth(0.4f)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 48.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        repeat(3) {
                                            TextPlaceholder(
                                                height = 32.dp,
                                                modifier = Modifier.width(70.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 24.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(ActionButtonSize)
                                                .shimmer()
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.onSurface)
                                        )
                                        ButtonPlaceholder(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(ActionButtonSize)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(ActionButtonSize)
                                                .shimmer()
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.onSurface)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                }

                                repeat(6) {
                                    ListItemPlaceHolder()
                                }
                            }
                        }
                    }

                    AlbumUiState.Empty -> {
                        item(key = "empty") {
                            StateMessageCard(
                                title = stringResource(R.string.empty_album),
                                description = stringResource(R.string.empty_album_desc),
                                iconRes = R.drawable.album,
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(top = systemBarsTopPadding + AppBarHeight),
                            )
                        }
                    }

                    is AlbumUiState.Error -> {
                        item(key = "error") {
                            StateMessageCard(
                                title = if (state.isNotFound) {
                                    stringResource(R.string.album_not_found)
                                } else {
                                    stringResource(R.string.error_unknown)
                                },
                                description = if (state.isNotFound) {
                                    stringResource(R.string.album_not_found_desc)
                                } else {
                                    stringResource(R.string.error_unknown)
                                },
                                iconRes = R.drawable.close,
                                containerColor = if (state.isNotFound)
                                    MaterialTheme.colorScheme.surfaceContainer
                                else
                                    MaterialTheme.colorScheme.errorContainer,
                                contentColor = if (state.isNotFound)
                                    MaterialTheme.colorScheme.onSurface
                                else
                                    MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(top = systemBarsTopPadding + AppBarHeight),
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.retry() },
                                    shape = RoundedCornerShape(percent = 50),
                                ) {
                                    Text(stringResource(R.string.retry))
                                }
                            }
                        }
                    }
                }
            }
        }

        // ---------------------------------------------------------------
        // Top App Bar — fondo siempre transparente sobre el color de superficie.
        // ---------------------------------------------------------------
        TopAppBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(1f),
            colors = transparentTopAppBarColors(appBarTint),
            scrollBehavior = scrollBehavior,
            title = {
                // 0 = sin título, 1 = título del álbum, 2 = contador de selección
                val titleMode = when {
                    selection -> 2
                    showTopBarTitle -> 1
                    else -> 0
                }
                AnimatedContent(
                    targetState = titleMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "topBarTitle"
                ) { mode ->
                    when (mode) {
                        2 -> {
                            val count = wrappedSongs.count { it.isSelected }
                            Text(
                                text = pluralStringResource(R.plurals.n_song, count, count),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        1 -> {
                            Text(
                                text = albumWithSongs?.album?.title.orEmpty(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        else -> Spacer(modifier = Modifier.size(0.dp))
                    }
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        if (selection) {
                            selection = false
                        } else {
                            navController.navigateUp()
                        }
                    },
                    onLongClick = {
                        if (!selection) {
                            navController.backToMain()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            if (selection) R.drawable.close else R.drawable.arrow_back
                        ),
                        contentDescription = null,
                        tint = appBarTint
                    )
                }
            },
            actions = {
                // Buscar dentro del álbum
                if (wrappedSongs.isNotEmpty()) {
                    IconButton(
                        onClick = { if (searching) closeSearch() else searching = true },
                        onLongClick = {}
                    ) {
                        Icon(
                            painter = painterResource(
                                if (searching) R.drawable.close else R.drawable.search
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = appBarTint
                        )
                    }
                }

                // Botón de descarga de carátula
                IconButton(
                    onClick = { if (!downloadingCover) showQualityDialog = true },
                    onLongClick = {}
                ) {
                    if (downloadingCover) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.download),
                            contentDescription = stringResource(R.string.download_cover_art),
                            modifier = Modifier.size(24.dp),
                            tint = appBarTint
                        )
                    }
                }

                if (selection) {
                    val count = wrappedSongs.count { it.isSelected }
                    IconButton(
                        onClick = {
                            val selectAll = count != wrappedSongs.size
                            wrappedSongs.forEach { it.isSelected = selectAll }
                        },
                        onLongClick = {}
                    ) {
                        Icon(
                            painter = painterResource(
                                if (count == wrappedSongs.size) R.drawable.deselect else R.drawable.select_all
                            ),
                            contentDescription = null,
                            tint = appBarTint
                        )
                    }

                    IconButton(
                        onClick = {
                            menuState.show {
                                SelectionSongMenu(
                                    songSelection = wrappedSongs.filter { it.isSelected }
                                        .map { it.item },
                                    onDismiss = menuState::dismiss,
                                    clearAction = { selection = false }
                                )
                            }
                        },
                        onLongClick = {}
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.more_vert),
                            contentDescription = null,
                            tint = appBarTint
                        )
                    }
                }
            }
        )

        // Diálogo para seleccionar calidad
        if (showQualityDialog) {
            QualitySelectionDialog(
                onDismiss = { showQualityDialog = false },
                onQualitySelected = { quality ->
                    downloadingCover = true
                    scope.launch {
                        try {
                            val success = downloadAlbumCover(
                                context = context,
                                imageUrl = albumWithSongs?.album?.thumbnailUrl,
                                albumTitle = albumWithSongs?.album?.title ?: "album",
                                quality = quality
                            )
                            Toast.makeText(
                                context,
                                if (success) "Carátula guardada en Pictures/OpenTune"
                                else "No se pudo guardar la carátula",
                                Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            downloadingCover = false
                        }
                    }
                }
            )
        }
    }
}