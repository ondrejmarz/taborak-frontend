package cz.ondrejmarz.taborak.ui.screens.menu

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.data.util.roleToText
import cz.ondrejmarz.taborak.ui.viewmodels.MemberViewModel
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.LoadingIcon
import cz.ondrejmarz.taborak.ui.components.RoleSelectionBottomSheet
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.components.SimpleAlertDialog
import cz.ondrejmarz.taborak.ui.viewmodels.factory.MemberViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MemberScreen(
    tourId: String,
    userId: String,
    memberViewModel: MemberViewModel = viewModel(factory = MemberViewModelFactory(tourId, userId)),
    navController: NavHostController
) {
    val users by memberViewModel.users.collectAsState()
    val role by memberViewModel.role.collectAsState()

    // for editing
    var selectedUser by remember { mutableStateOf("") }

    // when changing or choosing for first time role of user
    val sheetStateChooseRole = rememberModalBottomSheetState()
    var showBottomSheetChooseRole by remember { mutableStateOf(false) }

    // alert before last member is delete and tour too
    var showDeleteLastMemberAlert by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surfaceTint,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        snackbarData = data
                    )
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                tourId = tourId,
                allScreens = appTabRowScreens,
                onItemSelected = { route ->
                    navController.navigate(route)
                },
                currentScreen = "Členové"
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                users.isLoadingMembers || users.isLoadingApplications -> {
                    LoadingIcon()
                }
                else -> {
                    Section(
                        title = "Členové"
                    ) {
                        MemberList(
                            role = role,
                            userList = users.members,
                            onEditClick = { selectedUserId ->
                                if (users.members.find { userData -> userData.userId == selectedUserId }?.role == UserRole.MAJOR && memberViewModel.isLastMajor()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Nelze změnit: poslední hlavní vedoucí.")
                                    }
                                }
                                else {
                                    selectedUser = selectedUserId
                                    showBottomSheetChooseRole = true
                                }
                            },
                            onDeleteClick = { selectedUserId ->
                                // last user in tour, tour will be deleted
                                if (users.members.size == 1)
                                    showDeleteLastMemberAlert = true
                                // last user with Major role in tour, cannot leave tour
                                else if (users.members.find { userData -> userData.userId == selectedUserId }?.role == UserRole.MAJOR && memberViewModel.isLastMajor()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Nelze smazat: poslední hlavní vedoucí.")
                                    }
                                }
                                // user can leave tour
                                else {
                                    memberViewModel.deleteMember(selectedUserId)
                                }
                            }
                        )
                    }
                    if (users.applications.isNotEmpty()) {
                        Section(
                            title = "Žádosti o přijetí"
                        ) {
                            ApplicationList(
                                role = role,
                                userList = users.applications,
                                onAcceptClick = { userId ->
                                    selectedUser = userId
                                    showBottomSheetChooseRole = true
                                    memberViewModel.acceptApplication(userId) },
                                onDeleteClick = { userId -> memberViewModel.deleteApplication(tourId, userId) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheetChooseRole) {
        RoleSelectionBottomSheet(
            onRoleSelected = { selectedRole ->
                if (selectedUser == userId && memberViewModel.isLastMajor()) {
                    scope.launch {
                        Toast.makeText(
                            context,
                            "Tuto roli musí mít alespoň jeden člen turnusu!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    memberViewModel.setTourRole(selectedUser, selectedRole)
                }
                scope.launch {
                    sheetStateChooseRole.hide()
                }.invokeOnCompletion {
                    if (!sheetStateChooseRole.isVisible) {
                        showBottomSheetChooseRole = false
                    }
                }
            },
            onDismiss = {
                scope.launch {
                    sheetStateChooseRole.hide()
                }.invokeOnCompletion {
                    if (!sheetStateChooseRole.isVisible) {
                        showBottomSheetChooseRole = false
                    }
                }
            }
        )
    }

    if (showDeleteLastMemberAlert) {
        SimpleAlertDialog(
            onDismissRequest = { showDeleteLastMemberAlert = false },
            onConfirmation = {
                memberViewModel.viewModelScope.launch {
                    memberViewModel.deleteTour()
                }.invokeOnCompletion {
                    navController.popBackStack("home", inclusive = false, false)
                }
            },
            dialogTitle = "Opravdu si přejete odejít?" ,
            dialogText = "Veškeré informace o turnuse budou smazány a akci nebude možné vzít zpět."
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MemberList(
    role: UserRole,
    userList: List<UserData>,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
) {
    Column {
        userList.forEach { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(vertical = 10.dp),
                shape = MaterialTheme.shapes.small,
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = user.userName ?: "Neznámo")
                        Text(text = roleToText(user.role), style = MaterialTheme.typography.labelSmall)
                    }
                    Row {
                        IconButton(onClick = { onEditClick(user.userId) }, enabled = role == UserRole.MAJOR) {
                            Icon(Icons.Default.Edit, contentDescription = "Upravit")
                        }
                        IconButton(onClick = { onDeleteClick(user.userId) }, enabled = role == UserRole.MAJOR) {
                            Icon(Icons.Default.Delete, contentDescription = "Smazat")
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ApplicationList(
    role: UserRole,
    userList: List<UserData>,
    onAcceptClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
) {
    Column {
        userList.forEach { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(vertical = 10.dp),
                shape = MaterialTheme.shapes.small,
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = user.userName ?: "Neznámo")
                        Text(text = user.email?: "Bez e-mailové adresy", style = MaterialTheme.typography.labelSmall)
                    }
                    Row {
                        IconButton(onClick = { onAcceptClick(user.userId) }, enabled = role == UserRole.MAJOR) {
                            Icon(Icons.Default.Check, contentDescription = "Přijmou")
                        }
                        IconButton(onClick = { onDeleteClick(user.userId) }, enabled = role == UserRole.MAJOR) {
                            Icon(Icons.Default.Delete, contentDescription = "Smazat")
                        }
                    }
                }
            }
        }
    }
}