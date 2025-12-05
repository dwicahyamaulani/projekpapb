package com.example.projekpapbpakadam.uii.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.projekpapbpakadam.data.local.ExpenseEntity
import com.example.projekpapbpakadam.uii.home.HomeViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    vm: HomeViewModel
) {
    val state = vm.state.collectAsState().value
    val formatter = java.text.NumberFormat.getInstance(java.util.Locale("id", "ID"))

    // 🔹 State bulan & tahun aktif
    var selectedMonth by remember { mutableStateOf(currentMonth()) }
    var selectedYear by remember { mutableStateOf(currentYear()) }
    var expanded by remember { mutableStateOf(false) }

    val months = listOf(
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    )

    fun isForSelectedMonth(item: ExpenseEntity): Boolean {
        val cal = Calendar.getInstance()
        cal.timeInMillis = item.dateEpochMillis

        return cal.get(Calendar.MONTH) == selectedMonth &&
                cal.get(Calendar.YEAR) == selectedYear
    }

    val monthlyList = state.items
        .filter { it.type == "EXPENSE" } // hanya pengeluaran
        .filter(::isForSelectedMonth)     // filter bulan
        .sortedByDescending { it.dateEpochMillis }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Transaction Recap") },
                actions = {
                    IconButton(onClick = {
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

            // 🔹 Dropdown Picker Bulan (lebih jelas sebagai tombol pilihan)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${months[selectedMonth]} $selectedYear",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Pilih bulan",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                months.forEachIndexed { index, name ->
                    DropdownMenuItem(
                        text = { Text("$name $selectedYear") },
                        onClick = {
                            selectedMonth = index
                            expanded = false
                        }
                    )
                }
            }


            Spacer(Modifier.height(12.dp))

            if (monthlyList.isEmpty()) {
                Text(
                    text = "Tidak ada transaksi pada bulan ini.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(monthlyList) { e ->

                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("detail/${e.id}")
                                },
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🧾") // placeholder
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
}

// 🔹 Format tanggal biar rapi
fun com.example.projekpapbpakadam.data.local.ExpenseEntity.dateFormatted(): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this.dateEpochMillis
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    return String.format("%02d:%02d", hour, minute)
}

// 🔹 Helper ambil bulan & tahun sekarang
fun currentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH)
fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)
