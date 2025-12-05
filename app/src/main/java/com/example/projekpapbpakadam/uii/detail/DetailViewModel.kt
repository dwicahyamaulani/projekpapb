package com.example.projekpapbpakadam.uii.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekpapbpakadam.data.local.ExpenseEntity
import com.example.projekpapbpakadam.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class DetailState(
    val item: ExpenseEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class DetailViewModel(
    private val repo: ExpenseRepository
) : ViewModel() {

    var state by mutableStateOf(DetailState())
        private set

    fun load(id: String) {
        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true, error = null)

                // Ambil semua data, lalu cari yang id-nya cocok
                val list = repo.observeAll().first()
                val found = list.find { it.id == id }

                state = state.copy(
                    item = found,
                    isLoading = false
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal memuat data"
                )
            }
        }
    }
}
