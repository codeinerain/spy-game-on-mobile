package com.example.spygame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.model.GameConfig
import com.example.spygame.model.Player
import com.example.spygame.ui.theme.*

@Composable
fun VotingScreen(
    players: List<Player>,
    config: GameConfig,
    votes: Map<String, String?>,
    voteSecondsRemaining: Int,
    onCastVote: (voterId: String, targetPlayerId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val activePlayers = players.filter { it.isActive }

    // Active voter selection (in Pass & Play, cycle through voters who haven't voted yet)
    val unvotedPlayers = activePlayers.filter { it.id !in votes.keys }
    var selectedVoterId by remember(unvotedPlayers) {
        mutableStateOf(unvotedPlayers.firstOrNull()?.id ?: activePlayers.firstOrNull()?.id ?: "")
    }

    var selectedTargetId by remember { mutableStateOf<String?>(null) }

    val currentVoter = players.find { it.id == selectedVoterId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpyBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ГОЛОСОВАНИЕ",
                    color = SpyAmber,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Режим: ${config.votingMode.title}",
                    color = SpyMuted,
                    fontSize = 13.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SpyPanel2,
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine)
            ) {
                Text(
                    text = "${voteSecondsRemaining}с",
                    color = if (voteSecondsRemaining <= 10) SpyDanger else SpyAmber,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Rule hint
        Text(
            text = config.votingMode.hint,
            color = SpyMuted,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Voter indicator
        if (currentVoter != null) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SpyPanel,
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = currentVoter.avatar, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Голосует: ${currentVoter.name}",
                                color = SpyAccent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Проголосовало: ${votes.size} из ${activePlayers.size}",
                                color = SpyMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "КТО ПО ВАШЕМУ МНЕНИЮ ШПИОН?",
            color = SpyMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Candidates list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(activePlayers) { player ->
                val isSelf = player.id == selectedVoterId
                val isSelected = player.id == selectedTargetId

                // Calculate vote count so far
                val voteCount = votes.values.count { it == player.id }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = when {
                        isSelected -> SpyAccent.copy(alpha = 0.2f)
                        isSelf -> SpyBg2.copy(alpha = 0.5f)
                        else -> SpyPanel
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = when {
                            isSelected -> SpyAccent
                            isSelf -> SpyLine.copy(alpha = 0.5f)
                            else -> SpyLine
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isSelf) {
                            selectedTargetId = player.id
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SpyPanel2)
                            ) {
                                Text(text = player.avatar, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = player.name,
                                    color = if (isSelf) SpyMuted else SpyText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isSelf) {
                                    Text(
                                        text = "Нельзя голосовать за себя",
                                        color = SpyMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Выбран",
                                tint = SpyAccent
                            )
                        } else if (voteCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SpyPanel2
                            ) {
                                Text(
                                    text = "Голосов: $voteCount",
                                    color = SpyAmber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Abstain option
            item {
                val isAbstainSelected = selectedTargetId == null
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isAbstainSelected) SpyPanel2 else SpyBg2,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAbstainSelected) SpyMuted else SpyLine
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedTargetId = null }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "⚪ Воздержаться от обвинения",
                            color = SpyText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (isAbstainSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Выбрано",
                                tint = SpyMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Cast vote button
        Button(
            onClick = {
                if (selectedVoterId.isNotBlank()) {
                    onCastVote(selectedVoterId, selectedTargetId)
                    // Reset selection for next voter
                    selectedTargetId = null
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("cast_vote_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SpyAccent,
                contentColor = SpyAccentInk
            )
        ) {
            Icon(Icons.Default.HowToVote, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Подтвердить голос",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
        }
    }
}
