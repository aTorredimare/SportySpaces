package it.polito.mad.lab5.friends

import android.net.Uri
import android.os.Build
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import it.polito.mad.lab5.R
import it.polito.mad.lab5.profile.ProfileViewModel

@Composable
fun EditFriends(friendsViewModel: FriendsViewModel, profileViewModel: ProfileViewModel){
    var text by remember { mutableStateOf("") }
    var hasSearchBeenPerformed by remember { mutableStateOf(false) }
    val searchingFriend = friendsViewModel.searchingFriends
    val nick = profileViewModel.nickname.value
    val acceptedFriends = friendsViewModel.friendsId
    val pendingRequests = friendsViewModel.pendingId
    var searchId by remember { mutableStateOf("") }
    val context = LocalContext.current
    BackHandler(enabled = true) {
        friendsViewModel.editFriends.value = false
    }

    Scaffold(
        topBar = { FriendsTopBar(viewModel = friendsViewModel)
        },
        content = { it ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        OutlinedTextField(
                            shape = RoundedCornerShape(32.dp),
                            singleLine = true,
                            label = { Text("Search a friend...") },
                            value = text,
                            onValueChange = { value ->
                                text = value
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if(text != ""){
                                        friendsViewModel.searchFriend(text)
                                        searchId = text
                                        hasSearchBeenPerformed = true
                                    }
                                }
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width( 8.dp))
                }

                if(searchingFriend.isEmpty() && hasSearchBeenPerformed){
                    item{
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No user found, check for typos!"
                        )
                    }
                }

                searchingFriend.forEach { friend ->
                    item {
                        println("Pending: $pendingRequests")
                        println("Pending: $acceptedFriends")
                        var isFriend by remember { mutableStateOf(false) }
                        var isPending by remember { mutableStateOf(false) }
                        friendsViewModel.getUserIdByNickname(friend.nickname) { userId ->
                            isFriend = acceptedFriends.any { it.id == userId }
                            isPending = pendingRequests.any { it.id == userId }
                        }
                        val painter = if(friend.imageUri != "Loading"){
                            rememberAsyncImagePainter(
                                if (friend.imageUri.isEmpty())
                                    R.drawable.baseline_person_24
                                else
                                    Uri.parse(friend.imageUri)
                            )
                        } else {
                            val imageLoader = ImageLoader.Builder(LocalContext.current)
                                .components {
                                    if (Build.VERSION.SDK_INT >= 28) {
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
                            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Image(
                                    painter = painter,
                                    contentDescription = "ProfilePic",
                                    modifier = Modifier
                                        .weight(1f)
                                        .size(64.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(
                                    modifier = Modifier.weight(4f)
                                ) {
                                    Text(text = friend.name, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.padding(top = 8.dp))
                                    Text(text = friend.nickname, fontSize = 12.sp)
                                }
                                if (friend.nickname != nick && !isFriend && !isPending)
                                    Button(
                                        modifier = Modifier.weight(1.5f),
                                        onClick = { friendsViewModel.addPending(friend)
                                            makeText(context, "Friend request sent", Toast.LENGTH_LONG).show()
                                            isFriend = true
                                            isPending = true},
                                        enabled = !isFriend && !isPending
                                    ) {
                                        Text(text = "Add")
                                    }
                                else if (isPending) {
                                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                                        Text(text = "Pending...", modifier = Modifier.padding(8.dp))
                                    }
                                    Spacer(modifier= Modifier.width(8.dp))
                                } else if (isFriend) {
                                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                                        Text(text = "Friend", modifier = Modifier.padding(8.dp))
                                    }
                                    Spacer(modifier= Modifier.width(8.dp))
                                }
                                else{
                                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                                        Text(text = "You", modifier = Modifier.padding(8.dp))
                                    }
                                    Spacer(modifier= Modifier.width(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}