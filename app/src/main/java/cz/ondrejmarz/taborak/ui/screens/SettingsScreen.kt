package cz.ondrejmarz.taborak.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.data.api.ApiClient
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.viewmodel.UserViewModel
import cz.ondrejmarz.taborak.data.viewmodel.factory.TourViewModelFactory
import cz.ondrejmarz.taborak.data.viewmodel.factory.UserViewModelFactory
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.MemberList
import cz.ondrejmarz.taborak.ui.components.MiddleDarkButton
import cz.ondrejmarz.taborak.ui.components.RoleSelectionBottomSheet
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.components.TourList
import cz.ondrejmarz.taborak.ui.components.UserList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    var selectedUser by remember { mutableStateOf("") }
    var showBottomSheetChooseRole by remember { mutableStateOf(false) }

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (memberList != null && !memberList.isEmpty()) {
                Section(
                    title = "Členové"
                ) {
                    MemberList(
                        userList = memberList,
                        onEditClick = { userId ->
                            selectedUser = userId
                            showBottomSheetChooseRole = true },
                        onDeleteClick = { userId -> userViewModel.deleteMember(tourId, userId)}
                    )
                }
            }

            if (applicationList != null && !applicationList.isEmpty()) {
                Section(
                    title = "Žádosti o přijetí"
                ) {
                    UserList(
                        userList = applicationList,
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

            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MiddleDarkButton(
                    onClickButton = { navController.popBackStack() }
                ) {
                    Text(text = "Změnit turnus")
                }
                MiddleDarkButton(
                    onClickButton = {
                        tourModelView.deleteTour(tourId)
                        navController.popBackStack()
                    }
                ) {
                    Text(text = "Smazat turnus")
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