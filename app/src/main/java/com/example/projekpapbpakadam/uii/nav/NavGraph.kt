package com.example.projekpapbpakadam.uii.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.projekpapbpakadam.data.repository.ExpenseRepository
import com.example.projekpapbpakadam.uii.addEdit.AddEditScreen
import com.example.projekpapbpakadam.uii.addEdit.AddEditViewModel
import com.example.projekpapbpakadam.uii.home.HomeScreen
import com.example.projekpapbpakadam.uii.home.HomeViewModel
import com.example.projekpapbpakadam.uii.detail.DetailScreen
import com.example.projekpapbpakadam.uii.detail.DetailViewModel
import com.example.projekpapbpakadam.uii.settings.SettingsScreen
import com.example.projekpapbpakadam.uii.history.HistoryScreen
import com.example.projekpapbpakadam.uii.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"

    const val HOME = "home"
    const val ADD_EDIT = "add_edit"
    const val ADD_EDIT_OPT = "add_edit?id={id}"
    const val DETAIL = "detail/{id}"
    const val SETTINGS = "settings"

    const val HISTORY = "history"   // 🔹 TAMBAHAN BARU

}

@Composable
fun AppNavGraph(navController: NavHostController, repo: ExpenseRepository) {
    NavHost(navController, startDestination = Routes.SPLASH) {

        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }

        composable(Routes.HOME) { backStackEntry ->

            // ViewModel owner adalah route HOME
            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Routes.HOME)
            }

            val vm: HomeViewModel = viewModel(
                parentEntry,
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return HomeViewModel(repo) as T
                    }
                }
            )

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

        composable(Routes.HISTORY) {

            val parentEntry = remember(navController) {
                navController.getBackStackEntry(Routes.HOME)
            }

            val vm: HomeViewModel = viewModel(
                parentEntry,
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return HomeViewModel(repo) as T
                    }
                }
            )

            HistoryScreen(navController, vm)
        }

        composable(Routes.DETAIL) { backStackEntry ->
            // ambil id dari route detail/{id}
            val id = backStackEntry.arguments?.getString("id") ?: ""

            // ViewModel untuk detail
            val vm: DetailViewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    DetailViewModel(repo) as T
            })

            // load data berdasarkan id
            LaunchedEffect(id) {
                vm.load(id)
            }

            DetailScreen(navController = navController, vm = vm)
        }

        composable(Routes.SETTINGS) { SettingsScreen(navController) }
    }
}
