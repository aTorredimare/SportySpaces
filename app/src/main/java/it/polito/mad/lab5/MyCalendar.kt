package it.polito.mad.lab5

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MyCalendar(selectedDate: LocalDate?, setSelectedDate: (LocalDate?)->Unit, isColored: (CalendarDay)->Boolean, backgroundColor: Color = Color.Transparent, isEdit:Boolean = false) {

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed

    val daysOfWeek = remember { daysOfWeek() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid
    )
    HorizontalCalendar(
        state = state,
        dayContent = { day ->
            Day(day,
                isSelected = selectedDate == day.date,
                isColored = isColored(day),
                backgroundColor = backgroundColor,
                onClick =  { d ->
                    setSelectedDate(d.date)
                }
            )
        },
        monthHeader = {
                month -> MonthHeader(calendarMonth = month, daysOfWeek = daysOfWeek)
        },
        // Draw the day content gradient.
        monthBody = { _, content ->
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                content() // Render the provided content!
            }
        },
        // Add the corners/borders and month width.
        monthContainer = { _, container ->
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            Box(
                modifier = Modifier
                    .width(if(!isEdit) screenWidth else screenWidth * 0.75f )
                    .padding(16.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .border(
                        color = Color.Black,
                        width = 1.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                container() // Render the provided container!
            }
        }
    )

}

@Composable
private fun MonthHeader(calendarMonth: CalendarMonth, daysOfWeek: List<DayOfWeek>) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(top = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = calendarMonth.yearMonth.format(DateTimeFormatter.ofPattern("yyyy MMMM")).toString(),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            for (dayOfWeek in daysOfWeek) {
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Divider(color = Color.Black)
    }
}

@Composable
fun Day(day: CalendarDay, isSelected: Boolean, isColored: Boolean, backgroundColor: Color, onClick: (CalendarDay) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(color = if (isSelected) MaterialTheme.colorScheme.primary
                else if (isColored && (day.position == DayPosition.MonthDate)) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .clickable(
                enabled = (day.position == DayPosition.MonthDate) && (!day.date.isBefore(LocalDate.now())) ,
                onClick = { onClick(day) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = if(isSelected) MaterialTheme.colorScheme.onPrimary
                else if ((day.position == DayPosition.MonthDate) && (!day.date.isBefore(LocalDate.now()))) Color.Black
                else Color.Gray
        )
    }
}