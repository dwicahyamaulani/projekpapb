package com.example.projekpapbpakadam.uii.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun BottomBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("?")

    NavigationBar(
        tonalElevation = 10.dp
    ) {
        BottomNavData.items.forEach { item ->

            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    if (item.isCenter) {
                        // 🟦 TOMBOL ADD TENGAH BESAR KAYA FIGMA
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp))
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    navController.navigate(item.route)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                    } else {
                        // ICON KECIL BIASA
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp),
                            tint = if (selected)
                                MaterialTheme.colorScheme.primary
                            else
                                Color.Gray
                        )
                    }
                },
                // label hilang khusus tombol tengah
                label = if (!item.isCenter) {
                    { Text(text = item.label, fontSize = MaterialTheme.typography.labelSmall.fontSize) }
                } else null
            )
        }
    }
}
