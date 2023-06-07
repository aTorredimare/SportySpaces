package it.polito.mad.lab5.friends

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign


@Composable
fun FriendsTopBar(viewModel: FriendsViewModel) {

    val editFriends = viewModel.editFriends.value

    TopAppBar(
        title = {Text(text = if(!editFriends) "Your Friends" else "Search", textAlign = TextAlign.Center) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary),
        actions = {
            IconButton(
                onClick = {
                    viewModel.editFriends.value = !editFriends
                    if(viewModel.editFriends.value){
                        viewModel.searchingFriends.clear()
                    }
                },
                enabled = true
            ) {
                Icon(
                    imageVector = if(!viewModel.editFriends.value) {
                        Icons.Default.Search
                    } else {
                        Icons.Filled.Close
                    },
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    )
}