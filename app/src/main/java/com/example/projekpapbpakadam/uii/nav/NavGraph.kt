package com.example.projekpapbpakadam.uii.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.projekpapbpakadam.data.repository.ExpenseRepository
import com.example.projekpapbpakadam.uii.addEdit.AddEditScreen
import com.example.projekpapbpakadam.uii.addEdit.AddEditViewModel
import com.example.projekpapbpakadam.uii.home.HomeScreen
import com.example.projekpapbpakadam.uii.home.HomeViewModel
import com.example.projekpapbpakadam.uii.detail.DetailScreen
import com.example.projekpapbpakadam.uii.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val ADD_EDIT = "add_edit"
    const val ADD_EDIT_OPT = "add_edit?id={id}"
    const val DETAIL = "detail/{id}"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavGraph(navController: NavHostController, repo: ExpenseRepository) {
    NavHost(navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            val vm: HomeViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(c: Class<T>): T =
                    HomeViewModel(repo) as T
            })
            HomeScreen(navController, vm)
        }

        composable("${Routes.ADD_EDIT}?id={id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val vm: AddEditViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(c: Class<T>): T =
                    AddEditViewModel(repo) as T
            })

            // Load data kalau mode edit
            LaunchedEffect(id) {
                if (!id.isNullOrBlank()) vm.load(id)
            }

            AddEditScreen(navController, vm)
        }



        composable(Routes.DETAIL) { DetailScreen(navController) }
        composable(Routes.SETTINGS) { SettingsScreen(navController) }
    }
}
