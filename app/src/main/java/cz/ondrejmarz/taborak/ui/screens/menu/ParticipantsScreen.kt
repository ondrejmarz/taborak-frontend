package cz.ondrejmarz.taborak.ui.screens.menu

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.data.models.Group
import cz.ondrejmarz.taborak.R
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.data.models.Participant
import cz.ondrejmarz.taborak.ui.viewmodels.ParticipantsViewModel
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.viewmodels.factory.ParticipantsViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ParticipantsScreen(
    tourId: String,
    participantsViewModel: ParticipantsViewModel = viewModel(factory = ParticipantsViewModelFactory(tourId)),
    navController: NavHostController
) {
    val participants by participantsViewModel.participants.collectAsState()

    var editMode by remember { mutableStateOf(false) }
    var selectedParticipants by remember { mutableStateOf(emptyList<Participant>()) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                tourId = tourId,
                allScreens = appTabRowScreens,
                onItemSelected = { route ->
                    navController.navigate(route)
                },
                currentScreen = "Účastníci"
            )
        }
    ) { innerPadding ->

        if (participants.groupList?.isEmpty() == true) {

            Column(modifier = Modifier
                .padding(innerPadding),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Section(
                    title = "Účastníci",
                    onButtonClick = {
                        navController.navigate("participants_tutorial/$tourId")
                    },
                    buttonTitle = "Nahrát"
                ) {
                    DesignedCard(
                        title = "Turnus momentálně nemá žádné účastníky",
                        description = "Seznam účastníků můžete nahrát pomocí tlačítka výše."
                    )
                }
                Spacer(modifier = Modifier.height(60.dp))
                Image(
                    painter = painterResource(id = R.drawable.children_pana),
                    contentDescription = null
                )
            }
        }
        else {
            Section(
                title = "Účastníci",
                onButtonClick = { editMode = !editMode },
                buttonTitle = "Rozřadit"
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(ScrollState(0)),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    participants.groupList?.forEach { group ->
                        GroupCard(group, editMode) { selected ->
                            selectedParticipants = selected
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    if (editMode) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Row {
                                Button(onClick = {
                                   participantsViewModel.assignNewParticipants(1, selectedParticipants)
                                }, shape = MaterialTheme.shapes.small) {
                                    Text(text = "Oddíl 1")
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Button(onClick = {
                                    participantsViewModel.assignNewParticipants(2, selectedParticipants)
                                }, shape = MaterialTheme.shapes.small) {
                                    Text(text = "Oddíl 2")
                                }
                            }
                            Row {
                                Button(onClick = {
                                    participantsViewModel.assignNewParticipants(3, selectedParticipants)
                                }, shape = MaterialTheme.shapes.small) {
                                    Text(text = "Oddíl 3")
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Button(onClick = {
                                    participantsViewModel.assignNewParticipants(4, selectedParticipants)
                                }, shape = MaterialTheme.shapes.small) {
                                    Text(text = "Oddíl 4")
                                }
                            }
                            Row {
                                Button(onClick = {
                                    participantsViewModel.assignNewParticipants(5, selectedParticipants)
                                }, shape = MaterialTheme.shapes.small) {
                                    Text(text = "Oddíl 5")
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Button(onClick = {
                                    participantsViewModel.assignNewParticipants(6, selectedParticipants)
                                }, shape = MaterialTheme.shapes.small) {
                                    Text(text = "Oddíl 6")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupCard(group: Group, editMode: Boolean, onSelectedItemsChanged: (List<Participant>) -> Unit) {
    val selectedParticipants = remember { mutableStateOf(emptyList<Participant>()) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = MaterialTheme.shapes.small

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (group.number == "0") "Nezařazení účastníci" else "Oddíl č." + group.number,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(9f)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Průměrný věk je 10.5 let",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .weight(2f)
                )
            }

            ParticipantsCard(group.participants, selectedParticipants.value, editMode) { selected ->
                selectedParticipants.value = selected
                onSelectedItemsChanged(selected)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ParticipantsCard(participants: List<Participant>, selectedItems: List<Participant>, editMode: Boolean, onSelectedItemsChanged: (List<Participant>) -> Unit) {
    val selectedParticipants = remember { mutableStateOf(selectedItems) }
    Card(
        modifier = Modifier
            .fillMaxSize(fraction = 0.9f)
            .wrapContentWidth(Alignment.CenterHorizontally),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        participants.forEach { participant ->
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = participant.name?: "Osoba",
                    modifier = Modifier.weight(9f)
                )
                Spacer(modifier = Modifier.weight(1f))
                if (editMode) {
                    Checkbox(
                        checked = selectedParticipants.value.contains(participant),
                        onCheckedChange = { isChecked ->
                            selectedParticipants.value = if (isChecked) {
                                selectedParticipants.value + participant
                            } else {
                                selectedParticipants.value.filter { it != participant }
                            }
                            onSelectedItemsChanged(selectedParticipants.value)
                        }
                    )
                }
                else if (participant.age != null) {
                    Text(
                        text = participant.age + " let",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .weight(2f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}
