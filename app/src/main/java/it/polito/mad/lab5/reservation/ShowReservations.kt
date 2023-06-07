package it.polito.mad.lab5.reservation

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import it.polito.mad.lab5.MyCalendar
import it.polito.mad.lab5.R
import it.polito.mad.lab5.db.TimeSlot
import it.polito.mad.lab5.db.User
import it.polito.mad.lab5.db.Reservation
import it.polito.mad.lab5.friends.FriendsViewModel
import it.polito.mad.lab5.rent.Rent
import it.polito.mad.lab5.rent.RentViewModel
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reservation(vm: ShowReservationsViewModel, rentVm: RentViewModel,friendVM : FriendsViewModel) {
    val reservations = vm.reservations
    val dateList = reservations.map { it.date }
    val (selectedDate, setSelectedDate) = remember { mutableStateOf<LocalDate?>(LocalDate.now()) }

    val (reservationToEdit, setReservationToEdit) = remember { mutableStateOf(Reservation()) }
    val (reservationToAddFriend,setReservationToAddFriend) = remember {
        mutableStateOf(Reservation())
    }
    //val showDialog = vm.showDialog.value
    val selectedDateReservations = reservations
        .filter { it.date == Date.from(selectedDate?.atStartOfDay(ZoneOffset.systemDefault())?.toInstant() ) }

    val acceptedFriends = friendVM.friendsId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Browse your reservations", textAlign = TextAlign.Center)},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary)
            )
        },
        content = { it ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    MyCalendar(selectedDate, setSelectedDate, {
                        var found = false
                        for (d in dateList) {
                            if (d == Date.from(
                                    it.date.atStartOfDay(ZoneOffset.systemDefault()).toInstant()
                                )
                            ) {
                                found = true
                                break
                            }
                        }
                        found
                    }, Color.Gray)
                }

                items(items = selectedDateReservations.sortedBy { it.oraInizio }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        val (expanded, setExpanded) = remember { mutableStateOf(false) }
                        val context = LocalContext.current

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable { setExpanded(!expanded) },
                            //.align(Alignment.CenterHorizontally)
                        ){
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${it.oraInizio}:00 - ${it.oraFine}:00",
                                    fontSize = 20.sp,
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${it.discipline} at ${it.playgroundName}",
                                    fontSize = 18.sp,
                                )
                            }

                            if (it.customRequest != "") {
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Custom request: ${it.customRequest}",
                                        fontSize = 16.sp,
                                    )

                                }
                            }

                            if(expanded) {
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = {
                                            setReservationToAddFriend(it)
                                            //TODO invite to reservation mechanism
                                        },
                                        modifier = Modifier.padding(8.dp),
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Share,
                                            contentDescription = "Share Reservation"
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            setReservationToEdit(it)
                                            //setShowEditReservation(true)
                                        },
                                        modifier = Modifier.padding(8.dp),
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Edit,
                                            contentDescription = "Edit Reservation"
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            vm.deleteReservation(
                                                it.date,
                                                it.discipline,
                                                it.oraInizio,
                                                it.playgroundName
                                            )
                                            Toast.makeText(context, "Reservation deleted!", Toast.LENGTH_LONG).show()
                                        },
                                        modifier = Modifier.padding(8.dp),
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFF808C)
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = "Delete Reservation"
                                        )
                                    }
                                }
                            }

                        }
                    }
                }
            }
            if(reservationToEdit.discipline != ""){
                Dialog(
                    onDismissRequest = {
                        rentVm.isEdit.value = false
                        setReservationToEdit(Reservation())
                    },
                    content = {
                        rentVm.fetchAllSports()
                        rentVm.selectedSport.value = reservationToEdit.discipline
                        rentVm.loadPlaygrounds()
                        rentVm.selectedPlayground.value = reservationToEdit.playgroundName
                        rentVm.loadFullDates()
                        rentVm.selectedDate.value = reservationToEdit.date
                        rentVm.selectedTimeSlot.value = TimeSlot(reservationToEdit.oraInizio, reservationToEdit.oraFine)
                        rentVm.loadFreeSlots()
                        rentVm.customRequest.value = reservationToEdit.customRequest
                        rentVm.reservationToUpdateId.value = reservationToEdit.reservationId
                        rentVm.isEdit.value = true
                        rentVm.setReservationEditDialog.value = setReservationToEdit
                        Rent(rentVm)
                    }
                )
            }
            if(reservationToAddFriend.discipline!=""){
                vm.loadInvitationsList(reservationToAddFriend.reservationId)
                Dialog(
                    properties = DialogProperties(usePlatformDefaultWidth = false),
                    onDismissRequest = { setReservationToAddFriend(Reservation()) },
                    content = {
                        Surface(
                            modifier = Modifier.padding(16.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (acceptedFriends.isEmpty()) {
                                Text(text = "No available friend!")
                            }
                            else{
                                val uidAlreadySent = vm.uidAlreadySent
                                LazyColumn {
                                    acceptedFriends.forEach { friend ->
                                        item {
                                            val (user, setUser) = remember(friend.id) {
                                                mutableStateOf(
                                                    User()
                                                )
                                            }
                                            LaunchedEffect(friend.id) {
                                                friendVM.getUserById(friend.id, setUser)
                                            }
                                            val painter = if (user.imageUri != "Loading") {
                                                rememberAsyncImagePainter(
                                                    if (user.imageUri.isEmpty())
                                                        R.drawable.baseline_person_24
                                                    else
                                                        Uri.parse(user.imageUri)
                                                )
                                            } else {
                                                val imageLoader =
                                                    ImageLoader.Builder(LocalContext.current)
                                                        .components {
                                                            if (Build.VERSION.SDK_INT >= 28) {
                                                                add(ImageDecoderDecoder.Factory())
                                                            } else {
                                                                add(GifDecoder.Factory())
                                                            }
                                                        }
                                                        .build()
                                                rememberAsyncImagePainter(
                                                    R.drawable.loading,
                                                    imageLoader
                                                )
                                            }

                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                                                shape = RoundedCornerShape(32.dp),
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Image(
                                                        painter = painter,
                                                        contentDescription = "ProfilePic",
                                                        modifier = Modifier
                                                            .size(64.dp)
                                                            .clip(CircleShape),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                    Column(modifier = Modifier.padding(start = 16.dp)) {
                                                        Text(text = user.nickname, fontSize = 20.sp)
                                                        Spacer(modifier = Modifier.padding(top = 8.dp))
                                                        Text(text = user.name, fontSize = 12.sp)
                                                    }
                                                    Row(
                                                        horizontalArrangement = Arrangement.End,
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        if (uidAlreadySent.contains(friend.id)) {
                                                            Button(onClick = { },
                                                                    enabled = false) {
                                                                Text(text = "Sent")
                                                            }
                                                        }
                                                        else {
                                                            Button(onClick = {
                                                                vm.sendInvitation(
                                                                    reservationToAddFriend,
                                                                    friend.id
                                                                )
                                                            }) {
                                                                Text(text = "Invite")
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                )
            }
        }
    )
}
