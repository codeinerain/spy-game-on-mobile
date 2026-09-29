package com.example.spygame.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.ui.theme.*

@Composable
fun TimerDisplay(
    roundSecondsRemaining: Int,
    totalRoundSeconds: Int,
    turnSecondsRemaining: Int,
    totalTurnSeconds: Int,
    modifier: Modifier = Modifier
) {
    val isRoundLow = totalRoundSeconds > 0 && roundSecondsRemaining <= 30
    val isTurnLow = totalTurnSeconds > 0 && turnSecondsRemaining <= 10

    val roundTimerColor by animateColorAsState(
        targetValue = if (isRoundLow) SpyDanger else SpyText,
        label = "roundTimerColor"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("timer_display"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Round Timer
            Column {
                Text(
                    text = "РАУНД",
                    color = SpyMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = formatMinutes(roundSecondsRemaining, totalRoundSeconds),
                    color = roundTimerColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Turn Timer
            if (totalTurnSeconds > 0) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ХОД",
                        color = SpyMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${turnSecondsRemaining}с",
                        color = if (isTurnLow) SpyDanger else SpyAmber,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        if (totalTurnSeconds > 0) {
            Spacer(modifier = Modifier.height(6.dp))
            val turnFraction = (turnSecondsRemaining.toFloat() / totalTurnSeconds.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { turnFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (isTurnLow) SpyDanger else SpyAccent,
                trackColor = SpyBg2
            )
        }
    }
}

private fun formatMinutes(secondsRemaining: Int, totalSeconds: Int): String {
    if (totalSeconds <= 0) return "∞"
    val mins = secondsRemaining / 60
    val secs = secondsRemaining % 60
    return String.format("%02d:%02d", mins, secs)
}
