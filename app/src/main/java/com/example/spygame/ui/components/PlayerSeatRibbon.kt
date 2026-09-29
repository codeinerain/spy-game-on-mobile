package com.example.spygame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.model.Player
import com.example.spygame.ui.theme.*

@Composable
fun PlayerSeatRibbon(
    players: List<Player>,
    currentAskerId: String?,
    currentTargetId: String?,
    onPlayerClick: ((Player) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("player_seat_ribbon"),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
    ) {
        items(players, key = { it.id }) { player ->
            val isAsker = player.id == currentAskerId
            val isTarget = player.id == currentTargetId
            val isOut = player.exposed || player.left

            val borderColor = when {
                isAsker -> SpyAmber
                isTarget -> SpyAccent
                else -> Color.Transparent
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(68.dp)
                    .clickable(enabled = onPlayerClick != null && !isOut) {
                        onPlayerClick?.invoke(player)
                    }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isOut) SpyBg2.copy(alpha = 0.5f) else SpyPanel2)
                        .border(
                            width = if (isAsker || isTarget) 3.dp else 1.dp,
                            color = if (isAsker || isTarget) borderColor else SpyLine,
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = player.avatar,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = player.name,
                    color = when {
                        isOut -> SpyMuted.copy(alpha = 0.5f)
                        isAsker -> SpyAmber
                        isTarget -> SpyAccent
                        else -> SpyText
                    },
                    fontSize = 12.sp,
                    fontWeight = if (isAsker || isTarget) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (isAsker) {
                    Text(
                        text = "Спрашивает",
                        color = SpyAmber,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (isTarget) {
                    Text(
                        text = "Отвечает",
                        color = SpyAccent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
