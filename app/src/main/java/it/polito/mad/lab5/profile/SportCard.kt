package it.polito.mad.lab5.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.textButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import it.polito.mad.lab5.db.UserSports

@Composable
fun SportCard(
    sportName: String,
    level: String,
    modifier: Modifier = Modifier,
    add: Boolean,
    selectedSports: SnapshotStateList<UserSports>
) {
    // val context = LocalContext.current
    // val (expandable,setExpandable) = remember {mutableStateOf(false)}
    // val (level, setLevel) = remember { mutableStateOf(level) }
    val (showLevelDialog, setShowLevelDialog) = remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .height(100.dp)
            .width(150.dp)
            .clickable {
                if (add) {
                    setShowLevelDialog(true)
                } else {
                    val sportFound = selectedSports.find { it.sportName == sportName }
                    selectedSports.remove(sportFound)
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    ) {
        Row {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(0.80f)
                    //.background(Color.Green)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                if (add) {
                    Text(
                        text = sportName,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            //.background(Color.Red)
                    )
                    if (showLevelDialog) {
                        Dialog(
                            properties = DialogProperties(usePlatformDefaultWidth = true),
                            onDismissRequest = { setShowLevelDialog(false) },
                            content = {
                                Card(
                                    modifier = modifier
                                        //.height(100.dp)
                                        .width(100.dp)
                                        .clickable {},
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    )
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Select your ability level",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp,
                                            modifier = Modifier
                                                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                                                .align(Alignment.CenterHorizontally)
                                        )
                                        Button(
                                            modifier = Modifier
                                                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                                                .fillMaxWidth(),
                                            colors = textButtonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            onClick = {
                                                selectedSports.add(UserSports(sportName = sportName, level = "Beginner"))
                                                setShowLevelDialog(false)
                                            }
                                        ){
                                            Text(text = "Beginner")
                                        }
                                        Button(
                                            modifier = Modifier
                                                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                                                .fillMaxWidth(),
                                            colors = textButtonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            onClick = {
                                                selectedSports.add(UserSports(sportName = sportName, level = "Intermediate"))
                                                setShowLevelDialog(false)
                                            }
                                        ){
                                            Text(text = "Intermediate")
                                        }
                                        Button(
                                            modifier = Modifier
                                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                                .fillMaxWidth(),
                                            colors = textButtonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            ),
                                            onClick = {
                                                selectedSports.add(UserSports(sportName = sportName, level = "Advanced"))
                                                setShowLevelDialog(false)
                                            }
                                        ){
                                            Text(text = "Advanced")
                                        }
                                    }
                                }
                            }
                        )
                    }
                } else {
                    Text(
                        text = sportName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Ability Level:",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = level,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(0.20f)
                    .align(CenterVertically)
                    .padding(end = 8.dp)
            ) {
                if (add) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
