package com.example.spygame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.engine.SpyGameEngine
import com.example.spygame.model.*
import com.example.spygame.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(
    players: List<Player>,
    config: GameConfig,
    isSoloMode: Boolean,
    onBackToMenu: () -> Unit,
    onAddPlayer: (name: String, avatar: String, isBot: Boolean) -> Unit,
    onRemovePlayer: (playerId: String) -> Unit,
    onOpenSettings: () -> Unit,
    onStartRound: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newPlayerName by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf(GAME_AVATARS.first()) }

    val spyCount = SpyGameEngine.spyCountFor(players.size.coerceIn(GameLimits.MIN_PLAYERS, GameLimits.MAX_PLAYERS))
    val canStart = players.size >= GameLimits.MIN_PLAYERS

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isSoloMode) "Одиночная игра" else "Комната игры",
                        fontWeight = FontWeight.Bold,
                        color = SpyText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToMenu) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "В меню",
                            tint = SpyText
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки",
                            tint = SpyAmber
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SpyBg
                )
            )
        },
        containerColor = SpyBg,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Spy summary chip
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SpyPanel,
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Игроков: ${players.size} (мин. 3, макс. 10)",
                            color = SpyText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (canStart) "В этом раунде будет шпионов: $spyCount" else "Добавьте ещё ${3 - players.size} игроков",
                            color = if (canStart) SpyAmber else SpyDanger,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SpyPanel2,
                        modifier = Modifier.clickable { onOpenSettings() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = SpyMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = ConfigOptions.roundTimeLabel(config.roundTimeSec),
                                color = SpyMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Players List Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "СОСТАВ ИГРОКОВ",
                    color = SpyMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                if (players.size < GameLimits.MAX_PLAYERS) {
                    TextButton(
                        onClick = {
                            newPlayerName = "Игрок ${players.size + 1}"
                            selectedAvatar = GAME_AVATARS[players.size % GAME_AVATARS.size]
                            showAddDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = SpyAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Добавить",
                            color = SpyAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Players lazy list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(players, key = { it.id }) { player ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SpyPanel,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(SpyPanel2)
                                ) {
                                    Text(text = player.avatar, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = player.name,
                                            color = SpyText,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (player.isHost) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Хост",
                                                color = SpyAmber,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (player.isBot) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Бот",
                                                color = SpyMuted,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Очков: ${player.score}",
                                        color = SpyMuted,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            if (players.size > GameLimits.MIN_PLAYERS) {
                                IconButton(
                                    onClick = { onRemovePlayer(player.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Удалить",
                                        tint = SpyDanger.copy(alpha = 0.8f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Start Game Button
            Button(
                onClick = onStartRound,
                enabled = canStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("start_round_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SpyAccent,
                    contentColor = SpyAccentInk,
                    disabledContainerColor = SpyPanel2,
                    disabledContentColor = SpyMuted
                )
            ) {
                Text(
                    text = if (canStart) "Раздать роли и начать игру" else "Нужно минимум 3 игрока",
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp
                )
            }
        }
    }

    // Add Player Dialog
    if (showAddDialog) {
        var isBotChecked by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Новый игрок",
                    color = SpyText,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPlayerName,
                        onValueChange = { newPlayerName = it.take(GameLimits.NAME_MAX) },
                        label = { Text("Имя игрока") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SpyBg2,
                            unfocusedContainerColor = SpyBg2,
                            focusedBorderColor = SpyAccent,
                            unfocusedBorderColor = SpyLine,
                            focusedTextColor = SpyText,
                            unfocusedTextColor = SpyText
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Выберите аватар:",
                        color = SpyMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(GAME_AVATARS) { avatar ->
                            val isSelected = avatar == selectedAvatar
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) SpyAccent.copy(alpha = 0.25f) else SpyPanel2)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) SpyAccent else SpyLine,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAvatar = avatar }
                            ) {
                                Text(text = avatar, fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isBotChecked = !isBotChecked }
                    ) {
                        Checkbox(
                            checked = isBotChecked,
                            onCheckedChange = { isBotChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = SpyAccent,
                                checkmarkColor = SpyAccentInk
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Виртуальный бот (для соло-игры)",
                            color = SpyText,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPlayerName.isNotBlank()) {
                            onAddPlayer(newPlayerName.trim(), selectedAvatar, isBotChecked)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpyAccent,
                        contentColor = SpyAccentInk
                    )
                ) {
                    Text("Добавить", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Отмена", color = SpyMuted)
                }
            },
            containerColor = SpyPanel,
            shape = RoundedCornerShape(18.dp)
        )
    }
}
