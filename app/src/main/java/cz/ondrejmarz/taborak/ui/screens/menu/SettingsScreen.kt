package cz.ondrejmarz.taborak.ui.screens.menu

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.R
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole
import cz.ondrejmarz.taborak.data.util.formatDateStringToOutputDayString
import cz.ondrejmarz.taborak.ui.viewmodels.SettingsViewModel
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.LoadingIcon
import cz.ondrejmarz.taborak.ui.components.MiddleDarkButton
import cz.ondrejmarz.taborak.ui.components.RoleSelectionBottomSheet
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.components.SimpleAlertDialog
import cz.ondrejmarz.taborak.ui.viewmodels.factory.SettingsViewModelFactory
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    userId: String,
    tourId: String,
    settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(tourId, userId)),
    navController: NavHostController
) {
    // alert before tour is deleted
    var showDeleteTourAlert by remember { mutableStateOf(false) }

    val role by settingsViewModel.role.collectAsState()
    val tour by settingsViewModel.tour.collectAsState()

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
            verticalArrangement = Arrangement.Top
        ) {
            Section(
                title = tour.title?: "Turnus"
            ) {
                if (tour.startDate != null && tour.endDate != null) {
                    DesignedCard(
                        title = "od " + formatDateStringToOutputDayString(tour.startDate) + " do " + formatDateStringToOutputDayString(tour.endDate)
                    )
                }
            }
            MiddleDarkButton(
                onClickButton = { navController.popBackStack("home", inclusive = false, true) }
            ) {
                Text(text = "Změnit turnus")
            }
            if (role == UserRole.MAJOR) {
                MiddleDarkButton(
                    onClickButton = {
                        showDeleteTourAlert = true
                    }
                ) {
                    Text(text = "Smazat turnus")
                }
            }
            Image(
                modifier = Modifier.scale(0.9f),
                painter = painterResource(id = R.drawable.forest_bro),
                contentDescription = null
            )
        }
    }

    if (showDeleteTourAlert) {
        SimpleAlertDialog(
            onDismissRequest = { showDeleteTourAlert = false },
            onConfirmation = {
                settingsViewModel.viewModelScope.launch {
                    settingsViewModel.deleteTour()
                }.invokeOnCompletion {
                    navController.popBackStack("home", inclusive = false, false)
                }
            },
            dialogTitle = "Opravdu si přejete smazat celý turnus?" ,
            dialogText = "Veškeré informace budou smazány a akci nebude možné vzít zpět."
        )
    }
}