package com.example.projekpapbpakadam.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val amount: Long,
    val category: String,
    val type: String,              // "INCOME" atau "EXPENSE"
    val dateEpochMillis: Long,
    val latitude: Double?,
    val longitude: Double?,
    val localPhotoPath: String?,
    val remotePhotoUrl: String?,
    val synced: Boolean,
    val updatedAt: Long
)
