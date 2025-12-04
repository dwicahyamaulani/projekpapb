package com.example.projekpapbpakadam.uii.nav

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List

import com.example.projekpapbpakadam.uii.nav.Routes

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    val isCenter: Boolean = false
)

object BottomNavData {
    val items = listOf(
        BottomNavItem("Home", Routes.HOME, Icons.Filled.Home),
        BottomNavItem("Add", Routes.ADD_EDIT, Icons.Filled.Add),
        BottomNavItem("History", Routes.HISTORY, Icons.Filled.List)
    )
}
