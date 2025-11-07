package com.example.projekpapbpakadam.data.repository

import com.example.projekpapbpakadam.data.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun observeAll(): Flow<List<ExpenseEntity>>
    suspend fun get(id: String): ExpenseEntity?
    suspend fun addOrUpdate(entity: ExpenseEntity)
    suspend fun delete(id: String)
    suspend fun sync()
}
