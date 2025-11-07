package com.example.projekpapbpakadam.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY dateEpochMillis DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses ORDER BY dateEpochMillis DESC")
    suspend fun getAll(): List<ExpenseEntity>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ExpenseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    // Cari di title / category (LOWER untuk case-insensitive)
    @Query("""
        SELECT * FROM expenses 
        WHERE LOWER(title) LIKE LOWER('%' || :query || '%') 
           OR LOWER(category) LIKE LOWER('%' || :query || '%')
        ORDER BY dateEpochMillis DESC
    """)
    suspend fun search(query: String): List<ExpenseEntity>

    // Ambil berdasarkan status sinkron
    @Query("SELECT * FROM expenses WHERE synced = :synced ORDER BY dateEpochMillis DESC")
    suspend fun getBySyncStatus(synced: Boolean): List<ExpenseEntity>

    // (Opsional) tetap sediakan util lama jika dipakai di tempat lain
    @Query("SELECT * FROM expenses WHERE synced = 0")
    suspend fun getPendingSync(): List<ExpenseEntity>


}
