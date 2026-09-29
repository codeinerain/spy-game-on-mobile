package com.example.spygame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spygame.model.LocationCategory
import com.example.spygame.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacksScreen(
    categories: List<LocationCategory>,
    enabledCategoryIds: Set<String>,
    onToggleCategory: (String) -> Unit,
    onAddCustomLocation: (categoryId: String, name: String, synonyms: List<String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "") }
    var newLocationName by remember { mutableStateOf("") }
    var newLocationSynonyms by remember { mutableStateOf("") }

    val totalLocations = remember(categories) { categories.sumOf { it.items.size } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Наборы локаций", fontWeight = FontWeight.Bold, color = SpyText) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = SpyText)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить локацию", tint = SpyAccent)
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
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SpyPanel,
                border = androidx.compose.foundation.BorderStroke(1.dp, SpyLine),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Классический набор",
                            color = SpyText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Категорий: ${categories.size} • Локаций: $totalLocations",
                            color = SpyMuted,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "Включено: ${if (enabledCategoryIds.isEmpty()) "Все" else "${enabledCategoryIds.size}"}",
                        color = SpyAmber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "КАТЕГОРИИ (нажмите, чтобы включить/выключить)",
                color = SpyMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories, key = { it.id }) { cat ->
                    val isEnabled = enabledCategoryIds.isEmpty() || cat.id in enabledCategoryIds

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isEnabled) SpyPanel else SpyBg2,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isEnabled) SpyAccent.copy(alpha = 0.6f) else SpyLine
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleCategory(cat.id) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = cat.icon, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = cat.name,
                                        color = if (isEnabled) SpyText else SpyMuted,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "(${cat.items.size})",
                                        color = SpyMuted,
                                        fontSize = 13.sp
                                    )
                                }

                                Checkbox(
                                    checked = isEnabled,
                                    onCheckedChange = { onToggleCategory(cat.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = SpyAccent,
                                        checkmarkColor = SpyAccentInk,
                                        uncheckedColor = SpyLine
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = cat.items.take(8).joinToString(", ") { it.name } + if (cat.items.size > 8) "..." else "",
                                color = SpyMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Добавить локацию", color = SpyText, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newLocationName,
                        onValueChange = { newLocationName = it },
                        label = { Text("Название локации") },
                        placeholder = { Text("Например: Аквапарк") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SpyBg2,
                            unfocusedContainerColor = SpyBg2,
                            focusedBorderColor = SpyAccent,
                            unfocusedBorderColor = SpyLine,
                            focusedTextColor = SpyText,
                            unfocusedTextColor = SpyText
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newLocationSynonyms,
                        onValueChange = { newLocationSynonyms = it },
                        label = { Text("Синонимы (через запятую)") },
                        placeholder = { Text("водный парк, водные горки") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SpyBg2,
                            unfocusedContainerColor = SpyBg2,
                            focusedBorderColor = SpyAccent,
                            unfocusedBorderColor = SpyLine,
                            focusedTextColor = SpyText,
                            unfocusedTextColor = SpyText
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newLocationName.isNotBlank() && selectedCategoryId.isNotBlank()) {
                            val synonyms = newLocationSynonyms.split(",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() }
                            onAddCustomLocation(selectedCategoryId, newLocationName.trim(), synonyms)
                            newLocationName = ""
                            newLocationSynonyms = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SpyAccent, contentColor = SpyAccentInk)
                ) {
                    Text("Сохранить", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Отмена", color = SpyMuted)
                }
            },
            containerColor = SpyPanel,
            shape = RoundedCornerShape(18.dp)
        )
    }
}
