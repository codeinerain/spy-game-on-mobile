package com.example.spygame.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.model.Role
import com.example.spygame.ui.theme.*

@Composable
fun DossierCard(
    playerName: String,
    playerAvatar: String,
    role: Role?,
    locationName: String?,
    spyCount: Int,
    isRevealed: Boolean,
    onToggleReveal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggleReveal() }
            .testTag("dossier_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SpyPanel),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isRevealed && role == Role.SPY) SpyAmber else SpyLine)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // "TOP SECRET" Stamp
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .rotate(8f),
                color = Color.Transparent,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, SpyAmber.copy(alpha = 0.85f))
            ) {
                Text(
                    text = "СОВЕРШЕННО СЕКРЕТНО",
                    color = SpyAmber,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    letterSpacing = 1.sp
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with avatar
                Text(
                    text = playerAvatar,
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = "ДОСЬЕ: $playerName",
                    color = SpyMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!isRevealed) {
                    // Redacted confidential state
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(16.dp)
                                .background(SpyRedact, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .height(16.dp)
                                .background(SpyRedact, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(16.dp)
                                .background(SpyRedact, RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .background(SpyPanel2, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Показать роль",
                                tint = SpyAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Нажмите, чтобы открыть досье",
                                color = SpyAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    // Revealed state
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (role == Role.SPY) {
                            Text(
                                text = "ВЫ ШПИОН!",
                                color = SpyAmber,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ваша задача — выяснить локацию, не выдав себя, или запутать мирных игроков.",
                                color = SpyText,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = SpyAmber.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SpyAmber.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "Всего шпионов в игре: $spyCount",
                                    color = SpyAmber,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        } else {
                            Text(
                                text = "СЕКРЕТНАЯ ЛОКАЦИЯ",
                                color = SpyMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = locationName ?: "Неизвестно",
                                color = SpyOk,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Вы мирный житель. Задавайте наводящие вопросы, чтобы вычислить шпиона!",
                                color = SpyText,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .background(SpyBg2, RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "Скрыть",
                                tint = SpyMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Нажмите, чтобы скрыть досье",
                                color = SpyMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
