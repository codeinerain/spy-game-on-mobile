package com.example.spygame.model

data class GameLogEntry(
    val id: String,
    val type: EntryType,
    val fromPlayerName: String,
    val toPlayerName: String? = null,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Notice(
    val id: String,
    val text: String,
    val isWarning: Boolean = false,
    val isGood: Boolean = false
)

data class VotingResult(
    val accusedPlayerId: String?,
    val accusedPlayerName: String?,
    val isSpy: Boolean?,
    val tiedPlayerNames: List<String>,
    val votesTally: Map<String, Int>
)
