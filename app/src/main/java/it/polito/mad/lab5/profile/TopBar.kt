package it.polito.mad.lab5.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(viewModel: ProfileViewModel, logout: () -> Unit) {
    val editMode = viewModel.editMode.value
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(text = "Your Profile" , textAlign = TextAlign.Center, color = Color.White) },

        actions = {
            IconButton(
                onClick = {
                    if(editMode){
                        viewModel.updateUser()
                        viewModel.editMode.value = false
                    }
                    else {
                        setExpanded(true)
                    }
                },
                enabled = true
            ) {
                Icon(
                    imageVector =
                        if(editMode)
                            Icons.Default.Check
                        else
                            Icons.Default.Menu,
                    contentDescription = null,
                    tint = Color.White
                )

                if(!editMode) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { setExpanded(false) }
                    ) {
                        DropdownMenuItem(
                            text = {  Text("Edit profile") },
                            onClick = {
                                viewModel.editMode.value = true
                                setExpanded(false)
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                logout()
                            }
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary)
    )
}

