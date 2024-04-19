package cz.ondrejmarz.taborak.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.viewmodel.TourViewModel
import cz.ondrejmarz.taborak.data.viewmodel.UserViewModel
import cz.ondrejmarz.taborak.ui.components.TourList
import cz.ondrejmarz.taborak.ui.components.MiddleDarkButton
import cz.ondrejmarz.taborak.ui.components.Section
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavHostController,
    userId: String? = "",
    onLogoutClick: () -> Unit,
    onTourClick: (String, String) -> Unit,
    onCreateTourClick: () -> Unit,
    tourViewModel: TourViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    tourViewModel.fetchTours()

    val tourList by tourViewModel.tours.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheetAccessDenied by remember { mutableStateOf(false) }
    var selectedTour by remember { mutableStateOf("") }

    Scaffold { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Section(
                title = "Turnusy",
                onButtonClick = onCreateTourClick,
                buttonTitle = "Přidat",
                modifier = Modifier
                    .padding(20.dp)
                    .weight(5.5f)
            ) {
                TourList(
                    tourList.listedTours,
                    userId,
                    onTourSelected = { id: String ->
                        if (userId != null) {
                            onTourClick(id, userId)
                        }
                    },
                    onTourAccessDenied = { tourId: String ->
                        showBottomSheetAccessDenied = true
                        selectedTour = tourId
                    },
                    Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MiddleDarkButton(
                    onClickButton = onLogoutClick
                ) {
                    Text(text = "Odhlásit se")
                }
            }

            if (showBottomSheetAccessDenied) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheetAccessDenied = false
                    },
                    sheetState = sheetState
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Chcete požádat o přijetí?",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Nejste členem tohoto turnusu. Pokud chcete vstoupit, musí být vaše žádost přijata",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                shape = MaterialTheme.shapes.small,
                                onClick = {
                                    userViewModel.sendApplication(selectedTour, userId)
                                    scope.launch {
                                        sheetState.hide()
                                    }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheetAccessDenied = false
                                        }
                                    }
                                }
                            ) {
                                Text(text = "Poslat žádost")
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                shape = MaterialTheme.shapes.small,
                                onClick = {
                                    scope.launch {
                                        sheetState.hide()
                                    }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheetAccessDenied = false
                                        }
                                    }
                                }
                            ) {
                                Text(text = "Zrušit")
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}