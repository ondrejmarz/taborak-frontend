package cz.ondrejmarz.taborak.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.util.formatMillisToIsoDateTime
import cz.ondrejmarz.taborak.data.util.formatMillisToIsoDay
import cz.ondrejmarz.taborak.data.util.fromDayToReadableDay
import cz.ondrejmarz.taborak.data.util.toMillis
import cz.ondrejmarz.taborak.ui.components.DesignedBottomSheet
import cz.ondrejmarz.taborak.ui.viewmodels.HomeViewModel
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.MiddleDarkButton
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.components.TwoOptionButtons
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    userId: String? = "",
    onLogoutClick: () -> Unit,
    onTourClick: (String, String) -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    LaunchedEffect(key1 = true) {
        homeViewModel.fetchTours()
    }

    val isCreatedSuccessfully by homeViewModel.isCreatedSuccessfully.collectAsState()
    val tourList by homeViewModel.tours.collectAsState()

    val sheetStateAccessDenied = rememberModalBottomSheetState()
    var showBottomSheetAccessDenied by remember { mutableStateOf(false) }
    var selectedTour by remember { mutableStateOf("") }

    val sheetStateTourForm = rememberModalBottomSheetState(true)
    var showBottomSheetTourForm by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Section(
                title = "Turnusy",
                onButtonClick = {
                    homeViewModel.resetTourCreationState()
                    showBottomSheetTourForm = true
                },
                buttonTitle = "Přidat",
                modifier = Modifier
                    .padding(20.dp)
            ) {
                when {
                    tourList.listedTours != null -> {
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
                    tourList.isLoading -> {
                        Spacer(modifier = Modifier.height(60.dp))
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                    else -> {

                    }
                }
            }

            MiddleDarkButton(
                onClickButton = onLogoutClick
            ) {
                Text(text = "Odhlásit se")
            }
        }

        if (showBottomSheetAccessDenied) {
            DesignedBottomSheet(
                state = sheetStateAccessDenied,
                onDismiss = {
                    scope.launch {
                        sheetStateAccessDenied.hide()
                    }.invokeOnCompletion {
                        if (!sheetStateAccessDenied.isVisible) {
                            showBottomSheetAccessDenied = false
                        }
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
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
                                sheetStateAccessDenied.hide()
                            }.invokeOnCompletion {
                                if (!sheetStateAccessDenied.isVisible) {
                                    showBottomSheetAccessDenied = false
                                }
                            }
                        },
                        onLeftText = "Zrušit",
                        onRightClick = {
                            homeViewModel.sendApplication(selectedTour, userId)
                            scope.launch {
                                sheetStateAccessDenied.hide()
                            }.invokeOnCompletion {
                                if (!sheetStateAccessDenied.isVisible) {
                                    showBottomSheetAccessDenied = false
                                }
                            }
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Žádost úspěšně odeslána."
                                )
                            }
                        },
                        onRightText = "Poslat žádost"
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        if (showBottomSheetTourForm) {
            DesignedBottomSheet(
                state = sheetStateTourForm,
                onDismiss = {
                    scope.launch {
                        sheetStateTourForm.hide()
                    }.invokeOnCompletion {
                        if (!sheetStateTourForm.isVisible) {
                            showBottomSheetTourForm = false
                        }
                    }
                }
            ) {
                when(isCreatedSuccessfully) {
                    "creating" -> {
                        Spacer(modifier = Modifier.height(60.dp))
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    "default" -> {
                        TourForm(
                            userId,
                            onDismiss = {
                                scope.launch {
                                    sheetStateTourForm.hide()
                                }.invokeOnCompletion {
                                    if (!sheetStateTourForm.isVisible) {
                                        showBottomSheetTourForm = false
                                    }
                                }
                            },
                            onCreate = { tour: Tour ->
                                homeViewModel.createNewTour(
                                    tour
                                )
                            }
                        )
                    }

                    "error" -> {
                        Section(title = "Nastala chyba", buttonTitle = "Zavřít", onButtonClick = {
                            scope.launch {
                                sheetStateTourForm.hide()
                            }.invokeOnCompletion {
                                if (!sheetStateTourForm.isVisible) {
                                    showBottomSheetTourForm = false
                                }
                            }
                        }) {

                        }
                    }

                    else -> {
                        scope.launch {
                            sheetStateTourForm.hide()
                        }.invokeOnCompletion {
                            if (!sheetStateTourForm.isVisible) {
                                showBottomSheetTourForm = false
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TourForm(
    userId: String?,
    onDismiss: () -> Unit,
    onCreate: (Tour) -> Unit
) {
    val dateTime = LocalDateTime.now()

    val dateRangePickerState = remember {
        DateRangePickerState(
            locale = Locale.getDefault(),
            initialSelectedStartDateMillis = dateTime.toMillis(),
            initialDisplayedMonthMillis = null,
            initialSelectedEndDateMillis = dateTime.plusDays(11).toMillis(),
            initialDisplayMode = DisplayMode.Input
        )
    }
    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Tvorba turnusu",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Název") },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Téma") },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Popis") },
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Box(modifier = Modifier.padding(start = 20.dp, top = 20.dp)) {
                    Text(text = "Zvolte datum")
                }
            },
            headline = {
                Box(modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 10.dp)) {
                    Text(text = "${formatMillisToIsoDay(dateRangePickerState.selectedStartDateMillis).fromDayToReadableDay()} – ${formatMillisToIsoDay(dateRangePickerState.selectedEndDateMillis).fromDayToReadableDay()}", style = MaterialTheme.typography.titleLarge)
                }
            },
            showModeToggle = false,

            )
        Spacer(modifier = Modifier.height(20.dp))
        TwoOptionButtons(
            onLeftClick = onDismiss,
            onLeftText = "Zrušit",
            onRightClick = {
                if (title != "" && userId != null) {
                    onCreate(
                        Tour(
                            title = title,
                            description = description,
                            topic = topic,
                            endDate = formatMillisToIsoDateTime(dateRangePickerState.selectedEndDateMillis?: 0),
                            startDate = formatMillisToIsoDateTime(dateRangePickerState.selectedStartDateMillis?: 0),
                            members = listOf(userId),
                            applications = null,
                            groups = null,
                            dailyPrograms = null
                        )
                    )
                }
            },
            onRightText = "Vytvořit"
        )
        Spacer(modifier = Modifier.height(20.dp))
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
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        tourList?.forEach { tour ->
            val isMember = tour.members?.contains(userId)
            val hasApplication = tour.applications?.contains(userId)
            if (tour.tourId != null) {
                DesignedCard(
                    title = tour.title ?: "Nepojmenovaný turnus",
                    topic = tour.topic,
                    description = tour.description,
                    startTime = tour.startDate,
                    endTime = tour.endDate,
                    timeInDayFormat = true,
                    enabled = isMember,
                    button = if (isMember == true) "Otevřít" else if (hasApplication == true) "Žádost odeslána" else "Požádat o přijetí",
                    onClickAction = {
                        if (isMember == true) { onTourSelected(tour.tourId) }
                        else if (hasApplication == false) { onTourAccessDenied(tour.tourId) }
                    }
                )
            }
        }
    }
}
