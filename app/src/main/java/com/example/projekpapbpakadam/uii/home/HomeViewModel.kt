package com.example.projekpapbpakadam.uii.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekpapbpakadam.data.local.ExpenseEntity
import com.example.projekpapbpakadam.data.repository.BudgetRepository
import com.example.projekpapbpakadam.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeState(
    val items: List<ExpenseEntity> = emptyList(),
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val expenseByCategory: Map<String, Long> = emptyMap(),
    val loading: Boolean = false,
    val error: String? = null,
    val showBudgetDialog: Boolean = false,
    val monthlyBudget: Long = 0L
)

class HomeViewModel(private val repo: ExpenseRepository, private val budgetRepo: BudgetRepository) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        observeExpenses()
        observeBudget()
    }

    private fun observeBudget() {
        budgetRepo.observeCurrentMonth()
            .onEach { budget ->
                _state.update {
                    it.copy(monthlyBudget = budget)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeExpenses() {
        repo.observeAll()
            .onEach { list ->

                val now = Calendar.getInstance()
                val month = now.get(Calendar.MONTH)
                val year = now.get(Calendar.YEAR)

                fun isThisMonth(epoch: Long): Boolean {
                    val c = Calendar.getInstance()
                    c.timeInMillis = epoch
                    return c.get(Calendar.MONTH) == month &&
                            c.get(Calendar.YEAR) == year
                }

                val monthly = list.filter { isThisMonth(it.dateEpochMillis) }

                val incomeTotal = monthly
                    .filter { it.type == "INCOME" }
                    .sumOf { it.amount }

                val expenseList = monthly.filter { it.type == "EXPENSE" }
                val expenseTotal = expenseList.sumOf { it.amount }

                val byCategory = expenseList
                    .groupBy { it.category.ifBlank { "Others" } }
                    .mapValues { (_, items) -> items.sumOf { it.amount } }

                _state.update {
                    it.copy(
                        items = monthly, // ⬅ INI FIX PENTING
                        totalIncome = incomeTotal,
                        totalExpense = expenseTotal,
                        expenseByCategory = byCategory,
                        loading = false,
                        error = null
                    )
                }
            }
            .catch { err ->
                _state.update { it.copy(error = err.message, loading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun sync() = viewModelScope.launch {
        _state.update { it.copy(loading = true) }
        runCatching { repo.sync() }
            .onSuccess {
                _state.update { it.copy(loading = false) }
            }
            .onFailure { e ->
                _state.update { it.copy(error = e.message, loading = false) }
            }
    }

    fun delete(id: String) = viewModelScope.launch {
        repo.delete(id)
    }

    // Control dialog UI
    fun setBudgetDialog(show: Boolean) {
        _state.update { it.copy(showBudgetDialog = show) }
    }

    fun setBudget(amount: Long) = viewModelScope.launch {
        budgetRepo.setBudget(amount)
    }
}
