package com.example.spygame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spygame.data.LocationRepository
import com.example.spygame.engine.SpyGameEngine
import com.example.spygame.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class GameUiState(
    val phase: Phase = Phase.MENU,
    val roomCode: String = "SPY7",
    val isPassAndPlay: Boolean = true,
    val isSoloPractice: Boolean = false,
    val players: List<Player> = emptyList(),
    val config: GameConfig = GameConfig(),
    val currentRoleRevealIndex: Int = 0,
    val isDossierRevealed: Boolean = false,
    val currentLocation: LocationItem? = null,
    val spyCount: Int = 1,
    val currentAskerId: String? = null,
    val currentTargetId: String? = null,
    val currentQuestionText: String? = null,
    val turnStage: TurnStage = TurnStage.ASKING,
    val roundNumber: Int = 1,
    val roundSecondsRemaining: Int = 300,
    val turnSecondsRemaining: Int = 60,
    val voteSecondsRemaining: Int = 45,
    val log: List<GameLogEntry> = emptyList(),
    val notices: List<Notice> = emptyList(),
    val votes: Map<String, String?> = emptyMap(),
    val lastVotingResult: VotingResult? = null,
    val winningTeam: Team? = null,
    val endReason: EndReason? = null,
    val winningDetails: String? = null,
    val showSpyGuessDialog: Boolean = false,
    val showQuestionDialog: Boolean = false,
    val showAnswerDialog: Boolean = false,
    val showAssociationDialog: Boolean = false,
    val showSettingsDialog: Boolean = false
)

class GameViewModel : ViewModel() {
    private val locationRepository = LocationRepository()
    private val engine = SpyGameEngine(locationRepository)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var botJob: Job? = null

    init {
        engine.initDefaultPlayers()
        syncWithEngine()
    }

    private fun syncWithEngine() {
        _uiState.update { current ->
            current.copy(
                phase = engine.phase,
                players = engine.players.toList(),
                config = engine.config,
                currentLocation = engine.currentLocation,
                spyCount = engine.spyCount,
                currentAskerId = engine.currentAskerId,
                currentTargetId = engine.currentTargetId,
                currentQuestionText = engine.currentQuestionText,
                turnStage = engine.turnStage,
                roundNumber = engine.roundNumber,
                roundSecondsRemaining = engine.roundTimeRemainingSec,
                turnSecondsRemaining = engine.turnTimeRemainingSec,
                voteSecondsRemaining = engine.voteTimeRemainingSec,
                log = engine.log.toList(),
                notices = engine.notices.toList(),
                votes = engine.votes.toMap(),
                lastVotingResult = engine.lastVotingResult,
                winningTeam = engine.winningTeam,
                endReason = engine.endReason,
                winningDetails = engine.winningDetails
            )
        }
    }

    fun startPassAndPlay() {
        _uiState.update { it.copy(isPassAndPlay = true, isSoloPractice = false) }
        engine.phase = Phase.LOBBY
        syncWithEngine()
    }

    fun startSoloPractice() {
        _uiState.update { it.copy(isPassAndPlay = false, isSoloPractice = true) }
        // Ensure 1 human + 3 bots
        engine.players.clear()
        engine.players.add(Player(id = UUID.randomUUID().toString(), name = "Вы", avatar = "🦊", isHost = true, ready = true))
        engine.players.add(Player(id = UUID.randomUUID().toString(), name = "Агент 007", avatar = "🕶️", isBot = true, ready = true))
        engine.players.add(Player(id = UUID.randomUUID().toString(), name = "Шерлок", avatar = "🔍", isBot = true, ready = true))
        engine.players.add(Player(id = UUID.randomUUID().toString(), name = "Панда", avatar = "🐼", isBot = true, ready = true))
        engine.phase = Phase.LOBBY
        syncWithEngine()
    }

    fun showRules() {
        engine.phase = Phase.RULES
        syncWithEngine()
    }

    fun showPacks() {
        engine.phase = Phase.PACKS
        syncWithEngine()
    }

    fun returnToMenu() {
        stopTimers()
        engine.phase = Phase.MENU
        syncWithEngine()
    }

    fun returnToLobby() {
        stopTimers()
        engine.phase = Phase.LOBBY
        _uiState.update { it.copy(currentRoleRevealIndex = 0, isDossierRevealed = false) }
        syncWithEngine()
    }

