package cz.ondrejmarz.taborak.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cz.ondrejmarz.taborak.auth.UserData
import cz.ondrejmarz.taborak.auth.UserRole

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserList(
    userList: List<UserData>,
    onFirstClick: (String) -> Unit,
    firstButtonIcon: ImageVector,
    firstContentDescription: String,
    onSecondClick: (String) -> Unit,
    secondButtonIcon: ImageVector,
    secondContentDescription: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(userList) { index, user ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(vertical = 10.dp),
                shape = MaterialTheme.shapes.small,
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = user.userName?: "Neznámo")
                    Row {
                        IconButton(onClick = { onFirstClick(user.userId) }) {
                            Icon(firstButtonIcon, contentDescription = firstContentDescription)
                        }
                        IconButton(onClick = { onSecondClick(user.userId) }) {
                            Icon(secondButtonIcon, contentDescription = secondContentDescription)
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
    userList: List<UserData>,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(userList) { index, user ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(vertical = 10.dp),
                shape = MaterialTheme.shapes.small,
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = user.userName ?: "Neznámo")
                        Text(text = roleToText(user.role) )
                    }
                    Row {
                        IconButton(onClick = { onEditClick(user.userId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Upravit")
                        }
                        IconButton(onClick = { onDeleteClick(user.userId) }) {
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