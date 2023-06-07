package it.polito.mad.lab5.friends

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import it.polito.mad.lab5.profile.ProfileViewModel


@SuppressLint("UnrememberedMutableState")
@Composable
fun Friends(friendsViewModel: FriendsViewModel, profileViewModel: ProfileViewModel) {

    val editFriends = friendsViewModel.editFriends.value

    if(editFriends){
        EditFriends(friendsViewModel = friendsViewModel, profileViewModel)
    }
    else {
        ShowFriends(friendsViewModel = friendsViewModel)
    }
}


