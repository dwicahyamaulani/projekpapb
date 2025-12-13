package com.example.projekpapbpakadam.uii.home

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projekpapbpakadam.uii.nav.Routes
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    vm: HomeViewModel
) {
    val state by vm.state.collectAsState()

    // 🔹 SINGLE SOURCE OF TRUTH
    val monthlyBudget = state.monthlyBudget

    var budgetInput by rememberSaveable { mutableStateOf("") }
    var showBudgetDialog by remember { mutableStateOf(false) }

    val formatter = remember {
        NumberFormat.getInstance(Locale("id", "ID"))
    }

    val saldo = state.totalIncome - state.totalExpense
    val budgetTracker = monthlyBudget - state.totalExpense

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ================= HEADER =================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💰 PocketSpends",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f)
                )

                Button(onClick = { vm.sync() }) {
                    Text("🔄 Sync")
                }
            }
        }

        // ================= NET BALANCE =================
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Net Balance: IDR",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Rp ${formatter.format(saldo)}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        // ================= INCOME & EXPENSE =================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryStatCard(
                    title = "Income",
                    amountText = "Rp ${formatter.format(state.totalIncome)}",
                    amountColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                SummaryStatCard(
                    title = "Expenses",
                    amountText = "Rp ${formatter.format(state.totalExpense)}",
                    amountColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ================= BUDGET =================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Budget This Month
                ElevatedCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Budget This Month")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rp ${formatter.format(monthlyBudget)}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                budgetInput =
                                    if (monthlyBudget > 0) monthlyBudget.toString() else ""
                                showBudgetDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Budget")
                            }
                        }
                    }
                }

                // Budget Tracker
                ElevatedCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Budget Tracker")

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
        }

        // ================= PIE CHART =================
        if (state.expenseByCategory.isNotEmpty()) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("This Month's Expenses")
                        ExpensePieChart(data = state.expenseByCategory)
                    }
                }
            }
        }

        // ================= TRANSACTION HEADER =================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Transaction", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { navController.navigate(Routes.HISTORY) }) {
                    Text("See More")
                }
            }
        }

        // ================= LAST 3 TRANSACTIONS =================
        val latestItems = state.items
            .sortedByDescending { it.dateEpochMillis }
            .take(3)

        if (latestItems.isEmpty()) {
            item {
                Text("Belum ada transaksi.")
            }
        } else {
            items(latestItems, key = { it.id }) { e ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigate("detail/${e.id}") }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(e.title)
                            Text("Rp ${formatter.format(e.amount)}")
                            Text("${e.type} • ${e.category}")
                        }

                        IconButton(onClick = {
                            navController.navigate("add_edit?id=${e.id}")
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }

                        IconButton(onClick = { vm.delete(e.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }

    // ================= BUDGET DIALOG =================
    if (showBudgetDialog) {
        Dialog(onDismissRequest = { showBudgetDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Budget",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { budgetInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Rp 0") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Button(
                        onClick = {
                            val value =
                                budgetInput.filter { it.isDigit() }.toLongOrNull() ?: 0L
                            vm.setBudget(value)
                            showBudgetDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}


@Composable
fun SummaryStatCard(
    title: String,
    amountText: String,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = amountText,
                style = MaterialTheme.typography.titleMedium,
                color = amountColor
            )
        }
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

