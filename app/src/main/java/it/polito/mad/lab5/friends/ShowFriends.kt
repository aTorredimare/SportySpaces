package it.polito.mad.lab5.friends

import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults.elevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import it.polito.mad.lab5.db.User
import it.polito.mad.lab5.R
import java.time.ZoneId

@Composable
fun ShowFriends(friendsViewModel: FriendsViewModel){
    val acceptedFriends = friendsViewModel.friendsId
    val pendingRequests = friendsViewModel.pendingId
    val invitations = friendsViewModel.invitations

    Scaffold(
        topBar = { FriendsTopBar(viewModel = friendsViewModel)
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.CenterHorizontally)
        ) {
            if (pendingRequests.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Pending Requests",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(8.dp),
                            fontSize = 26.sp
                        )
                    }
                }

                pendingRequests.forEach { friend ->
                    item {
                        val (user, setUser) = remember(friend.id) {
                            mutableStateOf(User())
                        }
                        LaunchedEffect(friend.id) {
                            friendsViewModel.getUserById(friend.id, setUser)
                        }


                        val painter = if (user.imageUri != "Loading") {
                            rememberAsyncImagePainter(
                                if (user.imageUri.isEmpty())
                                    R.drawable.baseline_person_24
                                else
                                    Uri.parse(user.imageUri)
                            )
                        } else {
                            val imageLoader = ImageLoader.Builder(LocalContext.current)
                                .components {
                                    if (SDK_INT >= 28) {
                                        add(ImageDecoderDecoder.Factory())
                                    } else {
                                        add(GifDecoder.Factory())
                                    }
                                }
                                .build()
                            rememberAsyncImagePainter(R.drawable.loading, imageLoader)
                        }

                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(32.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column {
                                Card(
                                    modifier = Modifier.padding(top = 8.dp, start = 16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    shape = RoundedCornerShape(32.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary
                                    )
                                ) {
                                    Text(
                                        friend.state,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painter,
                                        contentDescription = "ProfilePicA",
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(modifier = Modifier.padding(start = 16.dp)) {
                                        Text(text = user.name, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.padding(top = 8.dp))
                                        Text(text = user.nickname, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        FloatingActionButton(
                                            onClick = { friendsViewModel.deletePending(friend.id) },
                                            shape = RoundedCornerShape(50),
                                            elevation = elevation(16.dp),
                                            containerColor = Color.LightGray,
                                            //contentColor = Color.Red
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = "Remove"
                                            )
                                        }
                                        if (friend.state == "Received") {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            FloatingActionButton(
                                                onClick = { friendsViewModel.addFriend(friend.id) },
                                                shape = RoundedCornerShape(50),
                                                elevation = elevation(16.dp),
                                                containerColor = MaterialTheme.colorScheme.primary,
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = "Add"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(acceptedFriends.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "Friends",
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally),
                            fontSize = 26.sp
                        )
                    }
                }
                acceptedFriends.forEach { friend ->
                    item {
                        val (user, setUser) = remember(friend.id) { mutableStateOf(User()) }
                        LaunchedEffect(friend.id) {
                            friendsViewModel.getUserById(friend.id, setUser)
                        }
                        val painter = if (user.imageUri != "Loading") {
                            rememberAsyncImagePainter(
                                if (user.imageUri.isEmpty())
                                    R.drawable.baseline_person_24
                                else
                                    Uri.parse(user.imageUri)
                            )
                        } else {
                            val imageLoader = ImageLoader.Builder(LocalContext.current)
                                .components {
                                    if (SDK_INT >= 28) {
                                        add(ImageDecoderDecoder.Factory())
                                    } else {
                                        add(GifDecoder.Factory())
                                    }
                                }
                                .build()
                            rememberAsyncImagePainter(R.drawable.loading, imageLoader)
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
                                    Text(text = user.name, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.padding(top = 8.dp))
                                    Text(text = user.nickname, fontSize = 12.sp)
                                }
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Button(onClick = { friendsViewModel.deleteFriend(friend.id) }) {
                                        Text(text = "Remove")
                                    }
                                }
                            }
                        }
                    }

                }
            }

            if(acceptedFriends.isEmpty() && pendingRequests.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(64.dp))
                        Text(
                            text = "Start making friends!",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(8.dp),
                            fontSize = 22.sp
                        )
                        Text(
                            text = "(Click on the top right icon to search)",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(8.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }

            if(invitations.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "Invitations",
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally),
                            fontSize = 26.sp
                        )
                    }

                }

                invitations.forEach { invite ->
                    item {
                        val (user, setUser) = remember(invite.userId) {
                            mutableStateOf(
                                User()
                            )
                        }
                        LaunchedEffect(invite.userId) {
                            friendsViewModel.getUserById(invite.userId, setUser)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            val context = LocalContext.current
                            Row(
                                modifier = Modifier.padding(8.dp).wrapContentHeight(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.padding(8.dp)) {

                                    val formattedDate = invite.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString()
                                    Text(text = "${invite.discipline} at ${invite.playgroundName}", fontSize = 20.sp)
                                    Text(text = "by ${user.nickname}", fontSize = 18.sp)
                                    Text(text = "${formattedDate}, at ${invite.oraInizio} - ${invite.oraFine}", fontSize = 16.sp)
                                }
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    FloatingActionButton(
                                        onClick = {
                                            friendsViewModel.deleteInvitation(invite)
                                            Toast.makeText(context, "Invite refused!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(50),
                                        elevation = elevation(16.dp),
                                        containerColor = Color.LightGray,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Remove"
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    FloatingActionButton(
                                        onClick = {
                                            friendsViewModel.addInvitationToReservations(invite)
                                            friendsViewModel.deleteInvitation(invite)
                                            Toast.makeText(context, "Invite accepted!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(50),
                                        elevation = elevation(16.dp),
                                        containerColor = MaterialTheme.colorScheme.primary,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Add"
                                        )
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