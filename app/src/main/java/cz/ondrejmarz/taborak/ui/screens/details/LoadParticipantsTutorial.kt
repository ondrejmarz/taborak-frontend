package cz.ondrejmarz.taborak.ui.screens.details

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.ondrejmarz.taborak.R
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.components.TwoOptionButtons
import cz.ondrejmarz.taborak.ui.viewmodels.LoadParticipantsViewModel
import cz.ondrejmarz.taborak.ui.viewmodels.factory.LoadParticipantsViewModelFactory
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoadParticipantsTutorial(
    tourId: String,
    onGoBack: () -> Unit,
    viewModel: LoadParticipantsViewModel = viewModel(factory = LoadParticipantsViewModelFactory(tourId))
) {
    val context = LocalContext.current

    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.run {
            var xlsxContent: ByteArray? = null
            context.contentResolver.openInputStream(this)?.use { inputStream ->
                xlsxContent = inputStream.readBytes()
            }
            xlsxContent?.let {
                viewModel.viewModelScope.launch {
                    viewModel.uploadParticipantXlsx(it)
                }.invokeOnCompletion { onGoBack() }
            }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Section(title = "Návod") {
                    Text(text = "V systému Domeček otevřete seznam účastníků tohoto turnusu.")
                    Text(text = "Zvolte možnost vytvořit novou sestavu, můžete ji pojmenovat jakkoliv.")
                    Text(text = "Sestava musí v odpovídajícím pořadí obsahovat jméno, příjmení, věk, email rodiče a telefon rodiče.")
                    Text(text = "Zvolte možnost XLSX a soubor uložte na Google Drive se stejným účtem jakým se přihlašujete do této aplikace.")
                    Text(text = "Pokračujte vybráním tohoto nově vytvořeného souboru.")
                    TwoOptionButtons(
                        onLeftClick = onGoBack,
                        onLeftText = "Zrušit",
                        onRightClick = {
                            getContent.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        },
                        onRightText = "Vybrat soubor"
                    )
                }
            }
            Column {
                Image(
                    painter = painterResource(id = R.drawable.spirit_pana),
                    contentDescription = null
                )
            }
        }
    }
}