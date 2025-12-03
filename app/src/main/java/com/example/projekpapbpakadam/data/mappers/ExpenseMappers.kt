package com.example.projekpapbpakadam.data.mappers

import com.example.projekpapbpakadam.data.local.ExpenseEntity
import com.example.projekpapbpakadam.data.remote.ExpenseRemoteDto

fun ExpenseEntity.toRemote(): ExpenseRemoteDto =
    ExpenseRemoteDto(
        id = id,
        title = title,
        amount = amount,
        category = category,
        type = type,
        dateEpochMillis = dateEpochMillis,
        latitude = latitude,
        longitude = longitude,
        photoUrl = remotePhotoUrl,
        updatedAt = updatedAt
    )

fun ExpenseRemoteDto.toEntity(localPhotoPath: String? = null): ExpenseEntity =
    ExpenseEntity(
        id = id,
        title = title,
        amount = amount,
        category = category,
        type = type,
        dateEpochMillis = dateEpochMillis,
        latitude = latitude,
        longitude = longitude,
        localPhotoPath = localPhotoPath,
        remotePhotoUrl = photoUrl,
        synced = true,
        updatedAt = updatedAt
    )
