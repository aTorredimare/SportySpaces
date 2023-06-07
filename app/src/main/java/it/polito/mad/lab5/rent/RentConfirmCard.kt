package it.polito.mad.lab5.rent

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import it.polito.mad.lab5.db.TimeSlot
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

@Composable
fun ReservationDialog(
    onDismiss: () -> Unit,
    onConfirm: suspend ()-> Unit,
    sport: String,
    field: String,
    date: LocalDate?,
    timeSlot: TimeSlot?,
    customRequest: String,
    hourlyRate: Int,
    onCustomRequestChange: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Confirm Reservation") },
        text = {
            Column {
                Text("Sport: $sport")
                Text("Field: $field")
                Text("Date: ${date.toString()}")
                timeSlot?.let {
                    Text("Time: ${it.oraInizio} - ${it.oraFine}")
                }
                Text("Hour rate: $hourlyRate€")
                OutlinedTextField(
                    value = customRequest,
                    onValueChange = onCustomRequestChange,
                    label = { Text("Custom Request") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { runBlocking{ onConfirm() } }) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary)) {
                Text("No")
            }
        }
    )
}