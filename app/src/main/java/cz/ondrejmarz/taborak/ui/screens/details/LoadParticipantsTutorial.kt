package cz.ondrejmarz.taborak.ui.screens.details

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cz.ondrejmarz.taborak.R
import cz.ondrejmarz.taborak.ui.components.Section
import cz.ondrejmarz.taborak.ui.components.TwoOptionButtons

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoadParticipantsTutorial(
    onDissmiss: () -> Unit,
    onContinue: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Section(title = "Návod") {
                    Text(text = "V systému Domeček otevřete seznam účastníků tohoto turnusu.")
                    Text(text = "Zvolte možnost vytvořit novou sestavu, můžete ji pojmenovat jakkoliv.")
                    Text(text = "Sestava musí v odpovídajícím pořadí obsahovat jméno, příjmení, věk, email rodiče a telefon rodiče.")
                    Text(text = "Zvolte možnost XLSX a soubor uložte na Google Drive se stejným účtem jakým se přihlašuje do této aplikace.")
                    Text(text = "Pokračujte vybráním tohoto nově vytvořeného souboru.")
                }
                TwoOptionButtons(
                    onLeftClick = onDissmiss,
                    onLeftText = "Zrušit",
                    onRightClick = onContinue,
                    onRightText = "Vybrat soubor"
                )
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