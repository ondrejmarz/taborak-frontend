package cz.ondrejmarz.taborak.ui.screens.menu

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.data.models.Group
import cz.ondrejmarz.taborak.R
import cz.ondrejmarz.taborak.appTabRowScreens
import cz.ondrejmarz.taborak.data.models.Participant
import cz.ondrejmarz.taborak.data.viewmodel.ParticipantsViewModel
import cz.ondrejmarz.taborak.ui.components.BottomNavBar
import cz.ondrejmarz.taborak.ui.components.DesignedCard
import cz.ondrejmarz.taborak.ui.components.Section

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ParticipantsScreen(
    tourId: String,
    participantsViewModel: ParticipantsViewModel = viewModel(),
    navController: NavHostController
) {
    participantsViewModel.fetchParticipants(tourId)
    val participants by participantsViewModel.participants.collectAsState()

    val context = LocalContext.current

    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.run {
            var xlsxContent: ByteArray? = null
            context.contentResolver.openInputStream(this)?.use { inputStream ->
                xlsxContent = inputStream.readBytes()
            }
            xlsxContent?.let { participantsViewModel.uploadParticipantXlsx(tourId, it) }
            participantsViewModel.fetchParticipants(tourId)
        }
    }

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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (participants.groupList?.isEmpty() == true) {
                Section(
                    title = "Účastníci",
                    onButtonClick = {
                        getContent.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    },
                    buttonTitle = "Nahrát"
                ) {
                    DesignedCard(
                        title = "Turnus momentálně nemá žádné účastníky",
                        description = "Seznam účastníků můžete nahrát pomocí tlačítka výše."
                    )
                    Image(
                        painter = painterResource(id = R.drawable.children_pana),
                        contentDescription = null
                    )
                }
            }
            else {
                Section(
                    title = "Účastníci"
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        participants.groupList?.forEach { group ->
                            GroupCard(group)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupCard(group: Group) {
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

            ParticipantsCard(group.participants)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ParticipantsCard(participants: List<Participant>) {
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
                if (participant.age != null) {
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
