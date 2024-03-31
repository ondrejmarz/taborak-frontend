package cz.ondrejmarz.taborak.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.ondrejmarz.taborak.data.models.Tour
import cz.ondrejmarz.taborak.data.viewmodel.factory.TourViewModelFactory
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TourFormScreen(
    navController: NavHostController
) {
    val tourModelView = TourViewModelFactory.getTourViewModel()

    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tvorba turnusu") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zpět")
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPaddding ->
        Column(
            modifier = Modifier
                .padding(innerPaddding)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Název") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Téma") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Popis") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Od") },
                    modifier = Modifier
                        .weight(1f)
                )

                Spacer(modifier = Modifier.width(20.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Do") },
                    modifier = Modifier
                        .weight(1f)
                )
            }

            Button(
                onClick = {
                    if (title != "") {
                        tourModelView.createNewTour(
                            Tour(
                                title = title,
                                description = description,
                                topic = topic,
                                endDate = "2024-03-28T23:00:00.242+00:00",
                                startDate = "2024-03-28T23:00:00.242+00:00"
                            )
                        )
                    }
                    navController.popBackStack() },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(alignment = Alignment.End),
                shape = MaterialTheme.shapes.small
            ) {
                Text(text = "Vytvořit")
            }
        }
    }
}