package com.example.spygame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.ui.theme.*

@Composable
fun MenuScreen(
    onStartPassAndPlay: () -> Unit,
    onStartSoloPractice: () -> Unit,
    onOpenRules: () -> Unit,
    onOpenPacks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SpyBg2, SpyBg)
                )
            )
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Espionage Badge / Icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(SpyPanel)
                    .border(2.dp, SpyAmber, CircleShape)
            ) {
                Text(
                    text = "🕵️‍♂️",
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ШПИОН",
                color = SpyText,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            // Redacted bar decoration
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(6.dp)
                    .background(SpyAmber, RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Психологическая детективная игра\nдля вечеринок (3–10 игроков)",
                color = SpyMuted,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Mode 1: Pass & Play (Main Action)
            Button(
                onClick = onStartPassAndPlay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .testTag("start_pass_and_play_button"),
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
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Игра в компании (Pass & Play)",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode 2: Solo Practice with Bots
            OutlinedButton(
                onClick = onStartSoloPractice,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_solo_practice_button"),
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
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = SpyAmber,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Одиночный режим (с ботами)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action 3: Location Packs
            OutlinedButton(
                onClick = onOpenPacks,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("open_packs_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SpyPanel,
                    contentColor = SpyText
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = SpyOk,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Наборы локаций и категории",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action 4: Rules
            OutlinedButton(
                onClick = onOpenRules,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("open_rules_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SpyPanel,
                    contentColor = SpyText
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = SpyMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Правила игры",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Footer info
        Text(
            text = "Без интернета • Все локации включены • 3–10 игроков",
            color = SpyMuted.copy(alpha = 0.7f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}
