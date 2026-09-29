package com.example.spygame.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.spygame.model.Player
import com.example.spygame.ui.theme.*

private val QUESTION_PROMPTS = listOf(
    "Ты часто здесь бываешь?",
    "Что обычно надевают в это место?",
    "Здесь шумно или тихо?",
    "Тут платят деньги или работают?",
    "Здесь тепло или холодно?",
    "Тут можно встретить детей?",
    "Что там делают по выходным?",
    "Ты бы взял с собой собаку?"
)

@Composable
fun QuestionDialog(
    currentAsker: Player,
    availableTargets: List<Player>,
    onDismiss: () -> Unit,
    onSendQuestion: (targetId: String, question: String) -> Unit
) {
    var selectedTargetId by remember { mutableStateOf(availableTargets.firstOrNull()?.id ?: "") }
    var questionText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SpyBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("question_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Задать вопрос",
                        color = SpyText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Закрыть", tint = SpyMuted)
                    }
                }

                Text(
                    text = "Кому вы хотите задать вопрос?",
                    color = SpyMuted,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Target players row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableTargets) { player ->
                        val isSelected = player.id == selectedTargetId
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) SpyAccent.copy(alpha = 0.2f) else SpyPanel,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) SpyAccent else SpyLine
                            ),
                            modifier = Modifier.clickable { selectedTargetId = player.id }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = player.avatar, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = player.name,
                                    color = if (isSelected) SpyAccent else SpyText,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Prompt suggestions
                Text(
                    text = "Примеры вопросов:",
                    color = SpyMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(QUESTION_PROMPTS) { prompt ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = SpyBg2,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
                            modifier = Modifier.clickable { questionText = prompt }
                        ) {
                            Text(
                                text = prompt,
                                color = SpyText,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Input
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Введите текст вопроса...", color = SpyMuted) },
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SpyBg2,
                        unfocusedContainerColor = SpyBg2,
                        focusedBorderColor = SpyAccent,
                        unfocusedBorderColor = SpyLine,
                        focusedTextColor = SpyText,
                        unfocusedTextColor = SpyText
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedTargetId.isNotBlank() && questionText.isNotBlank()) {
                            onSendQuestion(selectedTargetId, questionText.trim())
                        }
                    },
                    enabled = selectedTargetId.isNotBlank() && questionText.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_question_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpyAccent,
                        contentColor = SpyAccentInk
                    )
                ) {
                    Text(text = "Задать вопрос", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
