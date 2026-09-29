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

private val SAMPLE_ASSOCIATIONS = listOf(
    "Звуки", "Вода", "Очередь", "Дорого", "Билеты", "Холод", "Запах", "Ночь", "Музыка"
)

@Composable
fun AssociationDialog(
    askerName: String,
    onDismiss: () -> Unit,
    onSendAssociation: (word: String) -> Unit
) {
    var wordText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SpyBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("association_dialog")
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
                        text = "Сказать ассоциацию",
                        color = SpyAmber,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Закрыть", tint = SpyMuted)
                    }
                }

                Text(
                    text = "Вместо вопроса вы можете назвать 1–2 слова-ассоциации для локации.",
                    color = SpyMuted,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SAMPLE_ASSOCIATIONS) { chip ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = SpyBg2,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
                            modifier = Modifier.clickable { wordText = chip }
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
                    value = wordText,
                    onValueChange = { wordText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Например: «Вода», «Спешка»...", color = SpyMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SpyBg2,
                        unfocusedContainerColor = SpyBg2,
                        focusedBorderColor = SpyAmber,
                        unfocusedBorderColor = SpyLine,
                        focusedTextColor = SpyText,
                        unfocusedTextColor = SpyText
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (wordText.isNotBlank()) {
                            onSendAssociation(wordText.trim())
                        }
                    },
                    enabled = wordText.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_association_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpyAmber,
                        contentColor = SpyAccentInk
                    )
                ) {
                    Text(text = "Назвать ассоциацию", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
