package com.example.spygame.model

enum class Phase {
    MENU,
    LOBBY,
    ROLE_REVEAL,
    PLAYING,
    VOTING,
    RESULTS,
    RULES,
    PACKS
}

enum class Role {
    SPY,
    CIVILIAN
}

enum class Team {
    SPIES,
    CIVILIANS
}

enum class TurnStage {
    ASKING,
    ANSWERING
}

enum class EntryType {
    QUESTION,
    ANSWER,
    ASSOCIATION
}

enum class VotingMode(val title: String, val hint: String) {
    MAJORITY("Большинство", "Обвиняется игрок с наибольшим числом голосов. При ничьей игра продолжается."),
    UNANIMOUS("Единогласно", "Мирные побеждают, только если все мирные указали на одного игрока."),
    MULTI_ROUND("С переголосованием", "При ничьей сразу проходит переголосование между лидерами.")
}

enum class GuessRule(val title: String) {
    ELIMINATE_GUESSER("Выбывает ошибшийся"),
    TEAM_LOSES("Проигрывают все шпионы")
}

enum class ExposeReason(val title: String) {
    VOTED("раскрыт голосованием"),
    WRONG_GUESS("ошибся с локацией"),
    LEFT("вышел из игры")
}

enum class EndReason(val title: String) {
    SPY_GUESSED("Шпион правильно назвал локацию."),
    WRONG_GUESS("Шпион ошибся, называя локацию."),
    SPY_CAUGHT("Шпиона раскрыли голосованием."),
    SPIES_LEFT("Шпионы покинули игру."),
    INNOCENT_ACCUSED("Игроки обвинили мирного — шпионы остались незамеченными."),
    NOT_CAUGHT("Шпионов не раскрыли вовремя."),
    CIVILIANS_GONE("Не осталось мирных игроков."),
    ABORTED("Раунд прерван.")
}

object GameLimits {
    const val MIN_PLAYERS = 3
    const val MAX_PLAYERS = 10
    const val NAME_MAX = 20
    const val QUESTION_MAX = 200
    const val ANSWER_MAX = 200
    const val ASSOCIATION_MAX = 40
    const val GUESS_MAX = 60
}

val GAME_AVATARS = listOf(
    "🦊", "🐼", "🐸", "🦉", "🐙", "🦄", "🐯", "🐧",
    "🦁", "🐨", "🐰", "🦋", "🐢", "🦈", "🐝", "🦖"
)
