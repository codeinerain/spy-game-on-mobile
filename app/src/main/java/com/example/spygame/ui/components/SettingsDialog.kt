package com.example.spygame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.spygame.model.*
import com.example.spygame.ui.theme.*

@Composable
fun SettingsDialog(
    currentConfig: GameConfig,
    onDismiss: () -> Unit,
    onSaveConfig: (GameConfig) -> Unit
) {
    var config by remember { mutableStateOf(currentConfig) }
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SpyBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .testTag("settings_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Настройки раунда",
                        color = SpyText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Закрыть", tint = SpyMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Round Time
                    SettingSection(title = "Время раунда") {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(ConfigOptions.roundTimeOptions) { sec ->
                                val isSelected = config.roundTimeSec == sec
                                ChipOption(
                                    label = ConfigOptions.roundTimeLabel(sec),
                                    isSelected = isSelected,
                                    onClick = { config = config.copy(roundTimeSec = sec) }
                                )
                            }
                        }
                    }

                    // 2. Turn Time
                    SettingSection(title = "Время на ход") {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(ConfigOptions.turnTimeOptions) { sec ->
                                val isSelected = config.turnTimeSec == sec
                                ChipOption(
                                    label = ConfigOptions.turnTimeLabel(sec),
                                    isSelected = isSelected,
                                    onClick = { config = config.copy(turnTimeSec = sec) }
                                )
                            }
                        }
                    }

                    // 3. Voting Mode
                    SettingSection(title = "Правило голосования") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            VotingMode.values().forEach { mode ->
                                val isSelected = config.votingMode == mode
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) SpyAccent.copy(alpha = 0.2f) else SpyPanel,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) SpyAccent else SpyLine
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { config = config.copy(votingMode = mode) }
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = mode.title,
                                            color = if (isSelected) SpyAccent else SpyText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = mode.hint,
                                            color = SpyMuted,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. Guess Rule
                    SettingSection(title = "Ошибка шпиона при угадывании") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            GuessRule.values().forEach { rule ->
                                val isSelected = config.guessRule == rule
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) SpyAmber.copy(alpha = 0.2f) else SpyPanel,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) SpyAmber else SpyLine
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { config = config.copy(guessRule = rule) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = rule.title,
                                            color = if (isSelected) SpyAmber else SpyText,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 5. Auto vote after circles
                    SettingSection(title = "Автоматическое голосование") {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(ConfigOptions.autoVoteOptions) { circles ->
                                val isSelected = config.autoVoteAfterCircles == circles
                                ChipOption(
                                    label = ConfigOptions.autoVoteLabel(circles),
                                    isSelected = isSelected,
                                    onClick = { config = config.copy(autoVoteAfterCircles = circles) }
                                )
                            }
                        }
                    }

                    // 6. Associations toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { config = config.copy(allowAssociations = !config.allowAssociations) },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Разрешить ассоциации",
                                color = SpyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Игрок может назвать слово вместо вопроса",
                                color = SpyMuted,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = config.allowAssociations,
                            onCheckedChange = { config = config.copy(allowAssociations = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SpyAccent,
                                checkedTrackColor = SpyPanel2
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onSaveConfig(config)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_settings_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpyAccent,
                        contentColor = SpyAccentInk
                    )
                ) {
                    Text("Сохранить настройки", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun SettingSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            color = SpyMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        content()
    }
}

@Composable
private fun ChipOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) SpyAccent else SpyPanel,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) SpyAccent else SpyLine
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) SpyAccentInk else SpyText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