    fun addPlayer(name: String, avatar: String, isBot: Boolean = false) {
        engine.addPlayer(name, avatar, isBot)
        syncWithEngine()
    }

    fun removePlayer(playerId: String) {
        engine.removePlayer(playerId)
        syncWithEngine()
    }

    fun updateConfig(newConfig: GameConfig) {
        engine.config = newConfig
        syncWithEngine()
    }

    fun toggleCategory(categoryId: String) {
        val currentEnabled = engine.config.enabledCategoryIds.toMutableSet()
        if (currentEnabled.isEmpty()) {
            // All were enabled, disable this one
            val allIds = locationRepository.getAllCategories().map { it.id }.toSet()
            val newSet = allIds - categoryId
            engine.config = engine.config.copy(enabledCategoryIds = newSet)
        } else {
            if (categoryId in currentEnabled) {
                currentEnabled.remove(categoryId)
            } else {
                currentEnabled.add(categoryId)
            }
            engine.config = engine.config.copy(enabledCategoryIds = currentEnabled)
        }
        syncWithEngine()
    }

    fun startRound() {
        val ok = engine.startNewRound()
        if (ok) {
            _uiState.update { it.copy(currentRoleRevealIndex = 0, isDossierRevealed = false) }
            syncWithEngine()
        }
    }

    fun toggleDossierReveal() {
        _uiState.update { it.copy(isDossierRevealed = !it.isDossierRevealed) }
    }

    fun nextPlayerReveal() {
        val nextIdx = _uiState.value.currentRoleRevealIndex + 1
        if (nextIdx < engine.players.size) {
            _uiState.update { it.copy(currentRoleRevealIndex = nextIdx, isDossierRevealed = false) }
        } else {
            // All players viewed role, begin game!
            engine.beginPlaying()
            _uiState.update { it.copy(isDossierRevealed = false) }
            syncWithEngine()
            startTimers()
            triggerBotActionIfNeeded()
        }
    }

    fun askQuestion(toPlayerId: String, text: String) {
        val askerId = engine.currentAskerId ?: return
        engine.askQuestion(askerId, toPlayerId, text)
        _uiState.update { it.copy(showQuestionDialog = false) }
        syncWithEngine()
        triggerBotActionIfNeeded()
    }

    fun answerQuestion(text: String) {
        val targetId = engine.currentTargetId ?: return
        engine.answerQuestion(targetId, text)
        _uiState.update { it.copy(showAnswerDialog = false) }
        syncWithEngine()
        triggerBotActionIfNeeded()
    }

    fun sendAssociation(word: String) {
        val askerId = engine.currentAskerId ?: return
        engine.associate(askerId, word)
        _uiState.update { it.copy(showAssociationDialog = false) }
        syncWithEngine()
        triggerBotActionIfNeeded()
    }

    fun startVoting() {
        engine.startVoting()
        syncWithEngine()
        triggerBotVotesIfNeeded()
    }

    fun castVote(voterId: String, targetPlayerId: String?) {
        engine.castVote(voterId, targetPlayerId)
        syncWithEngine()
        if (engine.phase == Phase.RESULTS) {
            stopTimers()
        } else if (engine.phase == Phase.PLAYING) {
            // Round resumed after tie/inconclusive
            triggerBotActionIfNeeded()
        }
    }

    fun spyGuess(guessedLocation: String) {
        val activeSpies = engine.players.filter { it.isActive && it.role == Role.SPY }
        val guesser = activeSpies.firstOrNull() ?: engine.players.first()
        engine.spyGuess(guesser.id, guessedLocation)
        _uiState.update { it.copy(showSpyGuessDialog = false) }
        syncWithEngine()
        if (engine.phase == Phase.RESULTS) {
            stopTimers()
        }
    }

    fun openSpyGuessDialog() {
        _uiState.update { it.copy(showSpyGuessDialog = true) }
    }

    fun closeSpyGuessDialog() {
        _uiState.update { it.copy(showSpyGuessDialog = false) }
    }

    fun openQuestionDialog() {
        _uiState.update { it.copy(showQuestionDialog = true) }
    }

    fun closeQuestionDialog() {
        _uiState.update { it.copy(showQuestionDialog = false) }
    }

    fun openAnswerDialog() {
        _uiState.update { it.copy(showAnswerDialog = true) }
    }

    fun closeAnswerDialog() {
        _uiState.update { it.copy(showAnswerDialog = false) }
    }

