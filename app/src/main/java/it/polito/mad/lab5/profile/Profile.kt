
package it.polito.mad.lab5.profile

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable
fun Profile(context: Context, viewModel: ProfileViewModel, logout: () -> Unit) {

    val selectedSports = viewModel.selectedSports
    val allSports =  viewModel.allSports

    Scaffold(
        topBar = {
            MyTopBar(viewModel = viewModel, logout)
        }
    ) { it ->

        val name = viewModel.name.value
        val nickname = viewModel.nickname.value
        val email = viewModel.email.value
        val birthday = viewModel.birthdate.value
        val sex = viewModel.sex.value
        val city = viewModel.city.value
        val imageUri = viewModel.imageUri.value
        val editMode = viewModel.editMode.value
        val changePhotoExpanded = viewModel.changePhotoExpanded.value
        val showDialog = viewModel.showDialog.value

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                ProfileImage(
                    imageUri,
                    { viewModel.imageUri.value = it },
                    editMode,
                    changePhotoExpanded,
                    { viewModel.changePhotoExpanded.value = it },
                    context
                )
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ProfileField(
                    hover = "Nickname",
                    text = nickname,
                    setText = { viewModel.nickname.value = it },
                    type = "simple",
                    editMode = editMode
                )
                ProfileField(
                    hover = "FullName",
                    text = name,
                    setText = { viewModel.name.value = it },
                    type = "simple",
                    editMode = editMode
                )
                ProfileField(
                    hover = "Mail",
                    text = email,
                    setText = { viewModel.email.value = it },
                    type = "email",
                    editMode = editMode
                )
                ProfileField(
                    hover = "Birthdate",
                    text = birthday,
                    setText = { viewModel.birthdate.value = it },
                    type = "date",
                    editMode = editMode
                )
                ProfileField(
                    hover = "Sex",
                    text = sex,
                    setText = { viewModel.sex.value = it },
                    type = "simple",
                    editMode = editMode
                )
                ProfileField(
                    hover = "City",
                    text = city,
                    setText = { viewModel.city.value = it },
                    type = "simple-last",
                    editMode = editMode
                )
            }

            if (!editMode)
                Row {
                    Column(Modifier.weight(1f)) {
                        SportsTable(selectedSports) { viewModel.showDialog.value = it }
                    }
                }
            if (showDialog) {
                SelectSportsDialog(
                    allSports = allSports,
                    selectedSports = selectedSports,
                    onDismissRequest = {
                        viewModel.fetchUserSports()
                        viewModel.showDialog.value = false
                    },
                    setShowDialog = { viewModel.showDialog.value = it },
                    saveSports = { viewModel.updateUserSports() }
                )
            }
        }
    }
}
    