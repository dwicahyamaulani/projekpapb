package com.example.projekpapbpakadam.uii.addEdit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekpapbpakadam.data.local.ExpenseEntity
import com.example.projekpapbpakadam.data.repository.ExpenseRepository
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
enum class TransactionType { INCOME, EXPENSE }
data class AddEditState(
    val title: String = "",
    val amount: String = "",
    val photoLocalPath: String? = null,
    val type: TransactionType = TransactionType.EXPENSE,
    val dateMillis: Long = System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val category: String = "Others"
)

class AddEditViewModel(private val repo: ExpenseRepository) : ViewModel() {
        var state by mutableStateOf(AddEditState())
            private set

        fun setType(t: TransactionType) { state = state.copy(type = t) }
        var currentId: String? = null

        fun setTitle(v: String) { state = state.copy(title = v) }
        fun setAmount(v: String) { state = state.copy(amount = v) }
        fun setPhotoPath(p: String?) { state = state.copy(photoLocalPath = p) }

        fun setDate(millis: Long) { state = state.copy(dateMillis = millis) }

        fun setLocation(lat: Double?, lon: Double?) {
            state = state.copy(latitude = lat, longitude = lon)
        }

        fun setCategory(c: String) { state = state.copy(category = c) }
    fun save(onDone: () -> Unit) = viewModelScope.launch {
        val id = currentId ?: UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val entity = ExpenseEntity(
            id = id,
            title = state.title,
            amount = state.amount.toLongOrNull() ?: 0L,
            category = state.category,
            type = state.type.name,
            dateEpochMillis = state.dateMillis,      // ⬅️ pakai tanggal pilihan
            latitude = state.latitude,               // ⬅️ simpan lokasi
            longitude = state.longitude,
            localPhotoPath = state.photoLocalPath,
            remotePhotoUrl = null,
            synced = false,
            updatedAt = now
        )
        repo.addOrUpdate(entity)
        onDone()
    }

    fun load(id: String) {
        viewModelScope.launch {
            val data = repo.get(id)
            if (data != null) {
                currentId = data.id
                state = state.copy(
                    title = data.title,
                    amount = data.amount.toString(),
                    photoLocalPath = data.localPhotoPath,
                    type = when (data.type) {
                        "INCOME" -> TransactionType.INCOME
                        else -> TransactionType.EXPENSE
                    },
                    dateMillis = data.dateEpochMillis,
                    latitude = data.latitude,
                    longitude = data.longitude,
                    category = data.category
                )
            }
        }
    }
}

