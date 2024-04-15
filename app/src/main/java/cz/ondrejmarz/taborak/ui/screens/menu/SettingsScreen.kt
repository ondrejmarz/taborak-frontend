package cz.ondrejmarz.taborak.ui.screens.menu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.data.viewmodel.TourViewModel
import cz.ondrejmarz.taborak.data.viewmodel.UserViewModel
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.MemberList
import cz.ondrejmarz.taborak.ui.components.MiddleDarkButton
import cz.ondrejmarz.taborak.ui.components.RoleSelectionBottomSheet
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.components.UserList

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    tourId: String,
    tourViewModel: TourViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    navController: NavHostController
) {
    userViewModel.fetchAllUsersFromTour(tourId)

    val users by userViewModel.users.collectAsState()

    var selectedUser by remember { mutableStateOf("") }
    var showBottomSheetChooseRole by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                tourId = tourId,
                allScreens = appTabRowScreens,
                onItemSelected = { route ->
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(6f)
            ) {
                if (users.members.isNotEmpty()) {
                    Section(
                        title = "Členové"
                    ) {
                        MemberList(
                            userList = users.members,
                            onEditClick = { userId ->
                                selectedUser = userId
                                showBottomSheetChooseRole = true },
                            onDeleteClick = { userId -> userViewModel.deleteMember(tourId, userId)}
                        )
                    }
                }

                if (users.application.isNotEmpty()) {
                    Section(
                        title = "Žádosti o přijetí"
                    ) {
                        UserList(
                            userList = users.application,
                            onFirstClick = { userId ->
                                selectedUser = userId
                                showBottomSheetChooseRole = true
                                userViewModel.acceptApplication(tourId, userId) },
                            firstButtonIcon = Icons.Default.Check,
                            firstContentDescription = "Přijmout",
                            onSecondClick = { userId -> userViewModel.deleteApplication(tourId, userId) },
                            secondButtonIcon = Icons.Default.Delete,
                            secondContentDescription = "Smazat"
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(2f)
            ) {
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MiddleDarkButton(
                        onClickButton = { navController.popBackStack("home", inclusive = false, true) }
                    ) {
                        Text(text = "Změnit turnus")
                    }
                    MiddleDarkButton(
                        onClickButton = {
                            // TODO: vyhodit upozornění
                            tourViewModel.deleteTour(tourId)
                            navController.popBackStack("home", inclusive = true, true)
                        }
                    ) {
                        Text(text = "Smazat turnus")
                    }
                }
            }

            if (showBottomSheetChooseRole) {
                RoleSelectionBottomSheet(
                    roles = UserRole.values().asList(),
                    onRoleSelected = { role ->
                        userViewModel.setTourRole(tourId, selectedUser, role)
                    },
                    onDismiss = {
                        showBottomSheetChooseRole = false
                    }
                )
            }
        }
    }
}