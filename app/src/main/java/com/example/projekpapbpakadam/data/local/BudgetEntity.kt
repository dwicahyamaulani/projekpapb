package com.example.projekpapbpakadam.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class BudgetEntity(
    @PrimaryKey
    val monthKey: String,
    val amount: Long
)
