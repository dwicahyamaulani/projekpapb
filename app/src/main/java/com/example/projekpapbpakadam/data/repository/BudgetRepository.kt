package com.example.projekpapbpakadam.data.repository

import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun observeCurrentMonth(): Flow<Long>
    suspend fun setBudget(amount: Long)
}
