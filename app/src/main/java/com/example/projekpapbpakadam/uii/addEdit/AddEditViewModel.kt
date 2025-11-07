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

data class AddEditState(
    val title: String = "",
    val amount: String = "",
    val photoLocalPath: String? = null
)

class AddEditViewModel(private val repo: ExpenseRepository) : ViewModel() {
    var state by mutableStateOf(AddEditState()); private set
    var currentId: String? = null

    fun setTitle(v: String) { state = state.copy(title = v) }
    fun setAmount(v: String) { state = state.copy(amount = v) }
    fun setPhotoPath(p: String?) { state = state.copy(photoLocalPath = p) }

    fun save(onDone: () -> Unit) = viewModelScope.launch {
        val id = currentId ?: UUID.randomUUID().toString()
        val entity = ExpenseEntity(
            id = id,
            title = state.title,
            amount = state.amount.toLongOrNull() ?: 0L,
            category = "General",
            dateEpochMillis = System.currentTimeMillis(),
            latitude = null,
            longitude = null,
            localPhotoPath = state.photoLocalPath,
            remotePhotoUrl = null,
            synced = false,
            updatedAt = System.currentTimeMillis()
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
                    photoLocalPath = data.localPhotoPath
                )
            }
        }
    }
}
