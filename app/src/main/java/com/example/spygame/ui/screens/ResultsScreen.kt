package com.example.spygame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.model.*
import com.example.spygame.ui.theme.*

@Composable
fun ResultsScreen(
    winningTeam: Team?,
    endReason: EndReason?,
    winningDetails: String?,
    secretLocation: LocationItem?,
    players: List<Player>,
    onNextRound: () -> Unit,
    onReturnToLobby: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSpiesWin = winningTeam == Team.SPIES

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpyBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Victory Banner
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = if (isSpiesWin) SpyAmber.copy(alpha = 0.15f) else SpyOk.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(
                2.dp,
                if (isSpiesWin) SpyAmber else SpyOk
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isSpiesWin) "🕵️ ПОБЕДА ШПИОНОВ!" else "🎉 ПОБЕДА МИРНЫХ ЖИТЕЛЕЙ!",
                    color = if (isSpiesWin) SpyAmber else SpyOk,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = winningDetails ?: endReason?.title ?: "Раунд завершён",
                    color = SpyText,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Secret Location Revealed Box
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SpyPanel,
            border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "СЕКРЕТНАЯ ЛОКАЦИЯ РАУНДА",
                    color = SpyMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = secretLocation?.name ?: "—",
                    color = SpyAccent,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Roster & Role Disclosure
        Text(
            text = "РАСКРЫТИЕ РОЛЕЙ И СЧЁТ",
            color = SpyMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(players) { player ->
                val isSpy = player.role == Role.SPY
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SpyPanel,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSpy) SpyAmber.copy(alpha = 0.5f) else SpyLine
                    ),
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
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SpyPanel2)
                            ) {
                                Text(text = player.avatar, fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = player.name,
                                    color = SpyText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSpy) SpyAmber.copy(alpha = 0.2f) else SpyOk.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = if (isSpy) "ШПИОН" else "МИРНЫЙ",
                                        color = if (isSpy) SpyAmber else SpyOk,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Очков: ${player.score}",
                                color = SpyText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onReturnToLobby,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("results_to_lobby_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SpyPanel,
                    contentColor = SpyText
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine)
            ) {
                Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("В лобби", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }

            Button(
                onClick = onNextRound,
                modifier = Modifier
                    .weight(1.3f)
                    .height(54.dp)
                    .testTag("results_next_round_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SpyAccent,
                    contentColor = SpyAccentInk
                )
            ) {
                Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Следующий раунд", fontWeight = FontWeight.Black, fontSize = 15.sp)
            }
        }
    }
}
