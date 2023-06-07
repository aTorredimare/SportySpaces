package it.polito.mad.lab5.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.polito.mad.lab5.db.UserSports


@Composable
fun SportsTable(
    selectedSports: SnapshotStateList<UserSports>,
    setShowDialog: (Boolean) -> Unit
) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Your Sports",
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(0.75f)
                        )
                            Button(
                            onClick = { setShowDialog(true) }, modifier = Modifier.weight(0.25f)
                            ) {
                            Text(text = "Edit")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Sport",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Skill Level",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }

                    Divider(modifier = Modifier.height(1.dp), color = MaterialTheme.colorScheme.primary)

                    selectedSports.forEach { sport ->

                            Row(modifier = Modifier.fillMaxWidth().padding(top=5.dp)) {
                                Text(
                                    text = sport.sportName,
                                    modifier = Modifier.weight(1f),
                                    fontFamily = FontFamily.Serif,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = sport.level,
                                    modifier = Modifier.weight(1f),
                                    fontFamily = FontFamily.Serif,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
            }
        }

