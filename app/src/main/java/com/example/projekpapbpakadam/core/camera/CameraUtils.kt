package com.example.projekpapbpakadam.core.camera

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.camera.lifecycle.ProcessCameraProvider

object CameraUtils {

    fun createImageFile(context: Context): File =
        File.createTempFile(
            "photo_", ".jpg",
            context.getExternalFilesDir(null)
        )

    fun createImageUri(context: Context): Uri {
        val imageFile = createImageFile(context)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }
}

suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { cont ->
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener(
            { cont.resume(future.get()) },
            ContextCompat.getMainExecutor(this)
        )
    }
