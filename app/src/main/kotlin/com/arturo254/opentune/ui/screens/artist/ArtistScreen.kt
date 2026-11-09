/*
 * OpenTune Project (2026)
 * Arturo254 (github.com/Arturo254)
 * Material 3 Expressive Redesign
 * Licensed Under GPL-3.0 | see git history for contributors
 */

package com.arturo254.opentune.ui.screens.artist

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.size.Size
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import com.arturo254.opentune.LocalDatabase
import com.arturo254.opentune.LocalPlayerAwareWindowInsets
import com.arturo254.opentune.LocalPlayerConnection
import com.arturo254.opentune.R
import com.arturo254.opentune.canvas.providers.AppleMusicArtistBackgroundProvider
import com.arturo254.opentune.constants.AppBarHeight
import com.arturo254.opentune.constants.DisableBlurKey
import com.arturo254.opentune.constants.HideExplicitKey
import com.arturo254.opentune.db.entities.Album
import com.arturo254.opentune.db.entities.ArtistEntity
import com.arturo254.opentune.extensions.togglePlayPause
import com.arturo254.opentune.extensions.toMediaItem
import com.arturo254.opentune.innertube.models.AlbumItem
import com.arturo254.opentune.innertube.models.ArtistItem
import com.arturo254.opentune.innertube.models.PlaylistItem
import com.arturo254.opentune.innertube.models.SongItem
import com.arturo254.opentune.innertube.models.WatchEndpoint
import com.arturo254.opentune.innertube.pages.ArtistPage
import com.arturo254.opentune.models.toMediaMetadata
import com.arturo254.opentune.playback.queues.ListQueue
import com.arturo254.opentune.playback.queues.YouTubeQueue
import com.arturo254.opentune.ui.component.AlbumGridItem
import com.arturo254.opentune.ui.component.HideOnScrollFAB
import com.arturo254.opentune.ui.component.IconButton
import com.arturo254.opentune.ui.component.LocalMenuState
import com.arturo254.opentune.ui.component.NavigationTitle
import com.arturo254.opentune.ui.component.SongListItem
import com.arturo254.opentune.ui.component.YouTubeGridItem
import com.arturo254.opentune.ui.component.YouTubeListItem
import com.arturo254.opentune.ui.component.shimmer.ButtonPlaceholder
import com.arturo254.opentune.ui.component.shimmer.ListItemPlaceHolder
import com.arturo254.opentune.ui.component.shimmer.ShimmerHost
import com.arturo254.opentune.ui.component.shimmer.TextPlaceholder
import com.arturo254.opentune.ui.menu.AlbumMenu
import com.arturo254.opentune.ui.menu.SongMenu
import com.arturo254.opentune.ui.menu.YouTubeAlbumMenu
import com.arturo254.opentune.ui.menu.YouTubeArtistMenu
import com.arturo254.opentune.ui.menu.YouTubePlaylistMenu
import com.arturo254.opentune.ui.menu.YouTubeSongMenu
import com.arturo254.opentune.ui.player.CanvasArtworkPlayer
import com.arturo254.opentune.ui.theme.PlayerColorExtractor
import com.arturo254.opentune.ui.utils.appBarScrim
import com.arturo254.opentune.ui.utils.backToMain
import com.arturo254.opentune.ui.utils.resize
import com.arturo254.opentune.ui.utils.transparentTopAppBarColors
import com.arturo254.opentune.utils.rememberPreference
import com.arturo254.opentune.viewmodels.ArtistViewModel
import com.valentinilk.shimmer.shimmer

