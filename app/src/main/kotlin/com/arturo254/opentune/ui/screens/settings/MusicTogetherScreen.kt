/*
 * OpenTune Project Original (2026)
 * Arturo254 (github.com/Arturo254)
 * Licensed Under GPL-3.0 | see git history for contributors
 */

package com.arturo254.opentune.ui.screens.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.arturo254.opentune.LocalPlayerAwareWindowInsets
import com.arturo254.opentune.LocalPlayerConnection
import com.arturo254.opentune.R
import com.arturo254.opentune.constants.TogetherAllowGuestsToAddTracksKey
import com.arturo254.opentune.constants.TogetherAllowGuestsToControlPlaybackKey
import com.arturo254.opentune.constants.TogetherDefaultPortKey
import com.arturo254.opentune.constants.TogetherDisplayNameKey
import com.arturo254.opentune.constants.TogetherLastJoinLinkKey
import com.arturo254.opentune.constants.TogetherRelayUrlKey
import com.arturo254.opentune.constants.TogetherRequireHostApprovalToJoinKey
import com.arturo254.opentune.constants.TogetherWelcomeShownKey
import com.arturo254.opentune.together.TogetherLink
import com.arturo254.opentune.together.TogetherOnlineEndpoint
import com.arturo254.opentune.together.TogetherParticipant
import com.arturo254.opentune.together.TogetherRole
import com.arturo254.opentune.together.TogetherRoomSettings
import com.arturo254.opentune.together.TogetherSessionState
import com.arturo254.opentune.ui.component.IconButton as AtIconButton
import com.arturo254.opentune.ui.component.TextFieldDialog
import com.arturo254.opentune.ui.utils.backToMain
import com.arturo254.opentune.utils.dataStore
import com.arturo254.opentune.utils.rememberPreference
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

