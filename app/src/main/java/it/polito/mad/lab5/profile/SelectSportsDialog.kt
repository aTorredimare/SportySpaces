package it.polito.mad.lab5.profile


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import it.polito.mad.lab5.db.Sport
import it.polito.mad.lab5.db.UserSports


@Composable
fun SelectSportsDialog(
    allSports: MutableList<Sport>,
    selectedSports: SnapshotStateList<UserSports>,
    onDismissRequest: () -> Unit,
    setShowDialog: (Boolean) -> Unit,
    saveSports: () -> Unit
) {

    val filteredSports = allSports
        .filter { x-> selectedSports.toList().none{x.discipline==it.sportName} }
    val context = LocalContext.current

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest,
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(16.dp),
                shape = RectangleShape
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Your sports",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                                .align(CenterHorizontally)
                        )
                        Row(modifier = Modifier.weight(3f / 7f)) {
                            LazyColumn {
                                items(selectedSports) { sport ->
                                    SportCard(
                                        sportName = sport.sportName,
                                        level = sport.level,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth(),
                                        selectedSports = selectedSports,
                                        add = false
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Add a new sport",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                                .align(CenterHorizontally)

                        )

                        Row(modifier = Modifier.weight(3f / 7f)) {
                            LazyColumn(modifier = Modifier.weight(2f)) {
                                items(filteredSports) { sport ->
                                    SportCard(
                                        sportName = sport.discipline,
                                        level = "None",
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth(),
                                        selectedSports = selectedSports,
                                        add = true,
                                    )
                                }
                            }
                        }


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
                                .weight(1f / 7f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "All preferences Removed!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    selectedSports.clear()
                                    setShowDialog(false)
                                }, colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text(text = "Discard")
                            }
                            Button(onClick = {
                                saveSports()
                                Toast.makeText(context, "Sport list saved", Toast.LENGTH_LONG).show()
                                setShowDialog(false)
                            }) {
                                Text(text = "Save")
                            }
                        }
                    }
                }
        }}
    )
}