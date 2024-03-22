package cz.ondrejmarz.taborak

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cz.ondrejmarz.taborak.databinding.ActivityMainBinding
import cz.ondrejmarz.taborak.model.TourList
import cz.ondrejmarz.taborak.model.tourList
import cz.ondrejmarz.taborak.ui.components.AppTabRow
import cz.ondrejmarz.taborak.ui.theme.AppTheme

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TheApp();
        }
    }
}

@Composable
fun TheApp() {
    AppTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        val currentScreen =
            appTabRowScreens.find { it.route == currentDestination?.route } ?: Overview

        Scaffold(
            bottomBar = {
                AppTabRow(
                    allScreens = appTabRowScreens,
                    onTabSelected = { newScreen ->
                        navController.navigateSingleTopTo(newScreen.route)
                    },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            AppNavHost(navController = navController, Modifier.padding(innerPadding))
        }
    }
}