private enum class TogetherMode { Nearby, Online }

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicTogetherScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val playerConnection = LocalPlayerConnection.current
    val clipboard = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    val (welcomeShown, setWelcomeShown) = rememberPreference(TogetherWelcomeShownKey, false)
    var welcomeDismissedThisSession by rememberSaveable { mutableStateOf(false) }
    val showWelcome = !welcomeShown && !welcomeDismissedThisSession

    if (showWelcome) {
        WelcomeDialog(
            onGotIt = { dontShowAgain ->
                welcomeDismissedThisSession = true
                if (dontShowAgain) setWelcomeShown(true)
            },
            onDismiss = { welcomeDismissedThisSession = true },
        )
    }

    val (displayName, setDisplayName) = rememberPreference(
        TogetherDisplayNameKey,
        defaultValue = Build.MODEL?.takeIf { it.isNotBlank() } ?: context.getString(R.string.app_name),
    )
    val (port, setPort) = rememberPreference(TogetherDefaultPortKey, defaultValue = 42117)
    val (allowAddTracks, setAllowAddTracksRaw) = rememberPreference(
        TogetherAllowGuestsToAddTracksKey, defaultValue = true
    )
    val (allowControlPlayback, setAllowControlPlaybackRaw) = rememberPreference(
        TogetherAllowGuestsToControlPlaybackKey, defaultValue = false
    )
    val (requireApproval, setRequireApprovalRaw) = rememberPreference(
        TogetherRequireHostApprovalToJoinKey, defaultValue = false
    )
    val (lastJoinLink, setLastJoinLink) = rememberPreference(
        TogetherLastJoinLinkKey, defaultValue = ""
    )

    val sessionStateFlow = remember(playerConnection) {
        playerConnection?.service?.togetherSessionState ?: MutableStateFlow(TogetherSessionState.Idle)
    }
    val sessionState by sessionStateFlow.collectAsState()

    val isHosting = sessionState is TogetherSessionState.Hosting || sessionState is TogetherSessionState.HostingOnline
    val isJoining = sessionState is TogetherSessionState.Joining || sessionState is TogetherSessionState.JoiningOnline
    val isHostRole = when (val state = sessionState) {
        is TogetherSessionState.Hosting, is TogetherSessionState.HostingOnline -> true
        is TogetherSessionState.Joined -> state.role is TogetherRole.Host
        else -> false
    }
    val isCreatingSessionLoading =
        ((sessionState as? TogetherSessionState.Hosting)?.roomState == null && sessionState is TogetherSessionState.Hosting) ||
                ((sessionState as? TogetherSessionState.HostingOnline)?.roomState == null && sessionState is TogetherSessionState.HostingOnline)
    val isJoinedAsGuest = (sessionState as? TogetherSessionState.Joined)?.role is TogetherRole.Guest
    val isWaitingApproval = run {
        val joined = sessionState as? TogetherSessionState.Joined ?: return@run false
        joined.role is TogetherRole.Guest &&
                joined.roomState.participants.firstOrNull { it.id == joined.selfParticipantId }?.isPending == true
    }
    val isJoinedAsAcceptedGuest = isJoinedAsGuest && !isWaitingApproval
    val disableJoinUi = isHostRole || isCreatingSessionLoading || isJoinedAsGuest

    var showNameDialog by rememberSaveable { mutableStateOf(false) }
    var showPortDialog by rememberSaveable { mutableStateOf(false) }
    var showJoinDialog by rememberSaveable { mutableStateOf(false) }

    val hostingLan = sessionState as? TogetherSessionState.Hosting
    val hostingOnline = sessionState as? TogetherSessionState.HostingOnline
    val hostParticipants = (hostingLan?.roomState ?: hostingOnline?.roomState)?.participants.orEmpty()
    val hostSettings = hostingLan?.settings ?: hostingOnline?.settings
    var confirmKickParticipantId by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmBanParticipantId by rememberSaveable { mutableStateOf<String?>(null) }
    val confirmKickName = hostParticipants.firstOrNull { it.id == confirmKickParticipantId }?.name
    val confirmBanName = hostParticipants.firstOrNull { it.id == confirmBanParticipantId }?.name

    var mode by rememberSaveable { mutableStateOf(TogetherMode.Nearby) }
    var onlineCode by rememberSaveable { mutableStateOf("") }
    var joinInput by rememberSaveable { mutableStateOf(lastJoinLink) }
    val canJoinLan = remember(joinInput) { TogetherLink.decode(joinInput) != null }
    val canJoinOnline = onlineCode.length >= 4
    val idle = sessionState is TogetherSessionState.Idle || sessionState is TogetherSessionState.Error

    LaunchedEffect(disableJoinUi, isJoining, isHosting) {
        if (disableJoinUi || isJoining || isHosting) showJoinDialog = false
    }

    fun pushSettingsToActiveSession(
        addTracks: Boolean = allowAddTracks,
        controlPlayback: Boolean = allowControlPlayback,
        approval: Boolean = requireApproval,
    ) {
        if (isHosting) {
            playerConnection?.service?.updateTogetherSettings(
                TogetherRoomSettings(
                    allowGuestsToAddTracks = addTracks,
                    allowGuestsToControlPlayback = controlPlayback,
                    requireHostApprovalToJoin = approval,
                )
            )
        }
    }

    val setAllowAddTracks: (Boolean) -> Unit = { v ->
        setAllowAddTracksRaw(v); pushSettingsToActiveSession(addTracks = v)
    }
    val setAllowControlPlayback: (Boolean) -> Unit = { v ->
        setAllowControlPlaybackRaw(v); pushSettingsToActiveSession(controlPlayback = v)
    }
    val setRequireApproval: (Boolean) -> Unit = { v ->
        setRequireApprovalRaw(v); pushSettingsToActiveSession(approval = v)
    }

    val copy: (String) -> Unit = { text ->
        clipboard.setText(AnnotatedString(text))
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
    }
    val share: (String) -> Unit = { text ->
        context.startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                },
                null,
            )
        )
    }

    // ── Dialogs ──────────────────────────────────────────────────────────────

    if (showNameDialog) {
        TextFieldDialog(
            title = { Text(stringResource(R.string.together_display_name)) },
            placeholder = { Text(stringResource(R.string.together_display_name_placeholder)) },
            isInputValid = { it.trim().isNotBlank() },
            onDone = { setDisplayName(it.trim()) },
            onDismiss = { showNameDialog = false },
        )
    }

    if (showPortDialog) {
        TextFieldDialog(
            title = { Text(stringResource(R.string.together_port)) },
            placeholder = { Text("42117") },
            isInputValid = { it.trim().toIntOrNull() in 1..65535 },
            onDone = { setPort(it.trim().toInt()) },
            onDismiss = { showPortDialog = false },
        )
    }

    if (showJoinDialog) {
        TextFieldDialog(
            title = { Text(stringResource(R.string.join_session)) },
            placeholder = { Text(stringResource(R.string.together_join_link_hint)) },
            singleLine = false,
            maxLines = 8,
            isInputValid = { TogetherLink.decode(it) != null },
            onDone = { raw ->
                val trimmed = raw.trim()
                joinInput = trimmed
                setLastJoinLink(trimmed)
                playerConnection?.service?.joinTogether(trimmed, displayName)
            },
            onDismiss = { showJoinDialog = false },
        )
    }

    if (confirmKickParticipantId != null) {
        ConfirmDialog(
            title = stringResource(R.string.together_kick),
            message = stringResource(R.string.together_kick_confirm, confirmKickName ?: stringResource(R.string.unknown)),
            confirmText = stringResource(R.string.together_kick),
            onConfirm = {
                val pid = confirmKickParticipantId
                confirmKickParticipantId = null
                if (pid != null) playerConnection?.service?.kickTogetherParticipant(pid)
            },
            onDismiss = { confirmKickParticipantId = null },
        )
    }

    if (confirmBanParticipantId != null) {
        ConfirmDialog(
            title = stringResource(R.string.together_ban),
            message = stringResource(R.string.together_ban_confirm, confirmBanName ?: stringResource(R.string.unknown)),
            confirmText = stringResource(R.string.together_ban),
            onConfirm = {
                val pid = confirmBanParticipantId
                confirmBanParticipantId = null
                if (pid != null) playerConnection?.service?.banTogetherParticipant(pid)
            },
            onDismiss = { confirmBanParticipantId = null },
        )
    }

    // ── Screen content ────────────────────────────────────────────────────────

    val joinedState = sessionState as? TogetherSessionState.Joined
    val roomPeople = when {
        isHostRole -> hostParticipants.filterNot { it.isPending && hostSettings?.requireHostApprovalToJoin == true }
        joinedState != null -> joinedState.roomState.participants.filterNot { it.isPending }
        else -> emptyList()
    }

    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal))
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom)),
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top)
            )
        )

        StatusCard(
            state = sessionState,
            isHostRole = isHostRole,
            busy = isJoining || isCreatingSessionLoading,
            listeners = roomPeople.count { !it.isHost },
            people = roomPeople.map { it.name },
            hostIndex = roomPeople.indexOfFirst { it.isHost }.takeIf { it >= 0 },
            onCopy = copy,
            onShare = share,
            onLeave = { playerConnection?.service?.leaveTogether() },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp, bottom = 12.dp),
        )

        // Starting, joining and leaving cross-fade between the "idle" and "in a room" layouts.
        AnimatedContent(
            targetState = idle,
            transitionSpec = {
                (fadeIn(tween(260, delayMillis = 60)) +
                        slideInVertically(spring(dampingRatio = 0.85f, stiffness = 380f)) { it / 14 }) togetherWith
                        (fadeOut(tween(140)) + slideOutVertically(tween(180)) { -it / 24 }) using
                        SizeTransform(clip = false)
            },
            label = "togetherBody",
        ) { showIdle ->
            Column {
                if (showIdle) {
                    ModeSwitch(
                        mode = mode,
                        onMode = { mode = it },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp),
                    )

                    HostSectionCard(
                        mode = mode,
                        displayName = displayName,
                        port = port,
                        allowAddTracks = allowAddTracks,
                        allowControlPlayback = allowControlPlayback,
                        requireApproval = requireApproval,
                        onShowNameDialog = { showNameDialog = true },
                        onShowPortDialog = { showPortDialog = true },
                        onAllowAddTracksChange = setAllowAddTracks,
                        onAllowControlPlaybackChange = setAllowControlPlayback,
                        onRequireApprovalChange = setRequireApproval,
                        isStartEnabled = !isCreatingSessionLoading && !isJoining && !isHosting &&
                                sessionState !is TogetherSessionState.Joined,
                        isLoading = isCreatingSessionLoading,
                        onStartSession = {
                            val settings = TogetherRoomSettings(
                                allowGuestsToAddTracks = allowAddTracks,
                                allowGuestsToControlPlayback = allowControlPlayback,
                                requireHostApprovalToJoin = requireApproval,
                            )
                            if (mode == TogetherMode.Online) {
                                playerConnection?.service?.startTogetherOnlineHost(
                                    displayName = displayName,
                                    settings = settings,
                                )
                            } else {
                                playerConnection?.service?.startTogetherHost(
                                    port = port,
                                    displayName = displayName,
                                    settings = settings,
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp),
                    )

                    AnimatedVisibility(
                        visible = mode == TogetherMode.Nearby,
                        enter = fadeIn(tween(220, delayMillis = 60)) +
                                expandVertically(spring(dampingRatio = 0.9f, stiffness = 420f)),
                        exit = fadeOut(tween(120)) +
                                shrinkVertically(spring(dampingRatio = 1f, stiffness = 500f)),
                    ) {
                        HotspotTipCard(
                            onOpen = { openHotspotSettings(context) },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 12.dp),
                        )
                    }

                    JoinSectionCard(
                        mode = mode,
                        joinInput = joinInput,
                        onJoinInputChange = { joinInput = it },
                        onlineCode = onlineCode,
                        onOnlineCodeChange = { onlineCode = it },
                        canJoin = if (mode == TogetherMode.Online) canJoinOnline else canJoinLan,
                        disableJoinUi = disableJoinUi,
                        isJoined = isJoinedAsAcceptedGuest,
                        isWaitingApproval = isWaitingApproval,
                        isJoining = isJoining,
                        onShowJoinDialog = { showJoinDialog = true },
                        onPasteFromClipboard = {
                            val text = clipboard.getText()?.text?.trim().orEmpty()
                            if (text.isNotBlank()) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (mode == TogetherMode.Online) {
                                    onlineCode = text.uppercase().filter { it.isLetterOrDigit() }.take(8)
                                } else {
                                    joinInput = text
                                    // Auto-join if the pasted link is valid
                                    if (TogetherLink.decode(text) != null) {
                                        setLastJoinLink(text)
                                        playerConnection?.service?.joinTogether(text, displayName)
                                    }
                                }
                            }
                        },
                        onJoin = {
                            if (mode == TogetherMode.Online) {
                                playerConnection?.service?.joinTogetherOnline(onlineCode, displayName)
                            } else {
                                val trimmed = joinInput.trim()
                                setLastJoinLink(trimmed)
                                playerConnection?.service?.joinTogether(trimmed, displayName)
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp),
                    )

                    AnimatedVisibility(
                        visible = mode == TogetherMode.Online,
                        enter = fadeIn(tween(220, delayMillis = 60)) +
                                expandVertically(spring(dampingRatio = 0.9f, stiffness = 420f)),
                        exit = fadeOut(tween(120)) +
                                shrinkVertically(spring(dampingRatio = 1f, stiffness = 500f)),
                    ) {
                        RelayCard(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 12.dp),
                        )
                    }
                } else {
                    // A room is running (or being joined): who is in, and what they may do.
                    if (isHostRole && hostParticipants.isNotEmpty()) {
                        OnlineParticipantsCard(
                            participants = hostParticipants,
                            hostApprovalEnabled = hostSettings?.requireHostApprovalToJoin == true,
                            onApprove = { pid, approved ->
                                playerConnection?.service?.approveTogetherParticipant(pid, approved)
                            },
                            onKick = { confirmKickParticipantId = it },
                            onBan = { confirmBanParticipantId = it },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 12.dp),
                        )
                    }

                    if (joinedState != null && joinedState.role is TogetherRole.Guest && !isWaitingApproval) {
                        GuestParticipantsCard(
                            participants = joinedState.roomState.participants.filterNot { it.isPending },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 12.dp),
                        )
                    }

                    if (isHostRole) {
                        RulesCard(
                            allowAddTracks = hostSettings?.allowGuestsToAddTracks ?: allowAddTracks,
                            allowControlPlayback = hostSettings?.allowGuestsToControlPlayback ?: allowControlPlayback,
                            requireApproval = hostSettings?.requireHostApprovalToJoin ?: requireApproval,
                            onAllowAddTracksChange = setAllowAddTracks,
                            onAllowControlPlaybackChange = setAllowControlPlayback,
                            onRequireApprovalChange = setRequireApproval,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 12.dp),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }

    TopAppBar(
        title = { Text(stringResource(R.string.music_together)) },
        navigationIcon = {
            AtIconButton(
                onClick = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(painterResource(R.drawable.arrow_back), null)
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

// ── Generic building blocks ───────────────────────────────────────────────────

@Composable
private fun TogetherCard(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(spring(stiffness = Spring.StiffnessMediumLow)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp), content = content)
    }
}

@Composable
private fun CardHeader(
    icon: Int,
    title: String,
    subtitle: String?,
    tint: Color,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        listOf(tint.copy(alpha = 0.18f), tint.copy(alpha = 0.08f))
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        trailing?.invoke()
    }
}

