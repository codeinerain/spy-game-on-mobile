package com.example.spygame.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.example.spygame.model.LocationItem
import com.example.spygame.model.Player
import com.example.spygame.model.Role
import com.example.spygame.ui.components.DossierCard
import com.example.spygame.ui.theme.*

@Composable
fun RoleRevealScreen(
    players: List<Player>, // Only human players should be passed here!
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
    val isSoloMode = players.size == 1

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
            // Player handoff confidentiality banner
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (isRevealed) SpyPanel else SpyPanel2,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isRevealed) SpyLine else SpyAmber.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isSoloMode) {
                        Text(
                            text = "ОДИНОЧНЫЙ РАУНД С БОТАМИ",
                            color = SpyAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ваше секретное досье",
                            color = SpyText,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Роли ботов строго засекречены и не раскрываются!",
                            color = SpyMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = SpyAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ИГРОК ${currentIndex + 1} ИЗ ${players.size}",
                                color = SpyAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isRevealed) "Досье игрока:" else "Передайте телефон игроку:",
                            color = SpyMuted,
                            fontSize = 14.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = currentPlayer.avatar,
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentPlayer.name,
                                color = SpyText,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (!isRevealed) {
                                "⚠️ Убедитесь, что никто не смотрит в экран перед открытием!"
                            } else {
                                "Никому не показывайте экран и не называйте свою роль!"
                            },
                            color = if (!isRevealed) SpyDanger else SpyMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // The secret dossier card
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

        // Action Buttons: Open Dossier or Complete Step
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!isRevealed) {
                // Button to securely open dossier when device is in the right hands
                Button(
                    onClick = onToggleReveal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("role_reveal_button"),
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
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSoloMode) {
                                "Открыть моё досье"
                            } else {
                                "Я ${currentPlayer.name} — открыть досье"
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                // Button when dossier is read: hide and advance to next player or start game
                Button(
                    onClick = onNextPlayer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("role_ack_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLastPlayer) SpyOk else SpyAccent,
                        contentColor = SpyAccentInk
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isLastPlayer) {
                                "Я всё понял(а) — начать игру!"
                            } else {
                                "Скрыть и передать следующему"
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (isLastPlayer) Icons.Default.PlayArrow else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
