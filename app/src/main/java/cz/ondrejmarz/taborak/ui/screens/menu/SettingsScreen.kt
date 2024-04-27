package cz.ondrejmarz.taborak.ui.screens.menu

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.ui.viewmodels.SettingsViewModel
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.MiddleDarkButton
import cz.ondrejmarz.taborak.ui.components.RoleSelectionBottomSheet
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.components.SimpleAlertDialog
import cz.ondrejmarz.taborak.ui.viewmodels.factory.SettingsViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    userId: String,
    tourId: String,
    settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(tourId, userId)),
    navController: NavHostController
) {
    val users by settingsViewModel.users.collectAsState()
    val role by settingsViewModel.role.collectAsState()

    var selectedUser by remember { mutableStateOf("") }

    val sheetStateChooseRole = rememberModalBottomSheetState()
    var showBottomSheetChooseRole by remember { mutableStateOf(false) }

    var showDeleteTourAlert by remember { mutableStateOf(false) }

    var showDeleteLastMemberAlert by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                    .verticalScroll(rememberScrollState())
            ) {
                if (users.members.isNotEmpty()) {
                    Section(
                        title = "Členové"
                    ) {
                        MemberList(
                            role = role,
                            userList = users.members,
                            onEditClick = { userId ->
                                selectedUser = userId
                                showBottomSheetChooseRole = true },
                            onDeleteClick = { userId ->
                                if (users.members.size == 1)
                                    showDeleteLastMemberAlert = true
                                else
                                    settingsViewModel.deleteMember(userId) }
                        )
                    }
                }

                if (users.application.isNotEmpty()) {
                    Section(
                        title = "Žádosti o přijetí"
                    ) {
                        ApplicationList(
                            role = role,
                            userList = users.application,
                            onAcceptClick = { userId ->
                                selectedUser = userId
                                showBottomSheetChooseRole = true
                                settingsViewModel.acceptApplication(userId) },
                            onDeleteClick = { userId -> settingsViewModel.deleteApplication(tourId, userId) }
                        )
                    }
                }
            }
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
                        showDeleteTourAlert = true
                    }
                ) {
                    Text(text = "Smazat turnus")
                }
            }
        }
    }

    if (showBottomSheetChooseRole) {
        RoleSelectionBottomSheet(
            roles = UserRole.values().asList(),
            onRoleSelected = { selectedRole ->
                if (selectedUser == userId && settingsViewModel.isLast(UserRole.MAJOR)) {
                    scope.launch {
                        Toast.makeText(
                            context,
                            "Tuto roli musí mít alespoň jeden člen turnusu!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    settingsViewModel.setTourRole(selectedUser, selectedRole)
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

    if (showDeleteTourAlert) {
        SimpleAlertDialog(
            onDismissRequest = { showDeleteTourAlert = false },
            onConfirmation = {
                settingsViewModel.deleteTour()
                navController.popBackStack("home", inclusive = false, false)
            },
            dialogTitle = "Opravdu si přejete smazat celý turnus?" ,
            dialogText = "Veškeré informace budou smazány a akci nebude možné vzít zpět."
        )
    }

    if (showDeleteLastMemberAlert) {
        SimpleAlertDialog(
            onDismissRequest = { showDeleteLastMemberAlert = false },
            onConfirmation = {
                settingsViewModel.deleteTour()
                navController.popBackStack("home", inclusive = true, true)
            },
            dialogTitle = "Opravdu si přejete odejít?" ,
            dialogText = "Veškeré informace o turnuse budou smazány a akci nebude možné vzít zpět."
        )
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
                        Text(text = roleToText(user.role) )
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
                        Text(text = roleToText(user.role) )
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

private fun roleToText(role: UserRole?): String {
    return when (role) {
        UserRole.ADMIN -> "Admin"
        UserRole.MAJOR -> "Hlavní vedoucí"
        UserRole.MINOR -> "Zástupce"
        UserRole.TROOP -> "Oddílák"
        UserRole.GUEST -> "Host"
        else -> "Chyba"
    }
}