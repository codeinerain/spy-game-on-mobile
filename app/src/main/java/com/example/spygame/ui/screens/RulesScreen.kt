package com.example.spygame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Правила игры", fontWeight = FontWeight.Bold, color = SpyText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = SpyText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpyBg)
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
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            RuleCard(
                icon = "🎯",
                title = "Цель игры",
                description = "Игроки делятся на две стороны: Мирные жители и Шпионы.\n\n" +
                        "• Мирные знают секретную локацию (например: «Пляж», «Больница», «Кинотеатр») и пытаются вопросами вычислить, кто среди них шпион.\n" +
                        "• Шпионы не знают локацию. Их задача — слушать ответы других, блефовать, не выдать себя и догадаться, где все находятся!"
            )

            RuleCard(
                icon = "🤫",
                title = "Раздача ролей (Досье)",
                description = "Перед началом раунда телефон передаётся по кругу каждому участнику. Игрок нажимает «Открыть досье», запоминает свою роль и локацию, затем скрывает досье и передаёт телефон следующему."
            )

            RuleCard(
                icon = "❓",
                title = "Вопросы и ответы",
                description = "Игроки по очереди задают друг другу вопросы:\n\n" +
                        "• Текущий игрок выбирает любого собеседника (кроме себя) и спрашивает что-то о локации, например: «Часто ли ты тут бываешь?», «Что ты надеваешь в это место?», «Тут шумно?»\n" +
                        "• Ответивший игрок становится следующим спрашивающим.\n" +
                        "• Вопрос не должен быть слишком очевидным (иначе шпион сразу поймёт локацию), но и не слишком странным (чтобы вас не посчитали шпионом)!"
            )

            RuleCard(
                icon = "🗳️",
                title = "Голосование",
                description = "После завершения круга вопросов игроки могут запустить голосование за подозреваемого в шпионаже. Если игроки единогласно или большинством укажут на шпиона — мирные побеждают! Но если обвинят невинного мирного — шпионы торжествуют!"
            )

            RuleCard(
                icon = "🔍",
                title = "Догадка шпиона",
                description = "В любой момент раунда шпион может нажать кнопку «Назвать локацию». Если он правильно угадывает место — шпион немедленно побеждает! Если ошибается — выбывает из игры или проигрывает."
            )

            RuleCard(
                icon = "🏆",
                title = "Начисление очков",
                description = "• Победа мирных: +1 очко каждому мирному (+2 очка, если шпион пойман голосованием).\n" +
                        "• Победа шпионов: +2 очка каждому шпиону (+3 очка шпиону, который сам правильно назвал локацию)."
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RuleCard(icon: String, title: String, description: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SpyPanel,
        border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = SpyAmber,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = SpyText,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}
