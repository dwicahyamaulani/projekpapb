package com.example.projekpapbpakadam.uii.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projekpapbpakadam.uii.home.HomeScreen
import com.example.projekpapbpakadam.uii.addEdit.AddEditScreen
import com.example.projekpapbpakadam.uii.detail.DetailScreen
import com.example.projekpapbpakadam.uii.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val ADD_EDIT = "add_edit"
    const val DETAIL = "detail/{id}"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.ADD_EDIT) { AddEditScreen(navController) }
        composable(Routes.DETAIL) { DetailScreen(navController) }
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
    }
}
