package cz.ondrejmarz.taborak.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.viewmodel.TourViewModel
import cz.ondrejmarz.taborak.data.viewmodel.UserViewModel
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.MiddleDarkButton
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.components.TwoOptionButtons
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    userId: String? = "",
    onLogoutClick: () -> Unit,
    onTourClick: (String, String) -> Unit,
    onCreateTourClick: () -> Unit,
    tourViewModel: TourViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    LaunchedEffect(key1 = true) { tourViewModel.fetchTours() }

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
                    }
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
                        TwoOptionButtons(
                            onLeftClick = {
                                scope.launch {
                                    sheetState.hide()
                                }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheetAccessDenied = false
                                    }
                                }
                            },
                            onLeftText = "Zrušit",
                            onRightClick = {
                                userViewModel.sendApplication(selectedTour, userId)
                                scope.launch {
                                    sheetState.hide()
                                }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheetAccessDenied = false
                                    }
                                }
                            },
                            onRightText = "Poslat žádost"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TourList(
    tourList: List<Tour>?,
    userId: String?,
    onTourSelected: (String) -> Unit,
    onTourAccessDenied: (String) -> Unit
) {
    tourList?.forEach { tour ->
        val isMember = tour.members?.contains(userId)
        if (tour.tourId != null) {
            DesignedCard(
                title = tour.title ?: "Nepojmenovaný turnus",
                topic = tour.topic,
                description = tour.description,
                startTime = tour.startDate,
                endTime = tour.endDate,
                timeInDayFormat = true,
                enabled = isMember,
                button = if (isMember == true) "Otevřít" else "Požádat o přijetí",
                onClickAction = {
                    if (isMember == true) { onTourSelected(tour.tourId) }
                    else { onTourAccessDenied(tour.tourId) }
                }
            )
        }
    }
}
