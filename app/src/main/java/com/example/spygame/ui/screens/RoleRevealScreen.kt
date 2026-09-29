package com.example.spygame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.model.LocationItem
import com.example.spygame.model.Player
import com.example.spygame.model.Role
import com.example.spygame.ui.components.DossierCard
import com.example.spygame.ui.theme.*

@Composable
fun RoleRevealScreen(
    players: List<Player>,
    currentIndex: Int,
    currentLocation: LocationItem?,
    spyCount: Int,
    isRevealed: Boolean,
    onToggleReveal: () -> Unit,
    onNextPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentPlayer = players.getOrNull(currentIndex) ?: return
    val isLastPlayer = currentIndex == players.size - 1

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpyBg)
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Player handoff instruction
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SpyPanel2,
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ШАГ ${currentIndex + 1} ИЗ ${players.size}",
                        color = SpyAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Передайте телефон игроку",
                        color = SpyMuted,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${currentPlayer.avatar} ${currentPlayer.name}",
                        color = SpyText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Убедитесь, что никто больше не смотрит в экран!",
                        color = SpyDanger,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // The secret dossier
            DossierCard(
                playerName = currentPlayer.name,
                playerAvatar = currentPlayer.avatar,
                role = currentPlayer.role,
                locationName = currentLocation?.name,
                spyCount = spyCount,
                isRevealed = isRevealed,
                onToggleReveal = onToggleReveal
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Next player or start game button
        Button(
            onClick = onNextPlayer,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .testTag("role_ack_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SpyAccent,
                contentColor = SpyAccentInk
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isLastPlayer) "Все посмотрели — начать игру!" else "Скрыть и передать следующему",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
