package com.example.projekpapbpakadam.uii.home

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projekpapbpakadam.uii.nav.Routes

@Composable
fun HomeScreen(navController: NavController, vm: HomeViewModel) {
    val state by vm.state.collectAsState()

    var budgetValue by rememberSaveable { mutableStateOf(0L) }
    var budgetInput by rememberSaveable { mutableStateOf("") }
    var showBudgetDialog by remember { mutableStateOf(false) }

    val formatter = remember {
        java.text.NumberFormat.getInstance(java.util.Locale("id", "ID"))
    }

    val saldo = state.totalIncome - state.totalExpense
    val budgetTracker = budgetValue - state.totalExpense

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Routes.ADD_EDIT) }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PocketSpends 🪙",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { vm.sync() }) {
                    Text("🔄 Sync Sekarang")
                }
            }

            Spacer(Modifier.height(12.dp))

            // Ringkasan income / expense / saldo
            val saldo = state.totalIncome - state.totalExpense

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Ringkasan Bulan Ini", style = MaterialTheme.typography.titleMedium)
                    Text("Total Pemasukan: Rp ${state.totalIncome}")
                    Text("Total Pengeluaran: Rp ${state.totalExpense}")
                    Text("Saldo: Rp $saldo")
                }
            }

            Spacer(Modifier.height(12.dp))

            // 🔹 Baris Budget & Tracker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Kartu "Budget This Month"
                ElevatedCard(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.elevatedCardElevation()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Budget This Month", style = MaterialTheme.typography.labelMedium)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rp ${formatter.format(budgetValue)}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    budgetInput = if (budgetValue > 0) budgetValue.toString() else ""
                                    showBudgetDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Budget"
                                )
                            }
                        }
                    }
                }

                // Kartu "Budget Tracker"
                ElevatedCard(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.elevatedCardElevation()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Budget Tracker", style = MaterialTheme.typography.labelMedium)

                        Text(
                            text = "Rp ${formatter.format(budgetTracker)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (budgetTracker >= 0)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = if (budgetTracker >= 0)
                                "Masih dalam batas 😊"
                            else
                                "Melebihi budget 😬",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Setelah Spacer(Modifier.height(12.dp)) yang di bawah budget
            if (state.expenseByCategory.isNotEmpty()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.elevatedCardElevation()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "This Month's Expenses",
                            style = MaterialTheme.typography.titleMedium
                        )

                        ExpensePieChart(
                            data = state.expenseByCategory
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
            }


            if (state.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada pengeluaran.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.items, key = { it.id }) { e ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            onClick = { navController.navigate("detail/${e.id}") }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val thumbUri = remember(e.localPhotoPath, e.remotePhotoUrl) {
                                    e.localPhotoPath?.let(Uri::parse)
                                        ?: e.remotePhotoUrl?.let(Uri::parse)
                                }
                                if (thumbUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(thumbUri),
                                        contentDescription = null,
                                        modifier = Modifier.size(56.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(Modifier.width(12.dp))
                                }

                                Column(Modifier.weight(1f)) {
                                    Text(e.title, style = MaterialTheme.typography.titleMedium)
                                    Text("Rp ${e.amount}", style = MaterialTheme.typography.bodyMedium)

                                    Text(
                                        text = if (e.type == "INCOME") "Pemasukan • ${e.category}"
                                        else "Pengeluaran • ${e.category}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }

                                IconButton(onClick = {
                                    navController.navigate("add_edit?id=${e.id}")
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = { vm.delete(e.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cleaned = budgetInput.filter { it.isDigit() }
                        budgetValue = cleaned.toLongOrNull() ?: 0L
                        showBudgetDialog = false
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) {
                    Text("Batal")
                }
            },
            title = { Text("Set Budget Bulanan") },
            text = {
                OutlinedTextField(
                    value = budgetInput,
                    onValueChange = { budgetInput = it },
                    label = { Text("Nominal Budget (Rp)") },
                    singleLine = true
                )
            }
        )
    }
}

@Composable
fun ExpensePieChart(
    data: Map<String, Long>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text("Belum ada pengeluaran bulan ini")
        }
        return
    }

    val total = data.values.sum()
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 🔵 Pie chart
        Canvas(
            modifier = Modifier
                .size(200.dp)
        ) {
            var startAngle = -90f

            data.entries.forEachIndexed { index, (category, value) ->
                val sweepAngle = (value.toFloat() / total.toFloat()) * 360f
                val color = colors[index % colors.size]

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )

                startAngle += sweepAngle
            }
        }

        // 🔹 Legend kategori
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            data.entries.forEachIndexed { index, (category, value) ->
                val color = colors[index % colors.size]

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(color, shape = CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "$category — Rp $value",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

