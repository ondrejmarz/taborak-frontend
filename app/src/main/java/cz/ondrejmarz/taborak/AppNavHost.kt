package cz.ondrejmarz.taborak

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cz.ondrejmarz.taborak.auth.AuthTokenManager
import cz.ondrejmarz.taborak.auth.GoogleAuthUiClient
import cz.ondrejmarz.taborak.ui.viewmodels.SignInViewModel
import cz.ondrejmarz.taborak.ui.screens.menu.CalendarScreen
import cz.ondrejmarz.taborak.ui.screens.HomeScreen
import cz.ondrejmarz.taborak.ui.screens.menu.ParticipantsScreen
import cz.ondrejmarz.taborak.ui.screens.menu.SettingsScreen
import cz.ondrejmarz.taborak.ui.screens.menu.TasksScreen
import cz.ondrejmarz.taborak.ui.screens.auth.SignInScreen
import cz.ondrejmarz.taborak.ui.screens.details.DayPlanScreen
import cz.ondrejmarz.taborak.ui.screens.details.LoadParticipantsTutorial
import cz.ondrejmarz.taborak.ui.screens.menu.MemberScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navController: NavHostController,
    applicationContext: Context,
    googleAuthUiClient: GoogleAuthUiClient,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        AuthTokenManager.init(applicationContext)

        composable(route = SignIn.route) {

            SignInScreen(
                googleAuthUiClient,
                onSuccess = { navController.navigate("home") },
            )
        }

        composable(route = Home.route) {
            val viewModel = viewModel<SignInViewModel>()

            LaunchedEffect(key1 = Unit) {
                if(googleAuthUiClient.getSignInUser() != null) {
                    //viewModel.checkIfUserIsInDatabase(googleAuthUiClient.getSignInUser())
                    googleAuthUiClient.setSignInUserToken()?.let {
                        AuthTokenManager.saveAuthToken(it) }
                }
                else {
                    navController.navigate("sign_in")
                }
            }

            HomeScreen(
                googleAuthUiClient.getSignInUser()?.userId,
                onLogoutClick = {
                    viewModel.viewModelScope.launch {
                        googleAuthUiClient.signOut()
                        navController.navigate("sign_in")
                    }
                },
                onTourClick = { tourId: String, userId: String ->
                    navController.navigate(Calendar.route + "/" + tourId )
                    viewModel.loadTourUserRole(tourId, userId)
                }
            )
        }

        composable(
            route = Members.route + "/{tourId}",
            arguments = listOf(navArgument("tourId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId")
            val userId = googleAuthUiClient.getSignInUser()?.userId
            tourId?.run {
                userId?.run {
                    MemberScreen(tourId = tourId, userId = userId, navController = navController)
                }
            }
        }

        composable(
            route = Participants.route + "/{tourId}",
            arguments = listOf(navArgument("tourId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId")
            tourId?.run { ParticipantsScreen(tourId = tourId, navController = navController) }
        }

        composable(
            route = "participants_tutorial/{tourId}",
            arguments = listOf(navArgument("tourId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId")
            tourId?.run {
                LoadParticipantsTutorial(
                    tourId = tourId,
                    onGoBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Calendar.route + "/{tourId}",
            arguments = listOf(navArgument("tourId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId")
            tourId?.run {
                CalendarScreen(tourId = tourId, navController = navController)
            }
        }

        composable(
            route = "daily_plan/{tourId}/{day}",
            arguments = listOf(navArgument("tourId") { type = NavType.StringType },
                navArgument("day") { type = NavType.StringType})
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId")
            val day = backStackEntry.arguments?.getString("day")
            tourId?.run { DayPlanScreen(tourId = tourId, day, navController = navController) }
        }

        composable(
            route = Tasks.route + "/{tourId}",
            arguments = listOf(navArgument("tourId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId")
            tourId?.run { TasksScreen(tourId = tourId, navController = navController) }
        }
        composable(
            route = Settings.route + "/{tourId}",
            arguments = listOf(navArgument("tourId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId")
            val userId = googleAuthUiClient.getSignInUser()?.userId
            tourId?.run {
                userId?.run {
                    SettingsScreen(
                        userId = userId,
                        tourId = tourId,
                        navController = navController
                    )
                }
            }
        }
    }
}