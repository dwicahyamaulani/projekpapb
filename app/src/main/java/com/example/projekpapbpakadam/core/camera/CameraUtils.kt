package com.example.projekpapbpakadam.core.camera

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object CameraUtils {
    fun createImageUri(context: Context): Uri {
        val imageFile = File.createTempFile(
            "photo_", ".jpg",
            context.getExternalFilesDir(null)
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }
}
