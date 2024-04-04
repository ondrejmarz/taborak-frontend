package cz.ondrejmarz.taborak.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.viewmodel.UserViewModel
import cz.ondrejmarz.taborak.data.viewmodel.factory.TourViewModelFactory
import cz.ondrejmarz.taborak.data.viewmodel.factory.UserViewModelFactory
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.components.TourList
import cz.ondrejmarz.taborak.ui.components.UserList

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    tourId: String,
    navController: NavHostController
) {
    val tourModelView = TourViewModelFactory.getTourViewModel()
    //val currentTour = tourModelView.tours.value?.find { it.tourId == tourId }

    val userViewModel = UserViewModelFactory.getUserViewModel()
    userViewModel.fetchAllUsersFromTour(tourId)

    val memberListState: State<List<UserData>?> = userViewModel.members.observeAsState()
    val memberList: List<UserData>? = memberListState.value

    val applicationListState: State<List<UserData>?> = userViewModel.appliactions.observeAsState()
    val applicationList: List<UserData>? = applicationListState.value

    Scaffold(
        bottomBar = {
            BottomNavBar(
                tourId = tourId,
                allScreens = appTabRowScreens,
                onItemSelected = { route ->
                    navController.popBackStack()
                    navController.navigate(route)
                },
                currentScreen = "Nastavení"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Section(
                title = "Členové"
            ) {
                UserList(
                    userList = memberList,
                    onFirstClick = { },
                    firstButtonIcon = Icons.Default.Edit,
                    firstContentDescription = "Upravit",
                    onSecondClick = { userId -> userViewModel.deleteMember(tourId, userId)},
                    secondButtonIcon = Icons.Default.Delete,
                    secondContentDescription = "Smazat"
                )
            }

            Section(
                title = "Žádosti o přijetí"
            ) {
                UserList(
                    userList = applicationList,
                    onFirstClick = { user -> userViewModel.acceptApplication(tourId, user) },
                    firstButtonIcon = Icons.Default.Check,
                    firstContentDescription = "Přijmout",
                    onSecondClick = { userId -> userViewModel.deleteApplication(tourId, userId) },
                    secondButtonIcon = Icons.Default.Delete,
                    secondContentDescription = "Smazat"
                )
            }

            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(bottom = 20.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(text = "Zvolit turnus")
                }

                Button(
                    onClick = {
                        tourModelView.deleteTour(tourId)
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .padding(bottom = 20.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(text = "Smazat turnus")
                }
            }
        }
    }
}