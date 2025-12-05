package com.example.projekpapbpakadam.uii.detail

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    vm: DetailViewModel
) {
    val state = vm.state
    val item = state.item

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = { Text(text = "Transaction Detail") }
            )
        }
    ) { padding ->

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (item == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Data tidak ditemukan")
            }
            return@Scaffold
        }

        val formatter = java.text.NumberFormat.getInstance(Locale("id", "ID"))
        val amountText = "Rp ${formatter.format(item.amount)}"

        val dateText = formatDate(item.dateEpochMillis)
        val timeText = formatTime(item.dateEpochMillis)

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ===== Card utama =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // Judul + menu
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { /* TODO: menu */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }
                    }

                    // Amount besar
                    Text(
                        text = if (item.type == "EXPENSE") "-$amountText" else amountText,
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (item.type == "EXPENSE")
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )

                    // Baris info kecil (kategori, tanggal, lokasi, dll)
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Date",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Time",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // ===== Receipt / foto =====
            Text(
                text = "Receipt",
                style = MaterialTheme.typography.titleSmall
            )

            val photoUri = item.localPhotoPath?.let { Uri.parse(it) }
                ?: item.remotePhotoUrl?.let { Uri.parse(it) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(photoUri),
                        contentDescription = "Receipt photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada foto struk")
                    }
                }
            }
        }
    }
}

private fun formatDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    return sdf.format(Date(epochMillis))
}

private fun formatTime(epochMillis: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale("id", "ID"))
    return sdf.format(Date(epochMillis))
}
