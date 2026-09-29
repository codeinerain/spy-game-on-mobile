package com.example.spygame.model

data class Player(
    val id: String,
    val name: String,
    val avatar: String,
    val isBot: Boolean = false,
    val isHost: Boolean = false,
    val ready: Boolean = false,
    val score: Int = 0,
    val role: Role? = null,
    val exposed: Boolean = false,
    val exposedReason: ExposeReason? = null,
    val roleViewed: Boolean = false,
    val left: Boolean = false
) {
    val isActive: Boolean get() = !left && !exposed
}
