package com.example.projekpapbpakadam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.projekpapbpakadam.ui.theme.ProjekPAPBPakAdamTheme
import com.example.projekpapbpakadam.uii.nav.AppNavGraph
import com.example.projekpapbpakadam.uii.nav.BottomBar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.projekpapbpakadam.uii.nav.Routes


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjekPAPBPakAdamTheme {

                val navController = rememberNavController()
                val repo = (application as App).repository

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (currentRoute != Routes.SPLASH) {
                            BottomBar(navController = navController)
                        }
                    }
                ) { padding ->   // padding dari Scaffold
                    Box(
                        modifier = Modifier.padding(padding)
                    ) {
                        AppNavGraph(
                            navController = navController,
                            repo = repo,
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProjekPAPBPakAdamTheme {
        Greeting("Android")
    }
}