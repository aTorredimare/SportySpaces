package it.polito.mad.lab5.profile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ProfileField(hover: String, text: String, type: String, setText: (String)->Unit, editMode: Boolean) {
    var pickedDate by remember { mutableStateOf(LocalDate.now()) }
    val formattedDate by remember { derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd/MM/yyyy")
                .format(pickedDate)
        }
    }
    val dateDialogState = rememberMaterialDialogState()

    Card(modifier = Modifier.padding(bottom = 8.dp), colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,)
    ) {
        val focusManager = LocalFocusManager.current
        Row(
            modifier = if(editMode) {
                Modifier.fillMaxWidth().padding(8.dp)
            } else {
                Modifier.fillMaxWidth().height(48.dp).padding(8.dp)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = hover,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            if (editMode) {
                if( hover == "Birthdate") {
                    OutlinedTextField(
                        modifier = Modifier.weight(2f),
                        value = text,
                        singleLine = true,
                        onValueChange = {setText(it)},
                        trailingIcon = {
                            IconButton(
                                modifier = Modifier.weight(0.5f),
                                onClick = {  dateDialogState.show() }
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Calendar icon")
                            }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {focusManager.moveFocus(FocusDirection.Down) })
                    )

                    MaterialDialog(
                        dialogState = dateDialogState,
                        shape = RoundedCornerShape(16.dp),
                        buttons = {
                            positiveButton(
                                text = "Ok",
                                textStyle = TextStyle(color = Color.Black)
                            )
                            negativeButton(
                                text = "Cancel",
                                textStyle = TextStyle(color = Color.LightGray)
                            )
                        }
                    ) {
                        datepicker(
                            initialDate = pickedDate,
                            title = "Pick a date",
                            colors = DatePickerDefaults.colors(
                                headerBackgroundColor = MaterialTheme.colorScheme.primary,
                                headerTextColor = Color.Black,
                                dateActiveBackgroundColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            pickedDate = it
                            setText(formattedDate)
                        }
                    }
                }
                else {
                    OutlinedTextField(
                        value = text,
                        enabled = (hover != "Mail"),
                        onValueChange = { setText(it) },
                        modifier = Modifier.weight(2f),
                        singleLine = true,
                        keyboardOptions = when(type) {
                            "simple" -> KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            )
                            else -> KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            )
                        },
                        keyboardActions = KeyboardActions(
                            onNext = {focusManager.moveFocus(FocusDirection.Down) },
                            onDone = {focusManager.clearFocus()}
                        ),
                    )
                }
            }
            else {
                if (text === "")
                    Text(
                        text = "Not inserted yet",
                        modifier = Modifier.weight(2f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                else
                    Text(
                        text = text,
                        modifier = Modifier.weight(2f),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                    )
            }
        }
    }
}




