package com.example.projekpapbpakadam.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationUtils {

    @SuppressLint("MissingPermission")
    fun getCurrentLocationOnce(
        context: Context,
        onResult: (Location?) -> Unit
    ) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { loc -> onResult(loc) }
            .addOnFailureListener { onResult(null) }
    }
}