package com.example.spygame.model

data class GameConfig(
    val roundTimeSec: Int = 300,
    val turnTimeSec: Int = 60,
    val voteTimeSec: Int = 45,
    val roleRevealSec: Int = 60,
    val votingMode: VotingMode = VotingMode.MAJORITY,
    val guessRule: GuessRule = GuessRule.ELIMINATE_GUESSER,
    val autoVoteAfterCircles: Int = 2,
    val maxVoteAttempts: Int = 2,
    val allowAssociations: Boolean = true,
    val packId: String = "default",
    val enabledCategoryIds: Set<String> = emptySet(), // empty means all categories enabled
    val customSpyCount: Int? = null
)

object ConfigOptions {
    val roundTimeOptions = listOf(180, 300, 420, 600, 0)
    val turnTimeOptions = listOf(30, 60, 90, 0)
    val autoVoteOptions = listOf(0, 1, 2, 3)
    val maxVoteAttemptsOptions = listOf(1, 2, 3)

    fun roundTimeLabel(sec: Int): String = when (sec) {
        0 -> "Без лимита"
        180 -> "3 мин"
        300 -> "5 мин"
        420 -> "7 мин"
        600 -> "10 мин"
        else -> "${sec / 60} мин"
    }

    fun turnTimeLabel(sec: Int): String = when (sec) {
        0 -> "Без лимита"
        30 -> "30 сек"
        60 -> "60 сек"
        90 -> "90 сек"
        else -> "$sec сек"
    }

    fun autoVoteLabel(circles: Int): String = when (circles) {
        0 -> "Только вручную"
        1 -> "Каждый круг"
        2 -> "Каждые 2 круга"
        3 -> "Каждые 3 круга"
        else -> "$circles круга"
    }
}
