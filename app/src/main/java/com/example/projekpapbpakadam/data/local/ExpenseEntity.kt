package com.example.projekpapbpakadam.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val amount: Long,
    val category: String,
    val dateEpochMillis: Long,
    val latitude: Double?,
    val longitude: Double?,
    val localPhotoPath: String?,       // path file lokal
    val remotePhotoUrl: String?,       // URL di Firebase Storage
    val synced: Boolean,               // sudah tersinkron?
    val updatedAt: Long                // epoch millis untuk konflik
)
