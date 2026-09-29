package com.example.spygame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.model.*
import com.example.spygame.ui.components.*
import com.example.spygame.ui.theme.*

@Composable
fun GameScreen(
    players: List<Player>,
    config: GameConfig,
    currentAskerId: String?,
    currentTargetId: String?,
    currentQuestionText: String?,
    turnStage: TurnStage,
    roundSecondsRemaining: Int,
    turnSecondsRemaining: Int,
    log: List<GameLogEntry>,
    notices: List<Notice>,
    canStartVoting: Boolean,
    categories: List<LocationCategory>,
    showSpyGuessDialog: Boolean,
    showQuestionDialog: Boolean,
    showAnswerDialog: Boolean,
    showAssociationDialog: Boolean,
    onOpenQuestionDialog: () -> Unit,
    onCloseQuestionDialog: () -> Unit,
    onSendQuestion: (toPlayerId: String, text: String) -> Unit,
    onOpenAnswerDialog: () -> Unit,
    onCloseAnswerDialog: () -> Unit,
    onSendAnswer: (text: String) -> Unit,
    onOpenAssociationDialog: () -> Unit,
    onCloseAssociationDialog: () -> Unit,
    onSendAssociation: (word: String) -> Unit,
    onOpenSpyGuessDialog: () -> Unit,
    onCloseSpyGuessDialog: () -> Unit,
    onConfirmSpyGuess: (location: String) -> Unit,
    onStartVoting: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentAsker = players.find { it.id == currentAskerId }
    val currentTarget = players.find { it.id == currentTargetId }

    val logListState = rememberLazyListState()

    // Auto scroll to bottom of log when new entries arrive
    LaunchedEffect(log.size) {
        if (log.isNotEmpty()) {
            logListState.animateScrollToItem(log.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SpyBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Timers
            TimerDisplay(
                roundSecondsRemaining = roundSecondsRemaining,
                totalRoundSeconds = config.roundTimeSec,
                turnSecondsRemaining = turnSecondsRemaining,
                totalTurnSeconds = config.turnTimeSec
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Player Seats Ribbon
            PlayerSeatRibbon(
                players = players,
                currentAskerId = currentAskerId,
                currentTargetId = currentTargetId
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Current Turn State Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SpyPanel,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (turnStage == TurnStage.ANSWERING) SpyAccent else SpyAmber
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (turnStage == TurnStage.ASKING) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = currentAsker?.avatar ?: "🕵️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ход: ${currentAsker?.name ?: "Игрок"}",
                                color = SpyAmber,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Задайте наводящий вопрос любому игроку или назовите ассоциацию.",
                            color = SpyText,
                            fontSize = 13.sp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = currentTarget?.avatar ?: "🤔", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Отвечает: ${currentTarget?.name ?: "Игрок"}",
                                color = SpyAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        if (currentQuestionText != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SpyBg2,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "«$currentQuestionText»",
                                    color = SpyText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Log of Questions & Answers
            Text(
                text = "ИСТОРИЯ ВОПРОСОВ И ОТВЕТОВ",
                color = SpyMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                state = logListState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (log.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SpyPanel2,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Вопросов пока не было. Нажмите «Задать вопрос», чтобы начать диалог.",
                                color = SpyMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }

                items(log, key = { it.id }) { entry ->
                    when (entry.type) {
                        EntryType.QUESTION -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SpyPanel,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "❓ ${entry.fromPlayerName} → ${entry.toPlayerName}:",
                                            color = SpyAmber,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = entry.text,
                                        color = SpyText,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        EntryType.ANSWER -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SpyPanel2,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SpyOk.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "💬 Ответ от ${entry.fromPlayerName}:",
                                        color = SpyOk,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = entry.text,
                                        color = SpyText,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        EntryType.ASSOCIATION -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SpyPanel,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SpyAmber.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "💡 Ассоциация от ${entry.fromPlayerName}:",
                                        color = SpyAmber,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "«${entry.text}»",
                                        color = SpyText,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Primary Turn Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (turnStage == TurnStage.ASKING) {
                        Button(
                            onClick = onOpenQuestionDialog,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("ask_question_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SpyAccent,
                                contentColor = SpyAccentInk
                            )
                        ) {
                            Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Задать вопрос", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        if (config.allowAssociations) {
                            OutlinedButton(
                                onClick = onOpenAssociationDialog,
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(50.dp)
                                    .testTag("send_association_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = SpyPanel2,
                                    contentColor = SpyAmber
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SpyAmber)
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ассоциация", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    } else {
                        Button(
                            onClick = onOpenAnswerDialog,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("open_answer_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SpyOk,
                                contentColor = SpyAccentInk
                            )
                        ) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ответить на вопрос", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                // Secondary Global Actions: Voting & Guess Location
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Voting Button
                    OutlinedButton(
                        onClick = onStartVoting,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("start_voting_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = SpyPanel,
                            contentColor = SpyText
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine)
                    ) {
                        Icon(Icons.Default.HowToVote, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Голосование", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    // Spy Guess Button
                    Button(
                        onClick = onOpenSpyGuessDialog,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("spy_guess_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SpyAmber,
                            contentColor = SpyAccentInk
                        )
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Назвать локацию", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Dialogs
        if (showQuestionDialog && currentAsker != null) {
            val targets = players.filter { it.isActive && it.id != currentAsker.id }
            QuestionDialog(
                currentAsker = currentAsker,
                availableTargets = targets,
                onDismiss = onCloseQuestionDialog,
                onSendQuestion = onSendQuestion
            )
        }

        if (showAnswerDialog && currentTarget != null) {
            AnswerDialog(
                questionText = currentQuestionText ?: "",
                askerName = currentAsker?.name ?: "Игрок",
                onDismiss = onCloseAnswerDialog,
                onSendAnswer = onSendAnswer
            )
        }

        if (showAssociationDialog && currentAsker != null) {
            AssociationDialog(
                askerName = currentAsker.name,
                onDismiss = onCloseAssociationDialog,
                onSendAssociation = onSendAssociation
            )
        }

        if (showSpyGuessDialog) {
            SpyGuessDialog(
                categories = categories,
                onDismiss = onCloseSpyGuessDialog,
                onConfirmGuess = onConfirmSpyGuess
            )
        }
    }
}
