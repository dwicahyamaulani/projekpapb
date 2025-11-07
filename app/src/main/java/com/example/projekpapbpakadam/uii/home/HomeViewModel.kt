package com.example.projekpapbpakadam.uii.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekpapbpakadam.data.local.ExpenseEntity
import com.example.projekpapbpakadam.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeState(
    val items: List<ExpenseEntity> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(private val repo: ExpenseRepository) : ViewModel() {
    val state: StateFlow<HomeState> =
        repo.observeAll()
            .map { HomeState(items = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeState())

    fun sync() = viewModelScope.launch { runCatching { repo.sync() } }
    fun delete(id: String) = viewModelScope.launch { repo.delete(id) }
}
