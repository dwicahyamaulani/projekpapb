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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.projekpapbpakadam.core.location.LocationUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.app.DatePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    navController: NavController,
    vm: AddEditViewModel
) {
    val context = LocalContext.current
    val ui = vm.state

    var photoPreview by remember { mutableStateOf<Uri?>(ui.photoLocalPath?.let(Uri::parse)) }
    var showCamera by remember { mutableStateOf(false) }

    val launcherPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showCamera = true
        } else {
            Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    val calendar = remember { Calendar.getInstance() }

    // Sync calendar dengan state.dateMillis
    LaunchedEffect(ui.dateMillis) {
        calendar.timeInMillis = ui.dateMillis
    }

    val formattedDate = remember(ui.dateMillis) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        sdf.format(Date(ui.dateMillis))
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth, 0, 0, 0)
            vm.setDate(calendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // launcher permission lokasi
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            LocationUtils.getCurrentLocationOnce(context) { loc ->
                if (loc != null) {
                    vm.setLocation(loc.latitude, loc.longitude)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (vm.state.latitude == null || vm.state.longitude == null) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val categories = listOf(
        "Food & Drink",
        "Transport",
        "Bills & Utilities",
        "Shopping",
        "Health",
        "Education",
        "Entertainment",
        "Others"
    )

    var categoryExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (ui.type == TransactionType.EXPENSE)
                            "Tambah Pengeluaran"
                        else
                            "Tambah Pemasukan"
                    )
                },
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

            // Pilih jenis transaksi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = ui.type == TransactionType.EXPENSE,
                    onClick = { vm.setType(TransactionType.EXPENSE) },
                    label = { Text("Pengeluaran") }
                )
                FilterChip(
                    selected = ui.type == TransactionType.INCOME,
                    onClick = { vm.setType(TransactionType.INCOME) },
                    label = { Text("Pemasukan") }
                )
            }

            OutlinedTextField(
                value = ui.title,
                onValueChange = vm::setTitle,
                label = { Text("Nama transaksi") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ui.amount,
                onValueChange = vm::setAmount,
                label = { Text("Nominal (Rp)") },
                modifier = Modifier.fillMaxWidth()
            )

            // 🔻 Dropdown kategori
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = ui.category,
                    onValueChange = { }, // tidak diketik manual, pilih dari menu
                    readOnly = true,
                    label = { Text("Kategori") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = categoryExpanded
                        )
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                vm.setCategory(option)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // 📅 Pilih tanggal transaksi
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tanggal: $formattedDate")
            }

//            // 📍 Ambil lokasi sekali
//            if (ui.type == TransactionType.EXPENSE) {
//                if (ui.latitude != null && ui.longitude != null) {
//                    Text(
//                        text = "Lokasi: ${"%.5f".format(ui.latitude)}, ${"%.5f".format(ui.longitude)}",
//                        style = MaterialTheme.typography.labelSmall
//                    )
//                }
//            }

            // Bagian foto hanya muncul kalau tipe = PENGELUARAN
            if (ui.type == TransactionType.EXPENSE) {
                if (showCamera) {
                    CameraCaptureView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        onImageCaptured = { uri ->
                            vm.setPhotoPath(uri.toString())
                            photoPreview = uri
                            showCamera = false
                            Toast.makeText(context, "Foto tersimpan", Toast.LENGTH_SHORT).show()
                        },
                        onError = { e ->
                            showCamera = false
                            Toast.makeText(context, "Gagal ambil foto: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    // Tombol untuk minta permission + buka kamera
                    Button(
                        onClick = { launcherPermission.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) { Text("📸 Ambil Foto Struk (CameraX)") }
                }

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


}
