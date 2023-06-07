package it.polito.mad.lab5.rent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.polito.mad.lab5.db.TimeSlot

@Composable
fun ReservationList(data : List<TimeSlot>, onTimeSlotClick: (TimeSlot) -> Unit) {
    val columns = 4

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.height(200.dp).padding(16.dp)
    ) {
        items(data) { item ->
            ReservationCard(rent = item, onClick = { onTimeSlotClick(item) })
        }
    }
}

@Composable
fun ReservationCard(rent : TimeSlot, onClick: () -> Unit) {
    Card(
       shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("${rent.oraInizio}")
            Text("-${rent.oraFine}")
        }
    }
}
