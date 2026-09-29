package com.example.spygame.engine

import com.example.spygame.data.LocationRepository
import com.example.spygame.model.*
import java.util.UUID

class SpyGameEngine(
    val locationRepository: LocationRepository = LocationRepository()
) {
    var config: GameConfig = GameConfig()
    var phase: Phase = Phase.MENU
    val players = mutableListOf<Player>()
    val log = mutableListOf<GameLogEntry>()
    val notices = mutableListOf<Notice>()

    var currentLocation: LocationItem? = null
    var spyCount: Int = 1
    var currentAskerId: String? = null
    var currentTargetId: String? = null
    var currentQuestionText: String? = null
    var turnStage: TurnStage = TurnStage.ASKING

    var roundNumber: Int = 0
    var turnsTaken: Int = 0
    var circlesCompleted: Int = 0
    var voteAttempts: Int = 0
    var roundTimeRemainingSec: Int = 300
    var turnTimeRemainingSec: Int = 60
    var voteTimeRemainingSec: Int = 45

    // Voting state
    val votes = mutableMapOf<String, String?>() // voterId -> targetPlayerId
    var runoffCandidates: List<String>? = null
    var lastVotingResult: VotingResult? = null

    // Winner state
    var winningTeam: Team? = null
    var endReason: EndReason? = null
    var winningDetails: String? = null

    companion object {
        fun spyCountFor(playerCount: Int): Int {
            return when {
                playerCount <= 4 -> 1
                playerCount <= 6 -> 2
                else -> 3
            }
        }
    }

    fun initDefaultPlayers() {
        if (players.isEmpty()) {
            players.add(Player(id = UUID.randomUUID().toString(), name = "Игрок 1", avatar = "🦊", isHost = true, ready = true))
            players.add(Player(id = UUID.randomUUID().toString(), name = "Игрок 2", avatar = "🐼", isHost = false, ready = true))
            players.add(Player(id = UUID.randomUUID().toString(), name = "Игрок 3", avatar = "🦉", isHost = false, ready = true))
            players.add(Player(id = UUID.randomUUID().toString(), name = "Игрок 4 (Бот)", avatar = "🤖", isBot = true, ready = true))
        }
    }

    fun addPlayer(name: String, avatar: String, isBot: Boolean = false): Player {
        val clean = name.trim().take(GameLimits.NAME_MAX)
        val player = Player(
            id = UUID.randomUUID().toString(),
            name = if (clean.isEmpty()) "Игрок ${players.size + 1}" else clean,
            avatar = avatar,
            isBot = isBot,
            isHost = players.isEmpty(),
            ready = players.isEmpty()
        )
        players.add(player)
        addNotice("${player.name} присоединился к игре")
        return player
    }

    fun removePlayer(playerId: String) {
        val p = players.find { it.id == playerId } ?: return
        players.remove(p)
        if (p.isHost && players.isNotEmpty()) {
            players[0] = players[0].copy(isHost = true, ready = true)
        }
        addNotice("${p.name} покинул комнату")
    }

    fun updatePlayer(playerId: String, name: String, avatar: String) {
        val index = players.indexOfFirst { it.id == playerId }
        if (index != -1) {
            players[index] = players[index].copy(name = name.trim().take(GameLimits.NAME_MAX), avatar = avatar)
        }
    }

    fun toggleReady(playerId: String) {
        val index = players.indexOfFirst { it.id == playerId }
        if (index != -1 && !players[index].isHost) {
            players[index] = players[index].copy(ready = !players[index].ready)
        }
    }

    fun startNewRound(): Boolean {
        if (players.size < GameLimits.MIN_PLAYERS) return false

        roundNumber++
        log.clear()
        votes.clear()
        runoffCandidates = null
        lastVotingResult = null
        winningTeam = null
        endReason = null
        winningDetails = null

        val pool = locationRepository.getPool(config.enabledCategoryIds)
        currentLocation = locationRepository.getRandomLocation(config.enabledCategoryIds) ?: pool.firstOrNull()
        spyCount = spyCountFor(players.size)

        // Shuffle roles
        val shuffledIndices = players.indices.shuffled()
        val spyIndices = shuffledIndices.take(spyCount).toSet()

        for (i in players.indices) {
            val isSpy = i in spyIndices
            players[i] = players[i].copy(
                role = if (isSpy) Role.SPY else Role.CIVILIAN,
                exposed = false,
                exposedReason = null,
                roleViewed = false
            )
        }

        // Set initial asker
        val initialAsker = players.filter { it.isActive }.randomOrNull() ?: players.first()
        currentAskerId = initialAsker.id
        currentTargetId = null
        currentQuestionText = null
        turnStage = TurnStage.ASKING

        turnsTaken = 0
        circlesCompleted = 0
        voteAttempts = 0
        roundTimeRemainingSec = if (config.roundTimeSec > 0) config.roundTimeSec else 9999
        turnTimeRemainingSec = if (config.turnTimeSec > 0) config.turnTimeSec else 9999

        phase = Phase.ROLE_REVEAL
        addNotice("Раунд $roundNumber начался. Секретные досье розданы!")
        return true
    }

    fun markRoleViewed(playerId: String) {
        val idx = players.indexOfFirst { it.id == playerId }
        if (idx != -1) {
            players[idx] = players[idx].copy(roleViewed = true)
        }
    }

    fun beginPlaying() {
        phase = Phase.PLAYING
        addNotice("Игра началась! Задавайте вопросы.")
    }

    fun askQuestion(fromPlayerId: String, toPlayerId: String, text: String) {
        val asker = players.find { it.id == fromPlayerId } ?: return
        val target = players.find { it.id == toPlayerId } ?: return
        currentAskerId = fromPlayerId
        currentTargetId = toPlayerId
        currentQuestionText = text
        turnStage = TurnStage.ANSWERING
        turnTimeRemainingSec = if (config.turnTimeSec > 0) config.turnTimeSec else 9999

        log.add(
            GameLogEntry(
                id = UUID.randomUUID().toString(),
                type = EntryType.QUESTION,
                fromPlayerName = asker.name,
                toPlayerName = target.name,
                text = text
            )
        )
    }

    fun answerQuestion(fromPlayerId: String, text: String) {
        val target = players.find { it.id == fromPlayerId } ?: return
        val asker = players.find { it.id == currentAskerId }

        log.add(
            GameLogEntry(
                id = UUID.randomUUID().toString(),
                type = EntryType.ANSWER,
                fromPlayerName = target.name,
                toPlayerName = asker?.name,
                text = text
            )
        )

        advanceTurn(nextAskerId = target.id)
    }

    fun associate(fromPlayerId: String, word: String) {
        val asker = players.find { it.id == fromPlayerId } ?: return
        log.add(
            GameLogEntry(
                id = UUID.randomUUID().toString(),
                type = EntryType.ASSOCIATION,
                fromPlayerName = asker.name,
                toPlayerName = null,
                text = word
            )
        )
        // Pass turn to next in roster order
        val active = players.filter { it.isActive }
        val currIdx = active.indexOfFirst { it.id == fromPlayerId }
        val nextPlayer = if (currIdx != -1) active[(currIdx + 1) % active.size] else active.first()
        advanceTurn(nextAskerId = nextPlayer.id)
    }

    private fun advanceTurn(nextAskerId: String) {
        currentAskerId = nextAskerId
        currentTargetId = null
        currentQuestionText = null
        turnStage = TurnStage.ASKING
        turnTimeRemainingSec = if (config.turnTimeSec > 0) config.turnTimeSec else 9999

        turnsTaken++
        val activeCount = players.count { it.isActive }
        if (activeCount > 0 && turnsTaken % activeCount == 0) {
            circlesCompleted++
            addNotice("Круг $circlesCompleted завершён.")
            if (config.autoVoteAfterCircles > 0 && circlesCompleted % config.autoVoteAfterCircles == 0) {
                startVoting(auto = true)
            }
        }
    }

    fun canStartManualVoting(): Boolean {
        return phase == Phase.PLAYING && circlesCompleted >= 1 && voteAttempts < config.maxVoteAttempts
    }

    fun startVoting(auto: Boolean = false, candidates: List<String>? = null) {
        phase = Phase.VOTING
        votes.clear()
        runoffCandidates = candidates
        voteTimeRemainingSec = config.voteTimeSec
        addNotice(if (auto) "Автоматическое голосование раунда!" else "Началось голосование по подозреваемым!")
    }

    fun castVote(voterId: String, targetPlayerId: String?) {
        votes[voterId] = targetPlayerId
        val activePlayers = players.filter { it.isActive }
        // If all active players have cast their votes, resolve immediately
        if (votes.keys.containsAll(activePlayers.map { it.id })) {
            resolveVoting()
        }
    }

    fun resolveVoting() {
        val activePlayers = players.filter { it.isActive }
        val tally = mutableMapOf<String, Int>()
        for ((_, target) in votes) {
            if (target != null) {
                tally[target] = (tally[target] ?: 0) + 1
            }
        }

        when (config.votingMode) {
            VotingMode.UNANIMOUS -> {
                val civilians = activePlayers.filter { it.role == Role.CIVILIAN }
                val targetVotedByAll = civilians.mapNotNull { votes[it.id] }.distinct()
                if (targetVotedByAll.size == 1 && civilians.all { votes[it.id] != null }) {
                    val accusedId = targetVotedByAll.first()
                    handleAccused(accusedId, tally)
                } else {
                    handleVoteInconclusive(tally)
                }
            }
            VotingMode.MAJORITY -> {
                if (tally.isEmpty()) {
                    handleVoteInconclusive(tally)
                    return
                }
                val maxVotes = tally.values.maxOrNull() ?: 0
                val leaders = tally.filter { it.value == maxVotes }.keys.toList()
                if (leaders.size == 1) {
                    handleAccused(leaders.first(), tally)
                } else {
                    handleVoteInconclusive(tally, leaders)
                }
            }
            VotingMode.MULTI_ROUND -> {
                if (tally.isEmpty()) {
                    handleVoteInconclusive(tally)
                    return
                }
                val maxVotes = tally.values.maxOrNull() ?: 0
                val leaders = tally.filter { it.value == maxVotes }.keys.toList()
                if (leaders.size == 1) {
                    handleAccused(leaders.first(), tally)
                } else if (runoffCandidates == null) {
                    // Start runoff between leaders
                    val leaderNames = leaders.mapNotNull { id -> players.find { it.id == id }?.name }
                    addNotice("Ничья! Переголосование между: ${leaderNames.joinToString(", ")}")
                    startVoting(auto = true, candidates = leaders)
                } else {
                    handleVoteInconclusive(tally, leaders)
                }
            }
        }
    }

    private fun handleAccused(accusedId: String, tally: Map<String, Int>) {
        val accused = players.find { it.id == accusedId } ?: return
        val isSpy = accused.role == Role.SPY
        lastVotingResult = VotingResult(
            accusedPlayerId = accusedId,
            accusedPlayerName = accused.name,
            isSpy = isSpy,
            tiedPlayerNames = emptyList(),
            votesTally = tally
        )

        if (isSpy) {
            // Spy caught!
            val idx = players.indexOfFirst { it.id == accusedId }
            players[idx] = players[idx].copy(exposed = true, exposedReason = ExposeReason.VOTED)

            val remainingSpies = players.filter { it.isActive && it.role == Role.SPY }
            if (remainingSpies.isEmpty() || config.guessRule == GuessRule.TEAM_LOSES) {
                // Civilians win!
                endGame(
                    winner = Team.CIVILIANS,
                    reason = EndReason.SPY_CAUGHT,
                    details = "Шпион ${accused.name} был разоблачён!"
                )
            } else {
                addNotice("Шпион ${accused.name} пойман и выбывает! Остался ещё один шпион!")
                phase = Phase.PLAYING
            }
        } else {
            // Civilian was accused! Spies win!
            endGame(
                winner = Team.SPIES,
                reason = EndReason.INNOCENT_ACCUSED,
                details = "Игроки обвинили мирного ${accused.name}! Шпионы побеждают."
            )
        }
    }

    private fun handleVoteInconclusive(tally: Map<String, Int>, tiedIds: List<String> = emptyList()) {
        voteAttempts++
        val tiedNames = tiedIds.mapNotNull { id -> players.find { it.id == id }?.name }
        lastVotingResult = VotingResult(
            accusedPlayerId = null,
            accusedPlayerName = null,
            isSpy = null,
            tiedPlayerNames = tiedNames,
            votesTally = tally
        )

        if (voteAttempts >= config.maxVoteAttempts) {
            // Spies win because civilians failed too many votes
            endGame(
                winner = Team.SPIES,
                reason = EndReason.NOT_CAUGHT,
                details = "Превышен лимит попыток голосования. Шпионы победили!"
            )
        } else {
            addNotice("Голосование не выявило шпиона (попытка $voteAttempts из ${config.maxVoteAttempts}). Игра продолжается!")
            phase = Phase.PLAYING
        }
    }

    fun spyGuess(playerId: String, guessedLocation: String): Boolean {
        val player = players.find { it.id == playerId } ?: return false
        val loc = currentLocation ?: return false
        val correct = locationRepository.checkGuess(loc, guessedLocation)

        if (correct) {
            // Spies win!
            // +3 bonus points to this player
            awardScore(playerId, 3)
            endGame(
                winner = Team.SPIES,
                reason = EndReason.SPY_GUESSED,
                details = "${player.name} правильно назвал локацию: «${loc.name}»!"
            )
            return true
        } else {
            // Wrong guess
            val idx = players.indexOfFirst { it.id == playerId }
            players[idx] = players[idx].copy(exposed = true, exposedReason = ExposeReason.WRONG_GUESS)

            if (config.guessRule == GuessRule.TEAM_LOSES) {
                endGame(
                    winner = Team.CIVILIANS,
                    reason = EndReason.WRONG_GUESS,
                    details = "Шпион ${player.name} ошибся, назвав «$guessedLocation». Победа мирных!"
                )
            } else {
                val remainingSpies = players.filter { it.isActive && it.role == Role.SPY }
                if (remainingSpies.isEmpty()) {
                    endGame(
                        winner = Team.CIVILIANS,
                        reason = EndReason.WRONG_GUESS,
                        details = "Шпион ${player.name} ошибся с локацией. Больше шпионов нет!"
                    )
                } else {
                    addNotice("${player.name} ошибся с локацией и выбывает из игры.")
                }
            }
            return false
        }
    }

    fun onTimeExpired() {
        if (phase == Phase.PLAYING) {
            // Round time expired! Trigger final voting or Spies win if already voted max
            addNotice("Время раунда истекло!")
            startVoting(auto = true)
        } else if (phase == Phase.VOTING) {
            resolveVoting()
        }
    }

    private fun endGame(winner: Team, reason: EndReason, details: String) {
        winningTeam = winner
        endReason = reason
        winningDetails = details
        phase = Phase.RESULTS

        // Award scores
        for (i in players.indices) {
            val p = players[i]
            var pts = 0
            if (winner == Team.CIVILIANS && p.role == Role.CIVILIAN) {
                pts = if (reason == EndReason.SPY_CAUGHT) 2 else 1
            } else if (winner == Team.SPIES && p.role == Role.SPY) {
                pts = if (reason == EndReason.SPY_GUESSED) 3 else 2
            }
            if (pts > 0) {
                players[i] = p.copy(score = p.score + pts)
            }
        }
    }

    private fun awardScore(playerId: String, points: Int) {
        val idx = players.indexOfFirst { it.id == playerId }
        if (idx != -1) {
            players[idx] = players[idx].copy(score = players[idx].score + points)
        }
    }

    private fun addNotice(text: String, isWarning: Boolean = false, isGood: Boolean = false) {
        notices.add(0, Notice(id = UUID.randomUUID().toString(), text = text, isWarning = isWarning, isGood = isGood))
        if (notices.size > 20) notices.removeLast()
    }
}
