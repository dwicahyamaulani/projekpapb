package com.example.projekpapbpakadam.uii.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekpapbpakadam.data.local.ExpenseEntity
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
    val error: String? = null
)

class HomeViewModel(private val repo: ExpenseRepository) : ViewModel() {

    val state: StateFlow<HomeState> =
        repo.observeAll()
            .map { list ->
                // 🔹 hitung hanya untuk BULAN & TAHUN SEKARANG
                val nowCal = Calendar.getInstance()
                val curMonth = nowCal.get(Calendar.MONTH)
                val curYear = nowCal.get(Calendar.YEAR)

                fun isThisMonth(epochMillis: Long): Boolean {
                    val c = Calendar.getInstance()
                    c.timeInMillis = epochMillis
                    return c.get(Calendar.MONTH) == curMonth &&
                            c.get(Calendar.YEAR) == curYear
                }

                val monthly = list.filter { isThisMonth(it.dateEpochMillis) }

                val income = monthly
                    .filter { it.type == "INCOME" }
                    .sumOf { it.amount }

                val expenseList = monthly.filter { it.type == "EXPENSE" }
                val expenseTotal = expenseList.sumOf { it.amount }

                val byCategory = expenseList
                    .groupBy { it.category.ifBlank { "Others" } }
                    .mapValues { (_, items) -> items.sumOf { it.amount } }

                HomeState(
                    items = list,
                    totalIncome = income,
                    totalExpense = expenseTotal,
                    expenseByCategory = byCategory
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                HomeState()
            )

    fun sync() = viewModelScope.launch { runCatching { repo.sync() } }
    fun delete(id: String) = viewModelScope.launch { repo.delete(id) }
}