package com.example.spygame

import com.example.spygame.data.LocationRepository
import com.example.spygame.engine.SpyGameEngine
import com.example.spygame.model.GameLimits
import com.example.spygame.model.Phase
import com.example.spygame.model.Role
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameEngineTest {

    private lateinit var locationRepository: LocationRepository
    private lateinit var engine: SpyGameEngine

    @Before
    fun setUp() {
        locationRepository = LocationRepository()
        engine = SpyGameEngine(locationRepository)
        engine.initDefaultPlayers()
    }

    @Test
    fun testInitialRosterSize() {
        assertEquals(4, engine.players.size)
        assertEquals(Phase.MENU, engine.phase)
    }

    @Test
    fun testRosterClamping() {
        engine.setRosterSize(2, isSolo = false)
        assertEquals(GameLimits.MIN_PLAYERS, engine.players.size)

        engine.setRosterSize(12, isSolo = false)
        assertEquals(GameLimits.MAX_PLAYERS, engine.players.size)
    }

    @Test
    fun testSpyCountCalculation() {
        assertEquals(1, SpyGameEngine.spyCountFor(3))
        assertEquals(1, SpyGameEngine.spyCountFor(4))
        assertEquals(2, SpyGameEngine.spyCountFor(5))
        assertEquals(2, SpyGameEngine.spyCountFor(6))
        assertEquals(3, SpyGameEngine.spyCountFor(7))
        assertEquals(3, SpyGameEngine.spyCountFor(10))
    }

    @Test
    fun testStartNewRoundAssignsRoles() {
        engine.setRosterSize(5, isSolo = false)
        engine.config = engine.config.copy(customSpyCount = 1)
        val started = engine.startNewRound()

        assertTrue(started)
        assertEquals(Phase.ROLE_REVEAL, engine.phase)
        assertNotNull(engine.currentLocation)

        val spies = engine.players.filter { it.role == Role.SPY }
        val civilians = engine.players.filter { it.role == Role.CIVILIAN }

        assertEquals(1, spies.size)
        assertEquals(4, civilians.size)
    }

    @Test
    fun testMultipleSpiesConfiguration() {
        engine.setRosterSize(7, isSolo = false)
        engine.config = engine.config.copy(customSpyCount = 2)
        engine.startNewRound()

        val spies = engine.players.filter { it.role == Role.SPY }
        assertEquals(2, spies.size)
    }

    @Test
    fun testSoloModeBotsAreMarkedAsBots() {
        engine.setRosterSize(5, isSolo = true)
        val humanPlayers = engine.players.filter { !it.isBot }
        val botPlayers = engine.players.filter { it.isBot }

        assertEquals(1, humanPlayers.size)
        assertEquals("Вы", humanPlayers.first().name)
        assertEquals(4, botPlayers.size)
    }

    @Test
    fun testBotGeneratesContextualQuestionsAndAnswers() {
        val location = com.example.spygame.model.LocationItem("Кинотеатр", listOf("кино"))
        val civilian = com.example.spygame.model.Player("1", "Бот 1", "🤖", isBot = true, role = Role.CIVILIAN)
        val spy = com.example.spygame.model.Player("2", "Бот 2", "🕵️", isBot = true, role = Role.SPY)

        val civQuestion = com.example.spygame.engine.BotIntelligence.generateQuestion(civilian, spy, location)
        assertTrue(civQuestion.isNotBlank())

        val spyQuestion = com.example.spygame.engine.BotIntelligence.generateQuestion(spy, civilian, null)
        assertTrue(spyQuestion.isNotBlank())

        val civAnswer = com.example.spygame.engine.BotIntelligence.generateAnswer(civilian, "Там темно?", location)
        assertTrue(civAnswer.isNotBlank())

        val spyAnswer = com.example.spygame.engine.BotIntelligence.generateAnswer(spy, "Там темно?", null)
        assertTrue(spyAnswer.isNotBlank())
    }
}