@Composable
private fun CardCaption(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(horizontal = 18.dp, vertical = 6.dp),
    )
}

// ── Mode switch (Nearby / Online) ─────────────────────────────────────────────

@Composable
private fun ModeSwitch(
    mode: TogetherMode,
    onMode: (TogetherMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    Column(modifier.fillMaxWidth()) {
        BoxWithConstraints(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(4.dp),
        ) {
            val half = maxWidth / 2
            val thumbOffset by animateDpAsState(
                if (mode == TogetherMode.Nearby) 0.dp else half,
                spring(dampingRatio = 0.78f, stiffness = 520f),
                label = "togetherModeThumb",
            )
            Box(
                Modifier
                    .offset(x = thumbOffset)
                    .width(half)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
            )
            Row(Modifier.fillMaxSize()) {
                val options = listOf(
                    Triple(TogetherMode.Nearby, R.drawable.wifi, stringResource(R.string.together_mode_nearby)),
                    Triple(TogetherMode.Online, R.drawable.language, stringResource(R.string.together_mode_online)),
                )
                for ((value, icon, label) in options) {
                    val selected = mode == value
                    Row(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                if (!selected) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onMode(value)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            painterResource(icon),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        Crossfade(targetState = mode, animationSpec = tween(220), label = "togetherModeText") { m ->
            Text(
                if (m == TogetherMode.Nearby) stringResource(R.string.together_mode_nearby_desc)
                else stringResource(R.string.together_mode_online_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
            )
        }
    }
}

// ── Host section ──────────────────────────────────────────────────────────────

@Composable
private fun HostSectionCard(
    mode: TogetherMode,
    displayName: String,
    port: Int,
    allowAddTracks: Boolean,
    allowControlPlayback: Boolean,
    requireApproval: Boolean,
    onShowNameDialog: () -> Unit,
    onShowPortDialog: () -> Unit,
    onAllowAddTracksChange: (Boolean) -> Unit,
    onAllowControlPlaybackChange: (Boolean) -> Unit,
    onRequireApprovalChange: (Boolean) -> Unit,
    isStartEnabled: Boolean,
    isLoading: Boolean,
    onStartSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val online = mode == TogetherMode.Online
    TogetherCard(modifier) {
        CardHeader(
            icon = R.drawable.fire,
            title = stringResource(R.string.together_host_section),
            subtitle = if (online) stringResource(R.string.together_mode_online) else stringResource(R.string.together_lan),
            tint = MaterialTheme.colorScheme.primary,
            trailing = {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painterResource(if (online) R.drawable.language else R.drawable.wifi),
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Text(
                            if (online) stringResource(R.string.together_mode_online) else "LAN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            },
        )

        SettingsItemRow(
            icon = R.drawable.person,
            title = stringResource(R.string.together_display_name),
            subtitle = displayName,
            onClick = onShowNameDialog,
        )
        if (!online) {
            Divider()
            SettingsItemRow(
                icon = R.drawable.link,
                title = stringResource(R.string.together_port),
                subtitle = port.toString(),
                onClick = onShowPortDialog,
            )
        }
        Divider()
        RuleToggles(
            allowAddTracks = allowAddTracks,
            allowControlPlayback = allowControlPlayback,
            requireApproval = requireApproval,
            onAllowAddTracksChange = onAllowAddTracksChange,
            onAllowControlPlaybackChange = onAllowControlPlaybackChange,
            onRequireApprovalChange = onRequireApprovalChange,
        )

        Spacer(Modifier.height(8.dp))

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.97f else 1f,
            animationSpec = spring(stiffness = Spring.StiffnessMedium),
            label = "scale",
        )

        Button(
            enabled = isStartEnabled,
            onClick = onStartSession,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 10.dp)
                .scale(scale),
            shape = RoundedCornerShape(18.dp),
            interactionSource = interactionSource,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(Modifier.width(10.dp))
                Text(stringResource(R.string.loading), fontWeight = FontWeight.SemiBold)
            } else {
                Icon(
                    painterResource(if (online) R.drawable.language else R.drawable.fire),
                    null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (online) stringResource(R.string.together_start_online_session)
                    else stringResource(R.string.start_session),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun RuleToggles(
    allowAddTracks: Boolean,
    allowControlPlayback: Boolean,
    requireApproval: Boolean,
    onAllowAddTracksChange: (Boolean) -> Unit,
    onAllowControlPlaybackChange: (Boolean) -> Unit,
    onRequireApprovalChange: (Boolean) -> Unit,
) {
    ToggleRow(
        icon = R.drawable.playlist_add,
        title = stringResource(R.string.together_allow_guests_add),
        checked = allowAddTracks,
        onCheckedChange = onAllowAddTracksChange,
    )
    Divider()
    ToggleRow(
        icon = R.drawable.play,
        title = stringResource(R.string.together_allow_guests_control),
        checked = allowControlPlayback,
        onCheckedChange = onAllowControlPlaybackChange,
    )
    Divider()
    ToggleRow(
        icon = R.drawable.lock,
        title = stringResource(R.string.together_require_approval),
        checked = requireApproval,
        onCheckedChange = onRequireApprovalChange,
    )
}

/** Guest permissions, editable by the host while the room is running. */
@Composable
private fun RulesCard(
    allowAddTracks: Boolean,
    allowControlPlayback: Boolean,
    requireApproval: Boolean,
    onAllowAddTracksChange: (Boolean) -> Unit,
    onAllowControlPlaybackChange: (Boolean) -> Unit,
    onRequireApprovalChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    TogetherCard(modifier) {
        CardHeader(
            icon = R.drawable.lock,
            title = stringResource(R.string.together_guests_title),
            subtitle = null,
            tint = MaterialTheme.colorScheme.secondary,
        )
        RuleToggles(
            allowAddTracks = allowAddTracks,
            allowControlPlayback = allowControlPlayback,
            requireApproval = requireApproval,
            onAllowAddTracksChange = onAllowAddTracksChange,
            onAllowControlPlaybackChange = onAllowControlPlaybackChange,
            onRequireApprovalChange = onRequireApprovalChange,
        )
        CardCaption(stringResource(R.string.together_rules_footer))
    }
}

// ── Hotspot tip ───────────────────────────────────────────────────────────────

@Composable
private fun HotspotTipCard(onOpen: () -> Unit, modifier: Modifier = Modifier) {
    TogetherCard(modifier) {
        CardHeader(
            icon = R.drawable.wifi,
            title = stringResource(R.string.together_hotspot_title),
            subtitle = null,
            tint = Color(0xFF34C759),
        )
        CardCaption(stringResource(R.string.together_hotspot_body))
        TextButton(
            onClick = onOpen,
            modifier = Modifier.padding(horizontal = 10.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(stringResource(R.string.together_hotspot_open), fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun openHotspotSettings(context: Context) {
    val attempts = listOf(
        Intent().setClassName("com.android.settings", "com.android.settings.TetherSettings"),
        Intent(Settings.ACTION_WIRELESS_SETTINGS),
        Intent(Settings.ACTION_SETTINGS),
    )
    for (intent in attempts) {
        if (runCatching { context.startActivity(intent) }.isSuccess) return
    }
}

// ── Join section (LAN link / Online code) ─────────────────────────────────────

@Composable
private fun JoinSectionCard(
    mode: TogetherMode,
    joinInput: String,
    onJoinInputChange: (String) -> Unit,
    onlineCode: String,
    onOnlineCodeChange: (String) -> Unit,
    canJoin: Boolean,
    disableJoinUi: Boolean,
    isJoined: Boolean,
    isWaitingApproval: Boolean,
    isJoining: Boolean,
    onShowJoinDialog: () -> Unit,
    onPasteFromClipboard: () -> Unit,
    onJoin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val online = mode == TogetherMode.Online
    val parsedLink = remember(joinInput) { TogetherLink.decode(joinInput.trim()) }
    val inputEnabled = !disableJoinUi && !isJoining && !isJoined && !isWaitingApproval

    TogetherCard(modifier) {
        CardHeader(
            icon = R.drawable.multi_user,
            title = stringResource(R.string.together_join_section),
            subtitle = stringResource(R.string.join_session),
            tint = MaterialTheme.colorScheme.tertiary,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (online) {
                // Short room code typed (or pasted) directly
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f),
                ) {
                    val codeStyle = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 6.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Box(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (onlineCode.isEmpty()) {
                            Text(
                                "ABC123",
                                style = codeStyle.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        BasicTextField(
                            value = onlineCode,
                            onValueChange = { raw ->
                                onOnlineCodeChange(raw.uppercase().filter { it.isLetterOrDigit() }.take(8))
                            },
                            enabled = inputEnabled,
                            singleLine = true,
                            textStyle = codeStyle,
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            } else {
                // Link display — click to edit
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = inputEnabled, onClick = onShowJoinDialog),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            painterResource(R.drawable.link),
                            contentDescription = null,
                            tint = if (canJoin) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = if (joinInput.isBlank()) stringResource(R.string.together_join_link_hint)
                            else joinInput.trim(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = if (joinInput.isNotBlank()) FontFamily.Monospace
                                else FontFamily.Default
                            ),
                            color = if (joinInput.isNotBlank()) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        if (joinInput.isNotBlank() && !disableJoinUi) {
                            Icon(
                                painterResource(R.drawable.close),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .clickable { onJoinInputChange("") },
                            )
                        }
                    }
                }
            }

            if (inputEnabled) {
                FilledTonalButton(
                    onClick = onPasteFromClipboard,
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                ) {
                    Icon(
                        painterResource(R.drawable.copy),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        stringResource(R.string.paste),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        if (online) {
            CardCaption(stringResource(R.string.together_join_online_hint))
        }

        // Parsed link preview (LAN only)
        AnimatedVisibility(
            visible = !online && parsedLink != null && !isJoined && !isWaitingApproval,
            enter = fadeIn(tween(200)) + expandVertically(tween(250)),
            exit = fadeOut(tween(150)) + shrinkVertically(tween(200)),
        ) {
            if (parsedLink != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .padding(top = 4.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        ) {
                            Text(
                                text = "${parsedLink.host}:${parsedLink.port}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = FontFamily.Monospace
                                ),
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            )
                        }
                        Text(
                            text = "Session: ${parsedLink.sessionId.take(8)}…",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.weight(1f))
                        Icon(
                            painterResource(R.drawable.check),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.97f else 1f,
            animationSpec = spring(stiffness = Spring.StiffnessMedium),
            label = "joinBtnScale",
        )

        FilledTonalButton(
            enabled = canJoin && inputEnabled,
            onClick = onJoin,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 10.dp)
                .scale(scale),
            shape = RoundedCornerShape(18.dp),
            interactionSource = interactionSource,
        ) {
            when {
                isJoining -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.connecting), fontWeight = FontWeight.SemiBold)
                }
                isWaitingApproval -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.together_waiting_approval), fontWeight = FontWeight.SemiBold)
                }
                isJoined -> {
                    Icon(painterResource(R.drawable.check), null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.joined), fontWeight = FontWeight.SemiBold)
                }
                else -> {
                    Icon(painterResource(R.drawable.join), null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.join), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Relay server (Online mode) ────────────────────────────────────────────────

/** Which relay Online uses, and whether it answers — so a dead server is seen here, not guessed at. */
@Composable
private fun RelayCard(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val (custom, setCustom) = rememberPreference(TogetherRelayUrlKey, defaultValue = "")
    var editing by rememberSaveable { mutableStateOf(false) }
    var result by rememberSaveable { mutableStateOf<String?>(null) }
    var healthy by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var testing by rememberSaveable { mutableStateOf(false) }

    if (editing) {
        TextFieldDialog(
            title = { Text(stringResource(R.string.together_relay_title)) },
            placeholder = { Text(stringResource(R.string.together_relay_placeholder)) },
            initialTextFieldValue = TextFieldValue(custom),
            isInputValid = { it.isBlank() || TogetherOnlineEndpoint.normalizedRelayUrlOrNull(it) != null },
            onDone = {
                setCustom(it.trim())
                result = null
                healthy = null
            },
            onDismiss = { editing = false },
        )
    }

    TogetherCard(modifier) {
        CardHeader(
            icon = R.drawable.language,
            title = stringResource(R.string.together_relay_title),
            subtitle = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        SettingsItemRow(
            icon = R.drawable.language,
            title = if (custom.isBlank()) stringResource(R.string.together_relay_shared) else custom,
            subtitle = result ?: stringResource(R.string.together_relay_tap),
            tint = when (healthy) {
                true -> Color(0xFF34C759)
                false -> MaterialTheme.colorScheme.error
                null -> Color.Unspecified
            },
            onClick = { editing = true },
        )
        Divider()
        ActionRow(
            icon = R.drawable.sync,
            text = stringResource(R.string.together_relay_test),
            loading = testing,
            onClick = {
                testing = true
                result = null
                scope.launch {
                    val (ok, message) = withContext(Dispatchers.IO) { testRelay(context, custom) }
                    healthy = ok
                    result = message
                    testing = false
                }
            },
        )
        CardCaption(stringResource(R.string.together_relay_footer))
    }
}

/** Asks the relay's own health address, and says in words what came back. */
private suspend fun testRelay(context: Context, custom: String): Pair<Boolean, String> {
    val base = TogetherOnlineEndpoint.normalizedRelayUrlOrNull(custom)
        ?: TogetherOnlineEndpoint.baseUrlOrNull(context.dataStore)
        ?: return false to context.getString(R.string.together_relay_none)
    var connection: HttpURLConnection? = null
    return try {
        connection = URL(base.trimEnd('/') + "/health").openConnection() as HttpURLConnection
        connection.connectTimeout = 8_000
        connection.readTimeout = 8_000
        val code = connection.responseCode
        if (code in 200..299) true to context.getString(R.string.together_relay_working)
        else false to context.getString(R.string.together_relay_http_error, code)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false to context.getString(R.string.together_relay_unreachable, e.javaClass.simpleName)
    } finally {
        connection?.disconnect()
    }
}

// ── Status Card ───────────────────────────────────────────────────────────────

@Composable
private fun StatusCard(
    state: TogetherSessionState,
    isHostRole: Boolean,
    busy: Boolean,
    listeners: Int,
    people: List<String>,
    hostIndex: Int?,
    onCopy: (String) -> Unit,
    onShare: (String) -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isActive = state !is TogetherSessionState.Idle
    val isError = state is TogetherSessionState.Error
    val isWaitingApproval = run {
        val joined = state as? TogetherSessionState.Joined ?: return@run false
        joined.role is TogetherRole.Guest &&
                joined.roomState.participants
                    .firstOrNull { it.id == joined.selfParticipantId }?.isPending == true
    }
    val statusColor = when {
        isError -> MaterialTheme.colorScheme.error
        isActive -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val pulse = if (busy) {
        val transition = rememberInfiniteTransition(label = "togetherPulse")
        transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.14f,
            animationSpec = infiniteRepeatable(
                tween(800, easing = FastOutSlowInEasing),
                RepeatMode.Reverse,
            ),
            label = "togetherPulseScale",
        ).value
    } else 1f

    val subtitle: String? = when (state) {
        is TogetherSessionState.Hosting, is TogetherSessionState.HostingOnline -> when {
            listeners <= 0 -> stringResource(R.string.together_waiting_for_guests)
            listeners == 1 -> stringResource(R.string.together_one_listener)
            else -> stringResource(R.string.together_n_listeners, listeners)
        }
        else -> null
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(spring(stiffness = Spring.StiffnessMediumLow)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        if (isActive && !isError)
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                MaterialTheme.colorScheme.surfaceContainerLow,
                            )
                        else if (isError)
                            listOf(
                                MaterialTheme.colorScheme.error.copy(alpha = 0.14f),
                                MaterialTheme.colorScheme.surfaceContainerLow,
                            )
                        else
                            listOf(
                                MaterialTheme.colorScheme.surfaceContainerLow,
                                MaterialTheme.colorScheme.surfaceContainerLow,
                            )
                    )
                )
                .padding(18.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .graphicsLayer {
                                scaleX = pulse
                                scaleY = pulse
                            }
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        statusColor.copy(alpha = 0.18f),
                                        statusColor.copy(alpha = 0.08f),
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painterResource(if (isError) R.drawable.error else R.drawable.fire),
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                stringResource(R.string.together_status),
                                style = MaterialTheme.typography.labelLarge,
                                color = statusColor,
                            )
                            if (isActive && !isError) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                )
                            }
                        }
                        Text(
                            text = when (state) {
                                TogetherSessionState.Idle -> stringResource(R.string.together_idle)
                                is TogetherSessionState.Hosting -> stringResource(R.string.together_hosting)
                                is TogetherSessionState.HostingOnline -> stringResource(R.string.together_hosting)
                                is TogetherSessionState.Joining -> stringResource(R.string.together_joining)
                                is TogetherSessionState.JoiningOnline -> stringResource(R.string.together_joining)
                                is TogetherSessionState.Joined ->
                                    if (isWaitingApproval) stringResource(R.string.together_waiting_approval)
                                    else stringResource(R.string.together_connected)
                                is TogetherSessionState.Error -> stringResource(R.string.together_error_state)
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (subtitle != null) {
                            Text(
                                subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    if (isActive) {
                        FilledTonalButton(onClick = onLeave, shape = RoundedCornerShape(14.dp)) {
                            if (!isError) {
                                Icon(painterResource(R.drawable.leave), null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                            }
                            Text(
                                when {
                                    isError -> stringResource(R.string.dismiss)
                                    isHostRole -> stringResource(R.string.together_end_session)
                                    else -> stringResource(R.string.leave)
                                },
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }

                if (people.isNotEmpty() && !isError) {
                    AvatarStack(names = people, hostIndex = hostIndex)
                }

                when (state) {
                    is TogetherSessionState.Hosting -> {
                        LanSessionLinkCard(
                            link = state.joinLink,
                            localAddressHint = state.localAddressHint,
                            port = state.port,
                            onCopy = { onCopy(state.joinLink) },
                            onShare = { onShare(state.joinLink) },
                        )
                    }
                    is TogetherSessionState.HostingOnline -> {
                        val shareText = stringResource(R.string.together_share_code_text, state.code)
                        OnlineCodeCard(
                            code = state.code,
                            onCopy = { onCopy(state.code) },
                            onShare = { onShare(shareText) },
                        )
                    }
                    is TogetherSessionState.Error -> {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(14.dp),
                            )
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

// ── Invitation: LAN (QR + link) ───────────────────────────────────────────────

@Composable
private fun LanSessionLinkCard(
    link: String,
    localAddressHint: String?,
    port: Int,
    onCopy: () -> Unit,
    onShare: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TogetherQr(link)
            Text(
                stringResource(R.string.together_scan_qr),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )

            if (localAddressHint != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painterResource(R.drawable.wifi),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(13.dp),
                            )
                            Text(
                                text = "$localAddressHint:$port",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = FontFamily.Monospace
                                ),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                    Text(
                        stringResource(R.string.session_link),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = link,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 2,
                    )
                }
            }

            CopyShareButtons(
                copyLabel = stringResource(R.string.copy_link),
                onCopy = onCopy,
                onShare = onShare,
            )
        }
    }
}

// ── Invitation: Online (room code) ────────────────────────────────────────────

@Composable
private fun OnlineCodeCard(
    code: String,
    onCopy: () -> Unit,
    onShare: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.together_room_code),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = code.chunked(3).joinToString(" "),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 6.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onCopy)
                    .padding(vertical = 12.dp),
            )
            Text(
                stringResource(R.string.together_room_code_caption),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            CopyShareButtons(
                copyLabel = stringResource(R.string.together_copy_code),
                onCopy = onCopy,
                onShare = onShare,
            )
        }
    }
}

@Composable
private fun CopyShareButtons(
    copyLabel: String,
    onCopy: () -> Unit,
    onShare: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Button(
            onClick = onCopy,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.weight(1f),
        ) {
            Icon(painterResource(R.drawable.copy), null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(copyLabel, fontWeight = FontWeight.SemiBold)
        }
        FilledTonalButton(
            onClick = onShare,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.weight(1f),
        ) {
            Icon(painterResource(R.drawable.share), null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(stringResource(R.string.share), fontWeight = FontWeight.SemiBold)
        }
    }
}

/** The invite as a QR code (always dark on white, so every camera reads it). */
@Composable
private fun TogetherQr(link: String, modifier: Modifier = Modifier) {
    val matrix = remember(link) {
        runCatching {
            QRCodeWriter().encode(
                link,
                BarcodeFormat.QR_CODE,
                0,
                0,
                mapOf(
                    EncodeHintType.MARGIN to 1,
                    EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
                ),
            )
        }.getOrNull()
    } ?: return

    Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = modifier) {
        Canvas(
            Modifier
                .padding(12.dp)
                .size(184.dp)
        ) {
            val cells = matrix.width
            val cell = size.width / cells
            for (y in 0 until cells) {
                for (x in 0 until cells) {
                    if (matrix.get(x, y)) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(x * cell, y * cell),
                            size = Size(cell + 0.5f, cell + 0.5f),
                        )
                    }
                }
            }
        }
    }
}

// ── Participants ──────────────────────────────────────────────────────────────

@Composable
private fun OnlineParticipantsCard(
    participants: List<TogetherParticipant>,
    hostApprovalEnabled: Boolean,
    onApprove: (participantId: String, approved: Boolean) -> Unit,
    onKick: (participantId: String) -> Unit,
    onBan: (participantId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val waiting = participants.filter { it.isPending && hostApprovalEnabled }
    val inRoom = participants.filterNot { it.isPending && hostApprovalEnabled }

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (waiting.isNotEmpty()) {
            TogetherCard {
                CardHeader(
                    icon = R.drawable.lock,
                    title = stringResource(R.string.together_waiting_to_join),
                    subtitle = null,
                    tint = MaterialTheme.colorScheme.secondary,
                )
                waiting.forEachIndexed { index, person ->
                    key(person.id) {
                        if (index != 0) Divider()
                        ParticipantRow(
                            name = person.name,
                            subtitle = stringResource(R.string.together_pending_approval),
                            isHost = false,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                SmallRoundAction(
                                    R.drawable.close,
                                    stringResource(R.string.together_decline),
                                    MaterialTheme.colorScheme.error,
                                ) { onApprove(person.id, false) }
                                SmallRoundAction(
                                    R.drawable.check,
                                    stringResource(R.string.together_let_in),
                                    Color(0xFF34C759),
                                ) { onApprove(person.id, true) }
                            }
                        }
                    }
                }
            }
        }

        TogetherCard {
            CardHeader(
                icon = R.drawable.list,
                title = stringResource(R.string.together_participants),
                subtitle = stringResource(R.string.together_connected_count, inRoom.size),
                tint = MaterialTheme.colorScheme.tertiary,
            )
            inRoom.forEachIndexed { index, person ->
                key(person.id) {
                    if (index != 0) Divider()
                    ParticipantRow(
                        name = person.name,
                        subtitle = if (person.isHost) stringResource(R.string.together_role_host)
                        else stringResource(R.string.together_role_guest),
                        isHost = person.isHost,
                    ) {
                        if (!person.isHost) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                SmallRoundAction(
                                    R.drawable.kick,
                                    stringResource(R.string.together_kick),
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                ) { onKick(person.id) }
                                SmallRoundAction(
                                    R.drawable.block,
                                    stringResource(R.string.together_ban),
                                    MaterialTheme.colorScheme.error,
                                ) { onBan(person.id) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuestParticipantsCard(
    participants: List<TogetherParticipant>,
    modifier: Modifier = Modifier,
) {
    TogetherCard(modifier) {
        CardHeader(
            icon = R.drawable.list,
            title = stringResource(R.string.participants),
            subtitle = stringResource(R.string.together_connected_count, participants.size),
            tint = MaterialTheme.colorScheme.tertiary,
        )
        participants.forEachIndexed { index, person ->
            key(person.id) {
                if (index != 0) Divider()
                ParticipantRow(
                    name = person.name,
                    subtitle = if (person.isHost) stringResource(R.string.together_role_host)
                    else stringResource(R.string.together_role_guest),
                    isHost = person.isHost,
                )
            }
        }
    }
}

@Composable
private fun ParticipantRow(
    name: String,
    subtitle: String,
    isHost: Boolean,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        TogetherAvatar(name = name, host = isHost)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        trailing?.invoke()
    }
}

@Composable
private fun SmallRoundAction(icon: Int, description: String, tint: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.14f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(painterResource(icon), contentDescription = description, tint = tint, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun TogetherAvatar(
    name: String,
    host: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    val palette = listOf(
        Color(0xFF5B8DEF), Color(0xFFEF6C5B), Color(0xFF3CB371),
        Color(0xFFB46CEF), Color(0xFFE8A33D), Color(0xFF2FB5C7),
    )
    val base = if (host) MaterialTheme.colorScheme.primary
    else palette[(name.hashCode() and 0x7fffffff) % palette.size]
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(base.copy(alpha = if (host) 0.22f else 0.16f)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name.trim().firstOrNull()?.uppercase() ?: "?",
            fontSize = (size.value * 0.4f).sp,
            fontWeight = FontWeight.Bold,
            color = base,
        )
    }
}

@Composable
private fun AvatarStack(names: List<String>, hostIndex: Int?, max: Int = 5) {
    val ring = MaterialTheme.colorScheme.surfaceContainerLow
    Row(horizontalArrangement = Arrangement.spacedBy((-10).dp), verticalAlignment = Alignment.CenterVertically) {
        names.take(max).forEachIndexed { index, name ->
            TogetherAvatar(
                name = name,
                host = index == hostIndex,
                size = 36.dp,
                modifier = Modifier.border(2.dp, ring, CircleShape),
            )
        }
        if (names.size > max) {
            Box(
                Modifier
                    .size(36.dp)
                    .border(2.dp, ring, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "+${names.size - max}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ── Dialogs ───────────────────────────────────────────────────────────────────

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(title) },
        text = {
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            ) { Text(confirmText, fontWeight = FontWeight.SemiBold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, shape = RoundedCornerShape(16.dp)) {
                Text(stringResource(R.string.dismiss))
            }
        },
    )
}

@Composable
private fun WelcomeDialog(
    onGotIt: (dontShowAgain: Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var dontShowAgain by rememberSaveable { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painterResource(R.drawable.fire),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Text(
                    stringResource(R.string.together_welcome_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    stringResource(R.string.together_welcome_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        InstructionRow(
                            R.drawable.fire, MaterialTheme.colorScheme.primary,
                            stringResource(R.string.together_welcome_host_title),
                            stringResource(R.string.together_welcome_host_body)
                        )
                        InstructionRow(
                            R.drawable.link, MaterialTheme.colorScheme.tertiary,
                            stringResource(R.string.together_welcome_join_title),
                            stringResource(R.string.together_welcome_join_body)
                        )
                        InstructionRow(
                            R.drawable.lock, MaterialTheme.colorScheme.secondary,
                            stringResource(R.string.together_welcome_permissions_title),
                            stringResource(R.string.together_welcome_permissions_body)
                        )
                    }
                }
                CheckboxRow(
                    checked = dontShowAgain,
                    onCheckedChange = { dontShowAgain = it },
                    label = stringResource(R.string.together_dont_show_again),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onGotIt(dontShowAgain) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                Icon(painterResource(R.drawable.check), null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.got_it), fontWeight = FontWeight.SemiBold)
            }
        },
    )
}

@Composable
private fun InstructionRow(
    icon: Int,
    accentColor: Color,
    title: String,
    body: String,
) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accentColor.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(icon), null, tint = accentColor, modifier = Modifier.size(20.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ── Shared row primitives ─────────────────────────────────────────────────────

@Composable
private fun Divider() = HorizontalDivider(
    modifier = Modifier.padding(start = 76.dp, end = 18.dp),
    thickness = 0.5.dp,
    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
)

@Composable
private fun SettingsItemRow(
    icon: Int,
    title: String,
    subtitle: String,
    subtitleMaxLines: Int = 1,
    tint: Color = Color.Unspecified,
    onClick: (() -> Unit)? = null,
) {
    val accent = if (tint.isSpecified) tint else MaterialTheme.colorScheme.primary
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f,
        animationSpec = tween(100),
        label = "itemAlpha",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ) else Modifier
            )
            .graphicsLayer { this.alpha = alpha }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(icon), null, tint = accent.copy(alpha = 0.9f), modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = subtitleMaxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (onClick != null) {
            Spacer(Modifier.width(8.dp))
            Icon(
                painterResource(R.drawable.navigate_next),
                null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/** A tappable row whose label is the accent colour; shows a spinner while [loading]. */
@Composable
private fun ActionRow(
    icon: Int,
    text: String,
    onClick: () -> Unit,
    loading: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = !loading, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(icon), null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(
            text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ToggleRow(
    icon: Int,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(icon), null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f), modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            thumbContent = {
                Icon(
                    painterResource(if (checked) R.drawable.check else R.drawable.close),
                    null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            },
        )
    }
}

@Composable
private fun CheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}