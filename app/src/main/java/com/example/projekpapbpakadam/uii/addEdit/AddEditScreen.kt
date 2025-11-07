package com.example.projekpapbpakadam.uii.addEdit

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projekpapbpakadam.core.camera.CameraUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    navController: NavController,
    vm: AddEditViewModel
) {
    val context = LocalContext.current
    var photoPreview by remember { mutableStateOf<Uri?>(vm.state.photoLocalPath?.let(Uri::parse)) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            photoPreview = null
            vm.setPhotoPath(null)
        } else {
            Toast.makeText(context, "Foto tersimpan", Toast.LENGTH_SHORT).show()
        }
    }

    val launcherPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = CameraUtils.createImageUri(context)
            photoPreview = uri
            vm.setPhotoPath(uri?.toString())
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Pengeluaran") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.save { navController.popBackStack() } }) {
                Text("Simpan")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = vm.state.title,
                onValueChange = vm::setTitle,
                label = { Text("Judul") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = vm.state.amount,
                onValueChange = vm::setAmount,
                label = { Text("Nominal (Rp)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { launcherPermission.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) { Text("📸 Ambil Foto Struk") }

            val ui = vm.state
            if (ui.photoLocalPath != null) {
                val uri = remember(ui.photoLocalPath) { Uri.parse(ui.photoLocalPath) }
                val fileName = uri.lastPathSegment ?: "foto_struk.jpg"

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Foto terlampir", style = MaterialTheme.typography.titleSmall)
                            Text(fileName, style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = {
                            vm.setPhotoPath(null)
                            photoPreview = null
                        }) { Text("Hapus") }
                    }
                }
            }
            photoPreview?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Foto Struk",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
