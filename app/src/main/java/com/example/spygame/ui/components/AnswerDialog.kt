package com.example.spygame.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.spygame.ui.theme.*

private val ANSWER_QUICK_CHIPS = listOf(
    "Да, довольно часто",
    "Редко или почти никогда",
    "Зависит от сезона и настроения",
    "Только в хорошей компании",
    "Там всегда много людей",
    "Там тихо и спокойно",
    "Это стоит недешево",
    "Каждый там хотя бы раз был"
)

@Composable
fun AnswerDialog(
    questionText: String,
    askerName: String,
    onDismiss: () -> Unit,
    onSendAnswer: (answer: String) -> Unit
) {
    var answerText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SpyBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("answer_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ответ на вопрос",
                        color = SpyText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Закрыть", tint = SpyMuted)
                    }
                }

                // Question quote box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SpyPanel2,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Вопрос от $askerName:",
                            color = SpyAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = questionText,
                            color = SpyText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Быстрые варианты ответов:",
                    color = SpyMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ANSWER_QUICK_CHIPS) { chip ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = SpyBg2,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
                            modifier = Modifier.clickable { answerText = chip }
                        ) {
                            Text(
                                text = chip,
                                color = SpyText,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = answerText,
                    onValueChange = { answerText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Введите ваш ответ...", color = SpyMuted) },
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
                        if (answerText.isNotBlank()) {
                            onSendAnswer(answerText.trim())
                        }
                    },
                    enabled = answerText.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_answer_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpyOk,
                        contentColor = SpyAccentInk
                    )
                ) {
                    Text(text = "Ответить", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
