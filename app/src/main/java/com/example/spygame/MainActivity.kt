package com.example.spygame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.spygame.model.Phase
import com.example.spygame.ui.components.SettingsDialog
import com.example.spygame.ui.screens.*
import com.example.spygame.ui.theme.SpyBg
import com.example.spygame.ui.theme.SpyGameTheme
import com.example.spygame.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SpyGameTheme {
                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing,
                    containerColor = SpyBg
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        SpyGameApp(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun SpyGameApp(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Back handling for fluid navigation
    BackHandler(enabled = uiState.phase != Phase.MENU) {
        when (uiState.phase) {
            Phase.RULES, Phase.PACKS -> viewModel.returnToMenu()
            Phase.LOBBY -> viewModel.returnToMenu()
            Phase.ROLE_REVEAL, Phase.PLAYING, Phase.VOTING -> viewModel.returnToLobby()
            Phase.RESULTS -> viewModel.returnToLobby()
            Phase.MENU -> {}
        }
    }

    when (uiState.phase) {
        Phase.MENU -> {
            MenuScreen(
                playerCount = uiState.playerCountInput,
                spyCount = uiState.spyCountInput,
                isSoloMode = uiState.isSoloPractice,
                roundTimeSec = uiState.config.roundTimeSec,
                onPlayerCountChange = { viewModel.setPlayerCount(it) },
                onSpyCountChange = { viewModel.setSpyCount(it) },
                onGameModeChange = { viewModel.setGameMode(it) },
                onRoundTimeChange = { viewModel.updateConfig(uiState.config.copy(roundTimeSec = it)) },
                onStartGameDirect = { viewModel.startDirectRound() },
                onOpenLobby = { viewModel.openLobbyFromMenu() },
                onOpenRules = { viewModel.showRules() },
                onOpenPacks = { viewModel.showPacks() },
                onOpenSettings = { viewModel.openSettingsDialog() }
            )

            if (uiState.showSettingsDialog) {
                SettingsDialog(
                    currentConfig = uiState.config,
                    onDismiss = { viewModel.closeSettingsDialog() },
                    onSaveConfig = { viewModel.updateConfig(it) }
                )
            }
        }
        Phase.LOBBY -> {
            LobbyScreen(
                players = uiState.players,
                config = uiState.config,
                isSoloMode = uiState.isSoloPractice,
                onBackToMenu = { viewModel.returnToMenu() },
                onAddPlayer = { name, avatar, isBot -> viewModel.addPlayer(name, avatar, isBot) },
                onRemovePlayer = { id -> viewModel.removePlayer(id) },
                onOpenSettings = { viewModel.openSettingsDialog() },
                onStartRound = { viewModel.startRound() }
            )

            if (uiState.showSettingsDialog) {
                SettingsDialog(
                    currentConfig = uiState.config,
                    onDismiss = { viewModel.closeSettingsDialog() },
                    onSaveConfig = { viewModel.updateConfig(it) }
                )
            }
        }
        Phase.ROLE_REVEAL -> {
            val humanPlayers = uiState.players.filter { !it.isBot }
            RoleRevealScreen(
                players = humanPlayers,
                currentIndex = uiState.currentRoleRevealIndex,
                currentLocation = uiState.currentLocation,
                spyCount = uiState.spyCount,
                isRevealed = uiState.isDossierRevealed,
                onToggleReveal = { viewModel.toggleDossierReveal() },
                onNextPlayer = { viewModel.nextPlayerReveal() }
            )
        }
        Phase.PLAYING -> {
            GameScreen(
                players = uiState.players,
                config = uiState.config,
                currentAskerId = uiState.currentAskerId,
                currentTargetId = uiState.currentTargetId,
                currentQuestionText = uiState.currentQuestionText,
                turnStage = uiState.turnStage,
                roundSecondsRemaining = uiState.roundSecondsRemaining,
                turnSecondsRemaining = uiState.turnSecondsRemaining,
                log = uiState.log,
                notices = uiState.notices,
                canStartVoting = true,
                categories = viewModel.getAllCategories(),
                showSpyGuessDialog = uiState.showSpyGuessDialog,
                showQuestionDialog = uiState.showQuestionDialog,
                showAnswerDialog = uiState.showAnswerDialog,
                showAssociationDialog = uiState.showAssociationDialog,
                onOpenQuestionDialog = { viewModel.openQuestionDialog() },
                onCloseQuestionDialog = { viewModel.closeQuestionDialog() },
                onSendQuestion = { toId, text -> viewModel.askQuestion(toId, text) },
                onOpenAnswerDialog = { viewModel.openAnswerDialog() },
                onCloseAnswerDialog = { viewModel.closeAnswerDialog() },
                onSendAnswer = { text -> viewModel.answerQuestion(text) },
                onOpenAssociationDialog = { viewModel.openAssociationDialog() },
                onCloseAssociationDialog = { viewModel.closeAssociationDialog() },
                onSendAssociation = { word -> viewModel.sendAssociation(word) },
                onOpenSpyGuessDialog = { viewModel.openSpyGuessDialog() },
                onCloseSpyGuessDialog = { viewModel.closeSpyGuessDialog() },
                onConfirmSpyGuess = { loc -> viewModel.spyGuess(loc) },
                onStartVoting = { viewModel.startVoting() },
                currentLocation = uiState.currentLocation,
                isSoloMode = uiState.isSoloPractice
            )
        }
        Phase.VOTING -> {
            VotingScreen(
                players = uiState.players,
                config = uiState.config,
                votes = uiState.votes,
                voteSecondsRemaining = uiState.voteSecondsRemaining,
                onCastVote = { voterId, targetId -> viewModel.castVote(voterId, targetId) }
            )
        }
        Phase.RESULTS -> {
            ResultsScreen(
                winningTeam = uiState.winningTeam,
                endReason = uiState.endReason,
                winningDetails = uiState.winningDetails,
                secretLocation = uiState.currentLocation,
                players = uiState.players,
                onNextRound = { viewModel.startRound() },
                onReturnToLobby = { viewModel.returnToLobby() }
            )
        }
        Phase.RULES -> {
            RulesScreen(
                onBack = { viewModel.returnToMenu() }
            )
        }
        Phase.PACKS -> {
            PacksScreen(
                categories = viewModel.getAllCategories(),
                enabledCategoryIds = uiState.config.enabledCategoryIds,
                onToggleCategory = { viewModel.toggleCategory(it) },
                onAddCustomLocation = { catId, name, synonyms -> viewModel.addCustomLocation(catId, name, synonyms) },
                onBack = { viewModel.returnToMenu() }
            )
        }
    }
}