    fun openAssociationDialog() {
        _uiState.update { it.copy(showAssociationDialog = true) }
    }

    fun closeAssociationDialog() {
        _uiState.update { it.copy(showAssociationDialog = false) }
    }

    fun openSettingsDialog() {
        _uiState.update { it.copy(showSettingsDialog = true) }
    }

    fun closeSettingsDialog() {
        _uiState.update { it.copy(showSettingsDialog = false) }
    }

    fun addCustomLocation(categoryId: String, name: String, synonyms: List<String>) {
        locationRepository.addCustomLocation(categoryId, name, synonyms)
        syncWithEngine()
    }

    fun getAllCategories(): List<LocationCategory> = locationRepository.getAllCategories()

    private fun startTimers() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (engine.phase == Phase.PLAYING) {
                    if (engine.config.roundTimeSec > 0 && engine.roundTimeRemainingSec > 0) {
                        engine.roundTimeRemainingSec--
                        if (engine.roundTimeRemainingSec <= 0) {
                            engine.onTimeExpired()
                            syncWithEngine()
                            if (engine.phase == Phase.VOTING) {
                                triggerBotVotesIfNeeded()
                            }
                        }
                    }
                    if (engine.config.turnTimeSec > 0 && engine.turnTimeRemainingSec > 0) {
                        engine.turnTimeRemainingSec--
                        if (engine.turnTimeRemainingSec <= 0) {
                            // Turn timeout -> advance turn or handle missed answer
                            val active = engine.players.filter { it.isActive }
                            val next = active.randomOrNull() ?: active.first()
                            engine.currentAskerId = next.id
                            engine.currentTargetId = null
                            engine.turnStage = TurnStage.ASKING
                            engine.turnTimeRemainingSec = engine.config.turnTimeSec
                            syncWithEngine()
                            triggerBotActionIfNeeded()
                        }
                    }
                    syncWithEngine()
                } else if (engine.phase == Phase.VOTING) {
                    if (engine.voteTimeRemainingSec > 0) {
                        engine.voteTimeRemainingSec--
                        if (engine.voteTimeRemainingSec <= 0) {
                            engine.resolveVoting()
                            syncWithEngine()
                        }
                    }
                    syncWithEngine()
                } else {
                    break
                }
            }
        }
    }

    private fun stopTimers() {
        timerJob?.cancel()
        timerJob = null
        botJob?.cancel()
        botJob = null
    }

    private fun triggerBotActionIfNeeded() {
        if (!_uiState.value.isSoloPractice) return
        botJob?.cancel()
        botJob = viewModelScope.launch {
            delay(1800L)
            if (engine.phase != Phase.PLAYING) return@launch

            val asker = engine.players.find { it.id == engine.currentAskerId }
            val target = engine.players.find { it.id == engine.currentTargetId }

            if (engine.turnStage == TurnStage.ASKING && asker?.isBot == true) {
                // Bot asks question
                val candidates = engine.players.filter { it.isActive && it.id != asker.id }
                val chosenTarget = candidates.randomOrNull() ?: return@launch
                val sampleQuestions = listOf(
                    "Часто ли ты бываешь в таких местах?",
                    "Что ты обычно надеваешь туда?",
                    "Там тепло или холодно?",
                    "Много ли там незнакомых людей?",
                    "Нужны ли там специальные навыки?",
                    "Ты бы порекомендовал это место друзьям?"
                )
                askQuestion(chosenTarget.id, sampleQuestions.random())
            } else if (engine.turnStage == TurnStage.ANSWERING && target?.isBot == true) {
                // Bot answers question
                val sampleAnswers = listOf(
                    "Да, вполне привычно для меня.",
                    "Не скажу, что часто, но бывал.",
                    "Обычно одеваются удобно и практично.",
                    "Там довольно шумно и оживлённо.",
                    "Всё зависит от времени суток и компании."
                )
                answerQuestion(sampleAnswers.random())
            }
        }
    }

    private fun triggerBotVotesIfNeeded() {
        if (!_uiState.value.isSoloPractice) return
        viewModelScope.launch {
            delay(1200L)
            val bots = engine.players.filter { it.isActive && it.isBot }
            val candidates = engine.players.filter { it.isActive }
            for (bot in bots) {
                val targets = candidates.filter { it.id != bot.id }
                val chosenTarget = targets.randomOrNull()?.id
                castVote(bot.id, chosenTarget)
            }
        }
    }
}
