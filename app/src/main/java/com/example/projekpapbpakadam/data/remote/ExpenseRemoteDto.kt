package com.example.projekpapbpakadam.data.remote
data class ExpenseRemoteDto(
    val id: String = "",
    val title: String = "",
    val amount: Long = 0,
    val category: String = "",
    val type: String = "EXPENSE",
    val dateEpochMillis: Long = 0,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val photoUrl: String? = null,
    val updatedAt: Long = 0
)

