package it.polito.mad.lab5.rent

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.polito.mad.lab5.MyCalendar
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Rent(vm: RentViewModel) {
    Log.d(TAG, if(vm.isEdit.value) "rent edit mode" else "add rent mode")
    val context = LocalContext.current

    val sportsList = vm.sportsList1.toList()
    val selectedSport = vm.selectedSport.value

    val playgroundsList = vm.playgroundsList.toList()
    val selectedPlayground = vm.selectedPlayground.value

    val fullDates = vm.fullDates
    val selectedDate = vm.selectedDate.value

    val freeSlots = vm.freeSlots.toList()
    val selectedTimeSlot = vm.selectedTimeSlot.value

    val customRequest = vm.customRequest.value

    var expandedSport by remember { mutableStateOf(false) }
    var expandedField by remember { mutableStateOf(false) }
    //var showFieldsDropDown  by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if(!vm.isEdit.value) "Rent a playing field" else "Edit you reservation" , textAlign = TextAlign.Center)},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary)
            )
        },
        content = {it ->
            LazyColumn(
                modifier = Modifier.padding(it)
            ) {
                item {
                    if(!vm.isEdit.value) {
                        Text(text = "Choose your sport:", modifier = Modifier.padding(16.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedSport,
                            onExpandedChange = {
                                expandedSport = !expandedSport
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp)
                        ) {
                            OutlinedTextField(
                                value = selectedSport,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSport) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            DropdownMenu(
                                expanded = expandedSport,
                                onDismissRequest = {
                                    expandedSport = false/*; vm.selectedDate.value = null */
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp)
                            ) {

                                sportsList.forEach { item ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = item, modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 16.dp, end = 16.dp)
                                            )
                                        },
                                        onClick = {
                                            vm.selectedSport.value = item
                                            expandedSport = false
                                            //showFieldsDropDown = true
                                            vm.loadPlaygrounds()
                                            vm.selectedPlayground.value = "Playground"
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        if (selectedPlayground != "") {
                            if (selectedSport.isNotEmpty()) {
                                Text(
                                    text = "Select the playground:",
                                    modifier = Modifier.padding(16.dp)
                                )
                                ExposedDropdownMenuBox(
                                    expanded = expandedField,
                                    onExpandedChange = {
                                        expandedField = !expandedField
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(/*top = 16.dp, */start = 16.dp, end = 16.dp)
                                ) {
                                    OutlinedTextField(
                                        value = selectedPlayground,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expandedField
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                    )

                                    DropdownMenu(
                                        expanded = expandedField,
                                        onDismissRequest = {
                                            expandedField = false
                                            //vm.selectedDate.value = null
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, end = 16.dp)
                                    ) {

                                        //playgroundNamesList.forEach { item ->
                                        playgroundsList.forEach { item ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = item.playgroundName!!,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(start = 16.dp, end = 16.dp)
                                                    )
                                                },
                                                onClick = {
                                                    vm.selectedPlayground.value = item.playgroundName!!
                                                    expandedField = false
                                                    vm.selectedDate.value = Date()
                                                    vm.loadFullDates()
                                                    vm.loadFreeSlots()
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else {
                        Text(
                            text = "Selected sport:",
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = selectedSport,
                            enabled = false,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        )
                        Text(
                            text = "Selected playground:",
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = selectedPlayground,
                            enabled = false,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        )
                    }
                }

                item {
                    if (selectedSport != "Sport" && selectedPlayground != "Playground" && selectedPlayground != "") {
                        Text(text = "Select the date:", modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp))

                        MyCalendar(
                            selectedDate = vm.selectedDate.value!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                            setSelectedDate = { date: LocalDate? ->
                                vm.selectedDate.value = date.toDate()
                                Log.d(TAG, "Selected date: ${vm.selectedDate.value} and $selectedDate")
                                vm.loadFreeSlots()
                                              },
                            isColored = { //it: CalendarDay ->
                                var found = false
                                for (d in fullDates) {
                                    if (d == Date.from(it.date.atStartOfDay(ZoneOffset.systemDefault()).toInstant())) {
                                        found = true
                                        break
                                    }
                                }
                                found},
                            backgroundColor = Color(0xFFe06666),
                            isEdit = vm.isEdit.value
                        )
                    }
                }
                if (selectedSport != "Sport" && selectedPlayground != "Playground" && selectedPlayground != "" && selectedDate != null) {
                    item {
                        Text(text = "Choose the hour:", modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp))
                        if(freeSlots.isNotEmpty()) {
                            ReservationList(data = freeSlots) { chosenTimeSlot ->
                                showDialog = true
                                vm.selectedTimeSlot.value = chosenTimeSlot
                            }
                        }
                        else{
                            Text(text = "No slots available", modifier = Modifier.fillMaxWidth().padding(16.dp), textAlign = TextAlign.Center)
                        }

                        if (showDialog) {
                            ReservationDialog(
                                onDismiss = {
                                    showDialog = false
                                    vm.customRequest.value = ""
                                },
                                onConfirm = {
                                    runBlocking {
                                        vm.saveReservation()
                                    }
                                    Toast.makeText(context, "Reservation saved", Toast.LENGTH_LONG)
                                        .show()
                                    showDialog = false
                                    vm.customRequest.value = ""
                                },
                                sport = selectedSport,
                                field = selectedPlayground,
                                date = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                timeSlot = selectedTimeSlot,
                                customRequest = customRequest,
                                hourlyRate = playgroundsList.filter { it.playgroundName == selectedPlayground }[0].hourlyRate!!
                            ) { vm.customRequest.value = it }
                        }
                    }
                }
            }
        }
    )
}

fun LocalDate?.toDate(): Date {
    return Date.from(this!!.atStartOfDay(ZoneOffset.systemDefault()).toInstant())
}
