package com.example.spygame.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.model.ConfigOptions
import com.example.spygame.model.GameLimits
import com.example.spygame.ui.theme.*

@Composable
fun MenuScreen(
    playerCount: Int,
    spyCount: Int,
    isSoloMode: Boolean,
    roundTimeSec: Int,
    onPlayerCountChange: (Int) -> Unit,
    onSpyCountChange: (Int) -> Unit,
    onGameModeChange: (isSolo: Boolean) -> Unit,
    onRoundTimeChange: (Int) -> Unit,
    onStartGameDirect: () -> Unit,
    onOpenLobby: () -> Unit,
    onOpenRules: () -> Unit,
    onOpenPacks: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    val maxSpiesAllowed = (playerCount - 1).coerceAtMost(4).coerceAtLeast(1)
    val recommendedSpies = when {
        playerCount <= 4 -> 1
        playerCount <= 7 -> 1
        else -> 2
    }

    val civilianCount = (playerCount - spyCount).coerceAtLeast(1)

    // Local text input states so typing digits feels responsive and fluid
    var playerTextState by remember(playerCount) { mutableStateOf(playerCount.toString()) }
    var spyTextState by remember(spyCount) { mutableStateOf(spyCount.toString()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SpyBg2, SpyBg)
                )
            )
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header & Espionage Brand ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Espionage Badge with Target
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(SpyPanel)
                    .border(2.dp, SpyAmber, CircleShape)
            ) {
                Text(
                    text = "🕵️‍♂️",
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ШПИОН",
                color = SpyText,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            // Top secret classification badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = SpyAmber.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyAmber.copy(alpha = 0.6f)),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "СОВЕРШЕННО СЕКРЕТНО",
                    color = SpyAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Задавайте вопросы, ищите шпиона или угадывайте локацию",
                color = SpyMuted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }

        // --- Game Mode Selector Segmented Card ---
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SpyPanel,
            border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Pass & Play Mode
                val passPlaySelected = !isSoloMode
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (passPlaySelected) SpyAccent else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onGameModeChange(false) }
                        .testTag("mode_pass_and_play")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = if (passPlaySelected) SpyAccentInk else SpyMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "В компании",
                            color = if (passPlaySelected) SpyAccentInk else SpyText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Solo Mode
                val soloSelected = isSoloMode
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (soloSelected) SpyAccent else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onGameModeChange(true) }
                        .testTag("mode_solo")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = if (soloSelected) SpyAccentInk else SpyMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "С ботами",
                            color = if (soloSelected) SpyAccentInk else SpyText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // --- Card 1: NUMBER OF PLAYERS INPUT (Direct Input + Stepper + Slider + Chips) ---
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SpyPanel,
            border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("player_count_section")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = SpyAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "КОЛИЧЕСТВО ИГРОКОВ",
                            color = SpyMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SpyPanel2,
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = "от ${GameLimits.MIN_PLAYERS} до ${GameLimits.MAX_PLAYERS}",
                            color = SpyMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Stepper + Direct Editable Text Input Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Minus button
                    FilledIconButton(
                        onClick = {
                            if (playerCount > GameLimits.MIN_PLAYERS) {
                                val next = playerCount - 1
                                playerTextState = next.toString()
                                onPlayerCountChange(next)
                            }
                        },
                        enabled = playerCount > GameLimits.MIN_PLAYERS,
                        shape = RoundedCornerShape(14.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = SpyPanel2,
                            contentColor = SpyText,
                            disabledContainerColor = SpyBg2.copy(alpha = 0.5f),
                            disabledContentColor = SpyMuted.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .size(54.dp)
                            .testTag("player_count_minus")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Меньше игроков", modifier = Modifier.size(24.dp))
                    }

                    // Direct Editable Text Input for Player Count
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(130.dp)
                    ) {
                        OutlinedTextField(
                            value = playerTextState,
                            onValueChange = { input ->
                                val digitsOnly = input.filter { it.isDigit() }.take(2)
                                playerTextState = digitsOnly
                                val parsed = digitsOnly.toIntOrNull()
                                if (parsed != null && parsed in GameLimits.MIN_PLAYERS..GameLimits.MAX_PLAYERS) {
                                    onPlayerCountChange(parsed)
                                }
                            },
                            textStyle = TextStyle(
                                color = SpyText,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    val parsed = playerTextState.toIntOrNull()
                                    val clamped = (parsed ?: playerCount).coerceIn(GameLimits.MIN_PLAYERS, GameLimits.MAX_PLAYERS)
                                    playerTextState = clamped.toString()
                                    onPlayerCountChange(clamped)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .testTag("player_count_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SpyPanel2,
                                unfocusedContainerColor = SpyPanel2,
                                focusedBorderColor = SpyAccent,
                                unfocusedBorderColor = SpyLine,
                                focusedTextColor = SpyText,
                                unfocusedTextColor = SpyText
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = getPlayerNoun(playerCount),
                            color = SpyMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Plus button
                    FilledIconButton(
                        onClick = {
                            if (playerCount < GameLimits.MAX_PLAYERS) {
                                val next = playerCount + 1
                                playerTextState = next.toString()
                                onPlayerCountChange(next)
                            }
                        },
                        enabled = playerCount < GameLimits.MAX_PLAYERS,
                        shape = RoundedCornerShape(14.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = SpyAccent,
                            contentColor = SpyAccentInk,
                            disabledContainerColor = SpyBg2.copy(alpha = 0.5f),
                            disabledContentColor = SpyMuted.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .size(54.dp)
                            .testTag("player_count_plus")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Больше игроков", modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Smooth Slider for Players
                Slider(
                    value = playerCount.toFloat(),
                    onValueChange = { value ->
                        val rounded = value.toInt()
                        playerTextState = rounded.toString()
                        onPlayerCountChange(rounded)
                    },
                    valueRange = GameLimits.MIN_PLAYERS.toFloat()..GameLimits.MAX_PLAYERS.toFloat(),
                    steps = GameLimits.MAX_PLAYERS - GameLimits.MIN_PLAYERS - 1,
                    colors = SliderDefaults.colors(
                        thumbColor = SpyAccent,
                        activeTrackColor = SpyAccent,
                        inactiveTrackColor = SpyPanel2
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .testTag("player_count_slider")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Quick Select Chips (3..10)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items((GameLimits.MIN_PLAYERS..GameLimits.MAX_PLAYERS).toList()) { count ->
                        val isSelected = count == playerCount
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) SpyAccent else SpyPanel2,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) SpyAccent else SpyLine
                            ),
                            modifier = Modifier
                                .clickable {
                                    playerTextState = count.toString()
                                    onPlayerCountChange(count)
                                }
                                .testTag("player_chip_$count")
                        ) {
                            Text(
                                text = "$count",
                                color = if (isSelected) SpyAccentInk else SpyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- Card 2: NUMBER OF SPIES INPUT (Direct Input + Stepper + Slider + Chips) ---
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SpyPanel,
            border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("spy_count_section")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🕵️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "КОЛИЧЕСТВО ШПИОНОВ",
                            color = SpyMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    // Recommended badge (tap to apply)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SpyAmber.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SpyAmber.copy(alpha = 0.4f)),
                        modifier = Modifier.clickable {
                            spyTextState = recommendedSpies.toString()
                            onSpyCountChange(recommendedSpies)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Рекомендуется: $recommendedSpies",
                                color = SpyAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Stepper + Direct Editable Text Input for Spies
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Minus button
                    FilledIconButton(
                        onClick = {
                            if (spyCount > 1) {
                                val next = spyCount - 1
                                spyTextState = next.toString()
                                onSpyCountChange(next)
                            }
                        },
                        enabled = spyCount > 1,
                        shape = RoundedCornerShape(14.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = SpyPanel2,
                            contentColor = SpyText,
                            disabledContainerColor = SpyBg2.copy(alpha = 0.5f),
                            disabledContentColor = SpyMuted.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .size(54.dp)
                            .testTag("spy_count_minus")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Меньше шпионов", modifier = Modifier.size(24.dp))
                    }

                    // Direct Editable Text Input for Spy Count
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(130.dp)
                    ) {
                        OutlinedTextField(
                            value = spyTextState,
                            onValueChange = { input ->
                                val digitsOnly = input.filter { it.isDigit() }.take(2)
                                spyTextState = digitsOnly
                                val parsed = digitsOnly.toIntOrNull()
                                if (parsed != null && parsed in 1..maxSpiesAllowed) {
                                    onSpyCountChange(parsed)
                                }
                            },
                            textStyle = TextStyle(
                                color = SpyAmber,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    val parsed = spyTextState.toIntOrNull()
                                    val clamped = (parsed ?: spyCount).coerceIn(1, maxSpiesAllowed)
                                    spyTextState = clamped.toString()
                                    onSpyCountChange(clamped)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .testTag("spy_count_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SpyPanel2,
                                unfocusedContainerColor = SpyPanel2,
                                focusedBorderColor = SpyAmber,
                                unfocusedBorderColor = SpyLine,
                                focusedTextColor = SpyAmber,
                                unfocusedTextColor = SpyAmber
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = getSpyNoun(spyCount),
                            color = SpyAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Plus button
                    FilledIconButton(
                        onClick = {
                            if (spyCount < maxSpiesAllowed) {
                                val next = spyCount + 1
                                spyTextState = next.toString()
                                onSpyCountChange(next)
                            }
                        },
                        enabled = spyCount < maxSpiesAllowed,
                        shape = RoundedCornerShape(14.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = SpyAmber,
                            contentColor = SpyAccentInk,
                            disabledContainerColor = SpyBg2.copy(alpha = 0.5f),
                            disabledContentColor = SpyMuted.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .size(54.dp)
                            .testTag("spy_count_plus")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Больше шпионов", modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Smooth Slider for Spies (if range > 1)
                if (maxSpiesAllowed > 1) {
                    Slider(
                        value = spyCount.toFloat(),
                        onValueChange = { value ->
                            val rounded = value.toInt()
                            spyTextState = rounded.toString()
                            onSpyCountChange(rounded)
                        },
                        valueRange = 1f..maxSpiesAllowed.toFloat(),
                        steps = (maxSpiesAllowed - 2).coerceAtLeast(0),
                        colors = SliderDefaults.colors(
                            thumbColor = SpyAmber,
                            activeTrackColor = SpyAmber,
                            inactiveTrackColor = SpyPanel2
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .testTag("spy_count_slider")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Spies Quick Select Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (count in 1..maxSpiesAllowed) {
                        val isSelected = count == spyCount
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) SpyAmber else SpyPanel2,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) SpyAmber else SpyLine
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    spyTextState = count.toString()
                                    onSpyCountChange(count)
                                }
                                .testTag("spy_chip_$count")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$count ${getSpyNoun(count)}",
                                    color = if (isSelected) SpyAccentInk else SpyText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live Team Balance Breakdown & Proportion Bar
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SpyPanel2,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👥 $civilianCount мирных", color = SpyOk, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = " • ", color = SpyMuted, fontSize = 13.sp)
                                Text(text = "🕵️ $spyCount шпион${if (spyCount > 1) "а" else ""}", color = SpyAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            val percent = ((spyCount.toFloat() / playerCount.toFloat()) * 100).toInt()
                            Text(
                                text = "$percent% шпионов",
                                color = SpyMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Visual proportion bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(SpyBg2)
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                Box(
                                    modifier = Modifier
                                        .weight(civilianCount.toFloat())
                                        .fillMaxHeight()
                                        .background(SpyOk)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(spyCount.toFloat())
                                        .fillMaxHeight()
                                        .background(SpyAmber)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Card 3: ROUND DURATION QUICK SELECTOR ---
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SpyPanel,
            border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = SpyAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ВРЕМЯ РАУНДА",
                            color = SpyMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = ConfigOptions.roundTimeLabel(roundTimeSec),
                        color = SpyAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ConfigOptions.roundTimeOptions) { sec ->
                        val isSelected = sec == roundTimeSec
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) SpyAccent else SpyPanel2,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) SpyAccent else SpyLine
                            ),
                            modifier = Modifier
                                .clickable { onRoundTimeChange(sec) }
                                .testTag("time_chip_$sec")
                        ) {
                            Text(
                                text = ConfigOptions.roundTimeLabel(sec),
                                color = if (isSelected) SpyAccentInk else SpyText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- Primary Action Buttons ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Main Big CTA: Start Game / Reveal Roles
            Button(
                onClick = onStartGameDirect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("start_game_button"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SpyAccent,
                    contentColor = SpyAccentInk
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "РАЗДАТЬ РОЛИ И НАЧАТЬ",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Customize Players in Lobby Button
            OutlinedButton(
                onClick = onOpenLobby,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("open_lobby_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SpyPanel2,
                    contentColor = SpyText
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = SpyMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Настроить имена и аватары",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // --- Quick Utility Navigation Bar (Packs, Rules, Settings) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Packs
            OutlinedButton(
                onClick = onOpenPacks,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("menu_packs_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SpyPanel,
                    contentColor = SpyText
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.Public, contentDescription = null, tint = SpyOk, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Локации", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            // Rules
            OutlinedButton(
                onClick = onOpenRules,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("menu_rules_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SpyPanel,
                    contentColor = SpyText
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = SpyMuted, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Правила", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            // Settings
            OutlinedButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("menu_settings_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SpyPanel,
                    contentColor = SpyText
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = SpyAmber, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Опции", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

private fun getPlayerNoun(count: Int): String {
    val remainder10 = count % 10
    val remainder100 = count % 100
    return when {
        remainder100 in 11..19 -> "игроков"
        remainder10 == 1 -> "игрок"
        remainder10 in 2..4 -> "игрока"
        else -> "игроков"
    }
}

private fun getSpyNoun(count: Int): String {
    val remainder10 = count % 10
    val remainder100 = count % 100
    return when {
        remainder100 in 11..19 -> "шпионов"
        remainder10 == 1 -> "шпион"
        remainder10 in 2..4 -> "шпиона"
        else -> "шпионов"
    }
}
