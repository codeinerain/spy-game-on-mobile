package com.example.spygame.data

import com.example.spygame.model.DefaultLocationsPack
import com.example.spygame.model.LocationCategory
import com.example.spygame.model.LocationItem

class LocationRepository {
    private val _categories = mutableListOf<LocationCategory>()

    init {
        _categories.addAll(DefaultLocationsPack.categories)
    }

    fun getAllCategories(): List<LocationCategory> = _categories.toList()

    fun getCategory(id: String): LocationCategory? = _categories.find { it.id == id }

    fun getPool(enabledCategoryIds: Set<String>): List<LocationItem> {
        val activeCategories = if (enabledCategoryIds.isEmpty()) {
            _categories
        } else {
            _categories.filter { it.id in enabledCategoryIds }
        }
        return activeCategories.flatMap { it.items }
    }

    fun getRandomLocation(enabledCategoryIds: Set<String>, exclude: Set<String> = emptySet()): LocationItem? {
        val pool = getPool(enabledCategoryIds).filter { it.name !in exclude }
        if (pool.isEmpty()) {
            val fallback = getPool(enabledCategoryIds)
            return fallback.randomOrNull()
        }
        return pool.randomOrNull()
    }

    fun checkGuess(actualLocation: LocationItem, guess: String): Boolean {
        return actualLocation.matches(guess)
    }

    fun addCustomLocation(categoryId: String, name: String, synonyms: List<String> = emptyList()) {
        val index = _categories.indexOfFirst { it.id == categoryId }
        if (index != -1) {
            val cat = _categories[index]
            val updatedItems = cat.items + LocationItem(name.trim(), synonyms.map { it.trim() })
            _categories[index] = cat.copy(items = updatedItems)
        }
    }
}