// Estados del fondo de video
sealed class VideoBackgroundState {
    object Loading : VideoBackgroundState()
    object Empty : VideoBackgroundState()
    data class Success(val url: String) : VideoBackgroundState()
    data class Error(val message: String) : VideoBackgroundState()
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: BOTÓN SQUIRCLE M3 EXPRESSIVE CON FÍSICAS DE REBOTE
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ExpressiveSquircleIconButton(
    iconRes: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    shapeRadius: Int = 22,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "expressive_button_bounce"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(shapeRadius.dp),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
        modifier = modifier
            .size(54.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (isPressed) 1.dp else 4.dp,
                shape = RoundedCornerShape(shapeRadius.dp),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = contentDescription,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PANTALLA PRINCIPAL: ARTIST SCREEN (M3 EXPRESSIVE)
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ArtistViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val database = LocalDatabase.current
    val menuState = LocalMenuState.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val artistPage = viewModel.artistPage
    val libraryArtist by viewModel.libraryArtist.collectAsState()
    val librarySongs by viewModel.librarySongs.collectAsState()
    val libraryAlbums by viewModel.libraryAlbums.collectAsState()
    val hideExplicit by rememberPreference(key = HideExplicitKey, defaultValue = false)
    val (disableBlur) = rememberPreference(DisableBlurKey, false)

    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showLocal by rememberSaveable { mutableStateOf(false) }

    val systemBarsTopPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()

    var gradientColors by remember { mutableStateOf<List<Color>>(emptyList()) }
    val fallbackColor = MaterialTheme.colorScheme.surface.toArgb()
    val surfaceColor = MaterialTheme.colorScheme.surface

    val thumbnail = artistPage?.artist?.thumbnail ?: libraryArtist?.artist?.thumbnailUrl
    val artistName = artistPage?.artist?.title ?: libraryArtist?.artist?.name

    val storefront = remember {
        java.util.Locale.getDefault()
            .country
            .takeIf { it.length == 2 }
            ?.lowercase(java.util.Locale.ROOT)
            ?: "us"
    }

    // Video background dinámico
    val videoBackgroundState by produceState<VideoBackgroundState>(
        initialValue = VideoBackgroundState.Loading,
        artistName, storefront
    ) {
        value = if (!artistName.isNullOrBlank()) {
            try {
                val url = withContext(Dispatchers.IO) {
                    AppleMusicArtistBackgroundProvider.getByArtistName(
                        artistName = artistName,
                        storefront = storefront,
                    )
                }
                if (url.isNullOrBlank()) {
                    VideoBackgroundState.Empty
                } else {
                    delay(100)
                    VideoBackgroundState.Success(url)
                }
            } catch (e: Exception) {
                VideoBackgroundState.Error(e.message ?: "Failed to load background video")
            }
        } else {
            VideoBackgroundState.Empty
        }
    }

    // Extracción de colores para la malla de gradiente ambiental
    LaunchedEffect(thumbnail) {
        if (thumbnail != null) {
            val request = ImageRequest.Builder(context)
                .data(thumbnail)
                .size(Size(PlayerColorExtractor.Config.IMAGE_SIZE, PlayerColorExtractor.Config.IMAGE_SIZE))
                .allowHardware(false)
                .build()

            val result = runCatching { context.imageLoader.execute(request) }.getOrNull()

            if (result != null) {
                val bitmap = result.image?.toBitmap()
                if (bitmap != null) {
                    val palette = withContext(Dispatchers.Default) {
                        Palette.from(bitmap)
                            .maximumColorCount(PlayerColorExtractor.Config.MAX_COLOR_COUNT)
                            .resizeBitmapArea(PlayerColorExtractor.Config.BITMAP_AREA)
                            .generate()
                    }

                    gradientColors = PlayerColorExtractor.extractGradientColors(
                        palette = palette,
                        fallbackColor = fallbackColor
                    )
                }
            }
        } else {
            gradientColors = emptyList()
        }
    }

    val gradientAlpha by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex == 0) {
                val offset = lazyListState.firstVisibleItemScrollOffset
                (1f - (offset / 800f)).coerceIn(0f, 1f)
            } else {
                0f
            }
        }
    }

    val transparentAppBar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset < 120
        }
    }

    LaunchedEffect(libraryArtist) {
        showLocal = libraryArtist?.artist?.isLocal == true
    }

    Box(
        modifier = Modifier.fillMaxSize().background(surfaceColor)
    ) {
        // Reproductor de video de fondo
        if (videoBackgroundState is VideoBackgroundState.Success) {
            CanvasArtworkPlayer(
                primaryUrl = (videoBackgroundState as VideoBackgroundState.Success).url,
                fallbackUrl = null,
                isPlaying = true,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(-2f),
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(surfaceColor.copy(alpha = 0.55f))
        )

        // Malla reactiva de gradientes fluidos M3 Expressive
        if (!disableBlur && gradientColors.isNotEmpty() && gradientAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(0.68f)
                    .align(Alignment.TopCenter)
                    .zIndex(-1f)
                    .drawBehind {
                        val width = size.width
                        val height = size.height

                        if (gradientColors.size >= 3) {
                            val c0 = gradientColors[0]
                            val c1 = gradientColors[1]
                            val c2 = gradientColors[2]
                            val c3 = gradientColors.getOrElse(3) { c0 }
                            val c4 = gradientColors.getOrElse(4) { c1 }

                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(c0.copy(alpha = gradientAlpha * 0.75f), c0.copy(alpha = 0.40f), Color.Transparent),
                                    center = Offset(width * 0.5f, height * 0.18f),
                                    radius = width * 0.75f
                                )
                            )

                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(c1.copy(alpha = gradientAlpha * 0.55f), c1.copy(alpha = 0.28f), Color.Transparent),
                                    center = Offset(width * 0.15f, height * 0.35f),
                                    radius = width * 0.65f
                                )
                            )

                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(c2.copy(alpha = gradientAlpha * 0.50f), c2.copy(alpha = 0.24f), Color.Transparent),
                                    center = Offset(width * 0.85f, height * 0.42f),
                                    radius = width * 0.65f
                                )
                            )

                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(c3.copy(alpha = gradientAlpha * 0.32f), Color.Transparent),
                                    center = Offset(width * 0.35f, height * 0.65f),
                                    radius = width * 0.80f
                                )
                            )
                        } else if (gradientColors.isNotEmpty()) {
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(gradientColors[0].copy(alpha = gradientAlpha * 0.65f), Color.Transparent),
                                    center = Offset(width * 0.5f, height * 0.25f),
                                    radius = width * 0.85f
                                )
                            )
                        }

                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    surfaceColor.copy(alpha = gradientAlpha * 0.25f),
                                    surfaceColor.copy(alpha = gradientAlpha * 0.65f),
                                    surfaceColor
                                ),
                                startY = height * 0.38f,
                                endY = height
                            )
                        )
                    }
            )
        }

        LazyColumn(
            state = lazyListState,
            contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues(),
        ) {
            if (artistPage == null && !showLocal) {
                // Estado Shimmer (Placeholder M3E)
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
                                    .padding(top = 12.dp)
                                    .size(210.dp)
                                    .shimmer()
                                    .clip(RoundedCornerShape(44.dp))
                                    .background(MaterialTheme.colorScheme.onSurface)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            TextPlaceholder(
                                height = 36.dp,
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .shimmer()
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(MaterialTheme.colorScheme.onSurface)
                                )
                                ButtonPlaceholder(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(54.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .shimmer()
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(MaterialTheme.colorScheme.onSurface)
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        repeat(5) {
                            ListItemPlaceHolder()
                        }
                    }
                }
            } else {
                // ── HERO M3 EXPRESSIVE ──
                item(key = "header") {
                    val currentArtistName = artistPage?.artist?.title ?: libraryArtist?.artist?.name

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = systemBarsTopPadding + AppBarHeight),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar del Artista (Squircle M3E con elevación y halo)
                        Box(
                            modifier = Modifier
                                .padding(top = 10.dp, bottom = 18.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = RoundedCornerShape(48.dp),
                                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                                )
                                .clip(RoundedCornerShape(48.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(48.dp)
                                )
                        ) {
                            if (thumbnail != null) {
                                AsyncImage(
                                    model = thumbnail.resize(600, 600),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(210.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(210.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.person),
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Nombre del Artista con tipografía expresiva
                        Text(
                            text = currentArtistName ?: stringResource(R.string.unknown_artist),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        // Meta en vivo del artista: oyentes mensuales y suscriptores (M3 Expressive)
                        val monthlyListeners = artistPage?.artist?.monthlyListenerCountText
                        val subscriberCount = artistPage?.artist?.subscriberCountText
                        val listenerMeta = listOfNotNull(
                            monthlyListeners
                                ?.takeIf { it.isNotBlank() }
                                ?.let { "$it ${stringResource(R.string.monthly_listeners)}" },
                            subscriberCount
                                ?.takeIf { it.isNotBlank() }
                                ?.let { "$it ${stringResource(R.string.subscribers)}" },
                        )
                        if (listenerMeta.isNotEmpty()) {
                            Text(
                                text = listenerMeta.joinToString("  ·  "),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 8.dp, start = 24.dp, end = 24.dp)
                            )
                        }

                        // Descripción del Artista (Tarjeta M3E Expandible)
                        val description = artistPage?.description
                        if (!description.isNullOrBlank()) {
                            var isExpanded by rememberSaveable { mutableStateOf(false) }

                            Surface(
                                shape = RoundedCornerShape(28.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.70f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .combinedClickable(
                                            onClick = { isExpanded = !isExpanded },
                                            onLongClick = {}
                                        )
                                        .padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (!isExpanded && description.length > 110) {
                                            description.take(110).trimEnd() + "…"
                                        } else {
                                            description
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 20.sp
                                    )

                                    if (description.length > 110) {
                                        Text(
                                            text = stringResource(if (isExpanded) R.string.show_less else R.string.more),
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(top = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // ── ÚLTIMO LANZAMIENTO (M3 EXPRESSIVE) ──
                        val latestRelease = remember(artistPage, libraryAlbums, showLocal) {
                            buildLatestRelease(
                                artistPage = artistPage,
                                libraryAlbums = libraryAlbums,
                                showLocal = showLocal,
                            )
                        }
                        if (latestRelease != null) {
                            ExpressiveLatestReleaseCard(
                                release = latestRelease,
                                onOpen = { navController.navigate("album/${latestRelease.id}") },
                                onPlay = {
                                    val playlistId = latestRelease.playlistId
                                    if (!latestRelease.isLocal && playlistId != null) {
                                        playerConnection.playQueue(
                                            YouTubeQueue(WatchEndpoint(playlistId = playlistId))
                                        )
                                    } else {
                                        navController.navigate("album/${latestRelease.id}")
                                    }
                                },
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            )
                        }

                        // Fila de Estadísticas M3E (Tarjetas Tonales Compactas)
                        val songSections = artistPage?.sections?.filter { section ->
                            section.items.any { it is SongItem }
                        }
                        val songCount = if (showLocal) {
                            librarySongs.size
                        } else {
                            songSections
                                ?.flatMap { it.items }
                                ?.filterIsInstance<SongItem>()
                                ?.distinctBy { it.id }
                                ?.size ?: librarySongs.size
                        }
                        val hasMoreSongs = !showLocal && songSections?.any { it.moreEndpoint != null } == true

                        val albumSections = artistPage?.sections?.filter { section ->
                            section.items.any { it is AlbumItem }
                        }
                        val albumCount = if (showLocal) {
                            libraryAlbums.size
                        } else {
                            albumSections
                                ?.flatMap { it.items }
                                ?.filterIsInstance<AlbumItem>()
                                ?.distinctBy { it.id }
                                ?.size ?: libraryAlbums.size
                        }
                        val hasMoreAlbums = !showLocal && albumSections?.any { it.moreEndpoint != null } == true

                        if (songCount > 0 || albumCount > 0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                            ) {
                                if (songCount > 0) {
                                    ExpressiveStatCard(
                                        value = if (hasMoreSongs) "$songCount+" else songCount.toString(),
                                        label = stringResource(R.string.songs),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (albumCount > 0) {
                                    ExpressiveStatCard(
                                        value = if (hasMoreAlbums) "$albumCount+" else albumCount.toString(),
                                        label = stringResource(R.string.albums),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // ── ACCIONES M3 EXPRESSIVE ──
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // CTA principal: Reproducir todo (píldora con morfología de forma)
                            val playAllInteraction = remember { MutableInteractionSource() }
                            val playAllPressed by playAllInteraction.collectIsPressedAsState()
                            val playAllCorner by animateDpAsState(
                                targetValue = if (playAllPressed) 20.dp else 27.dp,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "playAllCorner"
                            )
                            val playAllScale by animateFloatAsState(
                                targetValue = if (playAllPressed) 0.97f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ),
                                label = "playAllScale"
                            )

                            Button(
                                onClick = {
                                    if (!showLocal) {
                                        artistPage?.artist?.playEndpoint?.let { endpoint ->
                                            playerConnection.playQueue(YouTubeQueue(endpoint))
                                        }
                                    } else if (librarySongs.isNotEmpty()) {
                                        playerConnection.playQueue(
                                            ListQueue(
                                                title = libraryArtist?.artist?.name ?: "Unknown Artist",
                                                items = librarySongs.map { it.toMediaItem() }
                                            )
                                        )
                                    }
                                },
                                interactionSource = playAllInteraction,
                                enabled = if (showLocal) librarySongs.isNotEmpty() else artistPage?.artist?.playEndpoint != null,
                                shape = RoundedCornerShape(playAllCorner),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .graphicsLayer {
                                        scaleX = playAllScale
                                        scaleY = playAllScale
                                    }
                                    .shadow(
                                        elevation = if (playAllPressed) 2.dp else 6.dp,
                                        shape = RoundedCornerShape(playAllCorner),
                                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.play),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.play_all),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Acciones secundarias: Suscribirse · Aleatorio · Radio
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Botón Suscribirse / Siguiendo (Squircle con animación de color)
                                val isSubscribed = libraryArtist?.artist?.bookmarkedAt != null
                                val subBgColor by animateColorAsState(
                                    targetValue = if (isSubscribed)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surfaceContainerHigh,
                                    label = "subBg"
                                )
                                val subContentColor by animateColorAsState(
                                    targetValue = if (isSubscribed)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    label = "subContent"
                                )

                                ExpressiveSquircleIconButton(
                                    iconRes = if (isSubscribed) R.drawable.done else R.drawable.add,
                                    contentDescription = stringResource(
                                        if (isSubscribed) R.string.subscribed else R.string.subscribe
                                    ),
                                    containerColor = subBgColor,
                                    contentColor = subContentColor,
                                    onClick = {
                                        database.transaction {
                                            val artist = libraryArtist?.artist
                                            if (artist != null) {
                                                update(artist.toggleLike())
                                            } else {
                                                artistPage?.artist?.let {
                                                    insert(
                                                        ArtistEntity(
                                                            id = it.id,
                                                            name = it.title,
                                                            channelId = it.channelId,
                                                            thumbnailUrl = it.thumbnail,
                                                        ).toggleLike()
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )

                                // Botón de Aleatorio en Squircle M3E
                                ExpressiveSquircleIconButton(
                                    iconRes = R.drawable.shuffle,
                                    contentDescription = stringResource(R.string.shuffle),
                                    onClick = {
                                        if (!showLocal) {
                                            artistPage?.artist?.shuffleEndpoint?.let { shuffleEndpoint ->
                                                playerConnection.playQueue(YouTubeQueue(shuffleEndpoint))
                                            }
                                        } else if (librarySongs.isNotEmpty()) {
                                            playerConnection.playQueue(
                                                ListQueue(
                                                    title = libraryArtist?.artist?.name ?: "Unknown Artist",
                                                    items = librarySongs.shuffled().map { it.toMediaItem() }
                                                )
                                            )
                                        }
                                    }
                                )

                                // Botón de Radio en Squircle M3E (si está disponible)
                                if (!showLocal && artistPage?.artist?.radioEndpoint != null) {
                                    ExpressiveSquircleIconButton(
                                        iconRes = R.drawable.radio,
                                        contentDescription = stringResource(R.string.radio),
                                        onClick = {
                                            artistPage.artist.radioEndpoint?.let {
                                                playerConnection.playQueue(YouTubeQueue(it))
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // ── SECCIÓN DE CONTENIDOS ──
                if (showLocal) {
                    // Canciones locales
                    if (librarySongs.isNotEmpty()) {
                        item {
                            NavigationTitle(
                                title = stringResource(R.string.songs),
                                onClick = { navController.navigate("artist/${viewModel.artistId}/songs") }
                            )
                        }

                        val filteredLibrarySongs = if (hideExplicit) {
                            librarySongs.filter { !it.song.explicit }
                        } else {
                            librarySongs
                        }

                        itemsIndexed(
                            items = filteredLibrarySongs.take(5),
                            key = { index, item -> "local_song_${item.id}_$index" }
                        ) { index, song ->
                            SongListItem(
                                song = song,
                                showInLibraryIcon = true,
                                isActive = song.id == mediaMetadata?.id,
                                isPlaying = isPlaying,
                                trailingContent = {
                                    IconButton(
                                        onClick = {
                                            menuState.show {
                                                SongMenu(
                                                    originalSong = song,
                                                    navController = navController,
                                                    onDismiss = menuState::dismiss,
                                                )
                                            }
                                        },
                                        onLongClick = {},
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.more_vert),
                                            contentDescription = null,
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            if (song.id == mediaMetadata?.id) {
                                                playerConnection.player.togglePlayPause()
                                            } else {
                                                playerConnection.playQueue(
                                                    ListQueue(
                                                        title = libraryArtist?.artist?.name ?: "Unknown Artist",
                                                        items = librarySongs.map { it.toMediaItem() },
                                                        startIndex = index
                                                    )
                                                )
                                            }
                                        },
                                        onLongClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            menuState.show {
                                                SongMenu(
                                                    originalSong = song,
                                                    navController = navController,
                                                    onDismiss = menuState::dismiss,
                                                )
                                            }
                                        },
                                    )
                                    .animateItem(),
                            )
                        }

                        if (filteredLibrarySongs.size > 5) {
                            item {
                                Surface(
                                    onClick = { navController.navigate("artist/${viewModel.artistId}/songs") },
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.view_all),
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    // Álbumes locales
                    if (libraryAlbums.isNotEmpty()) {
                        item {
                            NavigationTitle(
                                title = stringResource(R.string.albums),
                                onClick = { navController.navigate("artist/${viewModel.artistId}/albums") }
                            )
                        }

                        item {
                            val filteredLibraryAlbums = if (hideExplicit) {
                                libraryAlbums.filter { !it.album.explicit }
                            } else {
                                libraryAlbums
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = filteredLibraryAlbums,
                                    key = { album -> "local_album_${album.id}_${filteredLibraryAlbums.indexOf(album)}" }
                                ) { album ->
                                    AlbumGridItem(
                                        album = album,
                                        isActive = mediaMetadata?.album?.id == album.id,
                                        isPlaying = isPlaying,
                                        coroutineScope = coroutineScope,
                                        modifier = Modifier
                                            .combinedClickable(
                                                onClick = { navController.navigate("album/${album.id}") },
                                                onLongClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    menuState.show {
                                                        AlbumMenu(
                                                            originalAlbum = album,
                                                            navController = navController,
                                                            onDismiss = menuState::dismiss
                                                        )
                                                    }
                                                }
                                            )
                                            .animateItem()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Contenido remoto de YouTube Music
                    artistPage?.sections?.fastForEach { section ->
                        if (section.items.isNotEmpty()) {
                            item {
                                NavigationTitle(
                                    title = section.title,
                                    onClick = section.moreEndpoint?.let {
                                        {
                                            navController.navigate(
                                                "artist/${viewModel.artistId}/items?browseId=${it.browseId}&params=${it.params}",
                                            )
                                        }
                                    },
                                )
                            }
                        }

                        if ((section.items.firstOrNull() as? SongItem)?.album != null) {
                            items(
                                items = section.items.distinctBy { it.id },
                                key = { "youtube_song_${it.id}" },
                            ) { song ->
                                YouTubeListItem(
                                    item = song as SongItem,
                                    isActive = mediaMetadata?.id == song.id,
                                    isPlaying = isPlaying,
                                    trailingContent = {
                                        IconButton(
                                            onClick = {
                                                menuState.show {
                                                    YouTubeSongMenu(
                                                        song = song,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                            onLongClick = {},
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.more_vert),
                                                contentDescription = null,
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .combinedClickable(
                                            onClick = {
                                                if (song.id == mediaMetadata?.id) {
                                                    playerConnection.player.togglePlayPause()
                                                } else {
                                                    playerConnection.playQueue(
                                                        YouTubeQueue(
                                                            WatchEndpoint(videoId = song.id),
                                                            song.toMediaMetadata()
                                                        ),
                                                    )
                                                }
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                menuState.show {
                                                    YouTubeSongMenu(
                                                        song = song,
                                                        navController = navController,
                                                        onDismiss = menuState::dismiss,
                                                    )
                                                }
                                            },
                                        )
                                        .animateItem(),
                                )
                            }
                        } else {
                            item {
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(
                                        items = section.items.distinctBy { it.id },
                                        key = {
                                            val type = when (it) {
                                                is SongItem -> "song"
                                                is AlbumItem -> "album"
                                                is ArtistItem -> "artist"
                                                is PlaylistItem -> "playlist"
                                                else -> "item"
                                            }
                                            "youtube_${type}_${it.id}"
                                        },
                                    ) { item ->
                                        YouTubeGridItem(
                                            item = item,
                                            isActive = when (item) {
                                                is SongItem -> mediaMetadata?.id == item.id
                                                is AlbumItem -> mediaMetadata?.album?.id == item.id
                                                else -> false
                                            },
                                            isPlaying = isPlaying,
                                            coroutineScope = coroutineScope,
                                            modifier = Modifier
                                                .combinedClickable(
                                                    onClick = {
                                                        when (item) {
                                                            is SongItem -> playerConnection.playQueue(
                                                                YouTubeQueue(
                                                                    WatchEndpoint(videoId = item.id),
                                                                    item.toMediaMetadata()
                                                                ),
                                                            )
                                                            is AlbumItem -> navController.navigate("album/${item.id}")
                                                            is ArtistItem -> navController.navigate("artist/${item.id}")
                                                            is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
                                                        }
                                                    },
                                                    onLongClick = {
                                                        haptic.performHapticFeedback(
                                                            HapticFeedbackType.LongPress
                                                        )
                                                        menuState.show {
                                                            when (item) {
                                                                is SongItem ->
                                                                    YouTubeSongMenu(
                                                                        song = item,
                                                                        navController = navController,
                                                                        onDismiss = menuState::dismiss,
                                                                    )

                                                                is AlbumItem ->
                                                                    YouTubeAlbumMenu(
                                                                        albumItem = item,
                                                                        navController = navController,
                                                                        onDismiss = menuState::dismiss,
                                                                    )

                                                                is ArtistItem ->
                                                                    YouTubeArtistMenu(
                                                                        artist = item,
                                                                        onDismiss = menuState::dismiss,
                                                                    )

                                                                is PlaylistItem ->
                                                                    YouTubePlaylistMenu(
                                                                        playlist = item,
                                                                        coroutineScope = coroutineScope,
                                                                        onDismiss = menuState::dismiss,
                                                                    )
                                                            }
                                                        }
                                                    },
                                                )
                                                .animateItem(),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // FAB M3E para alternar vista local/remota
        HideOnScrollFAB(
            visible = librarySongs.isNotEmpty() && libraryArtist?.artist?.isLocal != true,
            lazyListState = lazyListState,
            icon = if (showLocal) R.drawable.language else R.drawable.library_music,
            onClick = {
                showLocal = showLocal.not()
                if (!showLocal && artistPage == null) viewModel.fetchArtistsFromYTM()
            }
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
                .align(Alignment.BottomCenter)
        )
    }

    // Top App Bar M3 Expressive con animación de título suave
    TopAppBar(
        title = {
            AnimatedVisibility(
                visible = !transparentAppBar,
                enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.94f),
                exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.94f)
            ) {
                Text(
                    text = artistPage?.artist?.title ?: libraryArtist?.artist?.name ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                    tint = if (transparentAppBar) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    viewModel.artistPage?.artist?.shareLink?.let { link ->
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Artist Link", link)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, R.string.link_copied, Toast.LENGTH_SHORT).show()
                    }
                },
                onLongClick = {},
            ) {
                Icon(
                    painter = painterResource(R.drawable.link),
                    contentDescription = null,
                    tint = if (transparentAppBar) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            viewModel.artistPage?.artist?.shareLink
                                ?: "https://music.youtube.com/channel/${viewModel.artistId}"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, null))
                },
                onLongClick = {},
            ) {
                Icon(
                    painter = painterResource(R.drawable.share),
                    contentDescription = null,
                    tint = if (transparentAppBar) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        },
        modifier = if (transparentAppBar) Modifier.appBarScrim() else Modifier,
        colors = if (transparentAppBar) {
            transparentTopAppBarColors(Color.White)
        } else {
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        },
    )
}

/**
 * Tarjeta de estadística M3 Expressive (Bento style)
 */
@Composable
private fun ExpressiveStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.70f),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTE: ÚLTIMO LANZAMIENTO DEL ARTISTA (M3 EXPRESSIVE)
// ─────────────────────────────────────────────────────────────────────────────

/** Tipo de un lanzamiento: YouTube Music usa el mismo renderer para los tres. */
private enum class ReleaseKind { Album, Single, Ep }

private data class ArtistLatestRelease(
    val id: String,
    val title: String,
    val year: Int?,
    val kind: ReleaseKind?,
    val thumbnail: String?,
    val isLocal: Boolean,
    val playlistId: String?,
)

/** Candidato interno a "último lanzamiento", con su posición para desempatar. */
private data class ReleaseCandidate(
    val item: AlbumItem,
    val kind: ReleaseKind?,
    val sectionIndex: Int,
    val itemIndex: Int,
)

/** Secciones que no son lanzamientos del artista (colaboraciones, mixes, etc.). */
private val NonReleaseSectionKeywords = listOf(
    "featured", "appears", "aparece", "destacado", "colaborac", "collab",
    "playlist", "mix", "radio", "similar", "video", "live", "en vivo", "fans",
)

/** Deduce el tipo de lanzamiento a partir del título de la sección ("Singles", "EPs"...). */
private fun releaseKindFromSection(sectionTitle: String): ReleaseKind? {
    val title = sectionTitle.lowercase()
    return when {
        title.contains("single") || title.contains("sencillo") -> ReleaseKind.Single
        Regex("\\beps?\\b").containsMatchIn(title) -> ReleaseKind.Ep
        title.contains("album") || title.contains("álbum") -> ReleaseKind.Album
        else -> null
    }
}

private fun isNonReleaseSection(sectionTitle: String): Boolean {
    val title = sectionTitle.lowercase()
    return NonReleaseSectionKeywords.any { title.contains(it) }
}

/**
 * Resuelve el lanzamiento más reciente del artista.
 *
 * YouTube Music sirve álbumes, sencillos y EPs con el mismo renderer
 * (`AlbumItem`), repartidos en secciones distintas ("Novedades", "Álbumes",
 * "Sencillos", "EPs"...). Aquí se reúnen todos los candidatos y se elige el más
 * reciente por año, usando el orden de las secciones como desempate, de modo
 * que un sencillo o un EP recién publicado también aparezca como último
 * lanzamiento. En modo local usa el álbum de mayor año de la biblioteca.
 */
private fun buildLatestRelease(
    artistPage: ArtistPage?,
    libraryAlbums: List<Album>,
    showLocal: Boolean,
): ArtistLatestRelease? {
    if (showLocal) {
        val album = libraryAlbums.maxByOrNull { it.album.year ?: Int.MIN_VALUE } ?: return null
        return ArtistLatestRelease(
            id = album.album.id,
            title = album.album.title,
            year = album.album.year,
            kind = null,
            thumbnail = album.album.thumbnailUrl,
            isLocal = true,
            playlistId = album.album.playlistId,
        )
    }

    val candidates = artistPage?.sections.orEmpty()
        .flatMapIndexed { sectionIndex, section ->
            if (isNonReleaseSection(section.title)) return@flatMapIndexed emptyList()
            val kind = releaseKindFromSection(section.title)
            section.items
                .filterIsInstance<AlbumItem>()
                .mapIndexed { itemIndex, item ->
                    ReleaseCandidate(item, kind, sectionIndex, itemIndex)
                }
        }
        .sortedWith(
            compareByDescending<ReleaseCandidate> { it.item.year }
                .thenBy { it.sectionIndex }
                .thenBy { it.itemIndex }
        )

    val best = candidates.firstOrNull() ?: return null

    return ArtistLatestRelease(
        id = best.item.id,
        title = best.item.title,
        year = best.item.year,
        kind = best.kind,
        thumbnail = best.item.thumbnail,
        isLocal = false,
        playlistId = best.item.playlistId,
    )
}

@Composable
private fun ExpressiveLatestReleaseCard(
    release: ArtistLatestRelease,
    onOpen: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.975f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "latestReleaseScale"
    )

    Surface(
        onClick = onOpen,
        interactionSource = interaction,
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.75f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (release.thumbnail != null) {
                    AsyncImage(
                        model = release.thumbnail,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.album),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(32.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.auto_awesome),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = stringResource(R.string.artist_latest_release),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }

                Text(
                    text = release.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                val typeLabel = when (release.kind) {
                    ReleaseKind.Album -> stringResource(R.string.albums)
                    ReleaseKind.Single -> stringResource(R.string.singles)
                    ReleaseKind.Ep -> stringResource(R.string.ep)
                    null -> null
                }
                val meta = listOfNotNull(
                    release.year?.toString(),
                    typeLabel,
                ).joinToString(" · ")
                if (meta.isNotBlank()) {
                    Text(
                        text = meta,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            val playInteraction = remember { MutableInteractionSource() }
            val playPressed by playInteraction.collectIsPressedAsState()
            val playScale by animateFloatAsState(
                targetValue = if (playPressed) 0.86f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "latestReleasePlayScale"
            )

            Surface(
                onClick = onPlay,
                interactionSource = playInteraction,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .size(52.dp)
                    .graphicsLayer {
                        scaleX = playScale
                        scaleY = playScale
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.play),
                        contentDescription = stringResource(R.string.play),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}