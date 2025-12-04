package com.example.projekpapbpakadam.uii.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.projekpapbpakadam.uii.home.HomeViewModel
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    vm: HomeViewModel
) {
    val state = vm.state.collectAsState().value
    val formatter = java.text.NumberFormat.getInstance(java.util.Locale("id", "ID"))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Transaction Recap") },
                actions = {
                    IconButton(onClick = {
                        // kayak figma: plus di kanan atas → ke add screen
                        navController.navigate(com.example.projekpapbpakadam.uii.nav.Routes.ADD_EDIT)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah transaksi"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "November 2025",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                val expenseList = state.items.filter { it.type == "EXPENSE" }

                items(expenseList) { e ->

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            // ICON CATEGORY (bulatan kecil)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🍔") // sementara emoji
                            }

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    e.category,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    e.title,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    e.dateFormatted(),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    "-Rp ${formatter.format(e.amount)}",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 🔹 Format tanggal biar rapi
fun com.example.projekpapbpakadam.data.local.ExpenseEntity.dateFormatted(): String {
    val cal = java.util.Calendar.getInstance()
    cal.timeInMillis = this.dateEpochMillis
    val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
    val minute = cal.get(java.util.Calendar.MINUTE)
    return String.format("%02d:%02d", hour, minute)
}
