package cz.ondrejmarz.taborak

import android.app.Activity.RESULT_OK
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.gms.auth.api.identity.Identity
import cz.ondrejmarz.taborak.auth.AuthTokenManager
import cz.ondrejmarz.taborak.auth.GoogleAuthUiClient
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.data.viewmodel.TourViewModel
import cz.ondrejmarz.taborak.data.viewmodel.UserViewModel
import cz.ondrejmarz.taborak.data.viewmodel.auth.SignInViewModel
import cz.ondrejmarz.taborak.ui.screens.CalendarScreen
import cz.ondrejmarz.taborak.ui.screens.HomeScreen
import cz.ondrejmarz.taborak.ui.screens.ParticipantsScreen
import cz.ondrejmarz.taborak.ui.screens.SettingsScreen
import cz.ondrejmarz.taborak.ui.screens.TasksScreen
import cz.ondrejmarz.taborak.ui.screens.TourFormScreen
import cz.ondrejmarz.taborak.ui.screens.TourScreen
import cz.ondrejmarz.taborak.ui.screens.auth.SignInScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navController: NavHostController,
    applicationContext: Context
) {
    NavHost(
        navController = navController,
        startDestination = "sign_in",
    ) {
        AuthTokenManager.init(applicationContext)

        val googleAuthUiClient by lazy {
            GoogleAuthUiClient(
                context = applicationContext,
                oneTapClient = Identity.getSignInClient(applicationContext)
            )
        }

        composable(route = SignIn.route) {
            val viewModel = viewModel<SignInViewModel>()
            //val tourViewModel = viewModel<TourViewModel>()
            val userViewModel = viewModel<UserViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(key1 = Unit) {
                if(googleAuthUiClient.getSignInUser() != null) {
                    userViewModel.checkIfUserIsInDatabase(googleAuthUiClient.getSignInUser())
                    googleAuthUiClient.setSignInUserToken()?.let {
                        AuthTokenManager.saveAuthToken(it) }
                    navController.navigate("home")
                }
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if(result.resultCode == RESULT_OK) {
                        viewModel.viewModelScope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            viewModel.onSignInResult(signInResult)
                        }
                    }
                }
            )

            LaunchedEffect(key1 = state.isSignInSuccessful) {
                if(state.isSignInSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Úspěšné přihlášení",
                        Toast.LENGTH_LONG
                    ).show()

                    googleAuthUiClient.setSignInUserToken()?.let {
                        AuthTokenManager.saveAuthToken(it) }
                    userViewModel.checkIfUserIsInDatabase(googleAuthUiClient.getSignInUser())
                    navController.navigate("home")
                    viewModel.resetState()
                }
            }

            SignInScreen(
                state = state,
                onSignInClick = {
                    viewModel.viewModelScope.launch {
                        val signInIntentSender = googleAuthUiClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                }
            )
        }

        composable(route = Home.route) {
            val viewModel = viewModel<SignInViewModel>()
            //val tourViewModel = viewModel<TourViewModel>()
            val userViewModel = viewModel<UserViewModel>()

            HomeScreen(
                navController,
                googleAuthUiClient.getSignInUser()?.userId,
                onLogoutClick = {
                    viewModel.viewModelScope.launch {
                        googleAuthUiClient.signOut()
                        navController.popBackStack()
                    }
                },
                onTourClick = { tourId: String, userId: String ->
                    navController.navigate("tour/" + tourId )
                    userViewModel.loadTourUserRole(tourId, userId)
                },
                onCreateTourClick = {
                    navController.navigateSingleTopTo("tour_form")
                }
            )
        }

        composable(route = "tour_form") {
            val tourViewModel = viewModel<TourViewModel>()
            val userViewModel = viewModel<UserViewModel>()
            TourFormScreen(
                navController,
                tourViewModel,
                googleAuthUiClient.getSignInUser()
            )
        }


        composable(
            route = Tour.route + "/{tourId}",
            arguments = listOf(navArgument("tourId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId")
            val tourViewModel = viewModel<TourViewModel>()
            tourId?.run { TourScreen(tourId = tourId, tourViewModel, navController) }
        }

        composable(
            route = Participants.route + "/{tourId}",
            arguments = listOf(navArgument("tourId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId")
            tourId?.run { ParticipantsScreen(tourId = tourId, navController) }
        }
        composable(
            route = Calendar.route + "/{tourId}",
            arguments = listOf(navArgument("tourId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId")
            tourId?.run { CalendarScreen(tourId = tourId, navController = navController) }
        }
        composable(
            route = Tasks.route + "/{tourId}",
            arguments = listOf(navArgument("tourId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId")
            tourId?.run { TasksScreen(tourId = tourId, navController = navController) }
        }
        composable(
            route = Settings.route + "/{tourId}",
            arguments = listOf(navArgument("tourId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val tourId = backStackEntry.arguments?.getString("tourId")
            tourId?.run { SettingsScreen(tourId = tourId, navController = navController) }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
