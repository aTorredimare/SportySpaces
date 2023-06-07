package it.polito.mad.lab5.friends

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import it.polito.mad.lab5.db.Friend
import it.polito.mad.lab5.db.Pending
import it.polito.mad.lab5.db.User
import it.polito.mad.lab5.db.Reservation
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class FriendsViewModel(application: Application) : AndroidViewModel(application) {

    private var db = Firebase.firestore

    val editFriends = mutableStateOf(false)
    val friendsId : SnapshotStateList<Friend> = mutableStateListOf()
    val pendingId : SnapshotStateList<Pending> = mutableStateListOf()
    val searchingFriends : SnapshotStateList<User> = mutableStateListOf()

    val invitations: SnapshotStateList<Reservation> = mutableStateListOf()

    val text = mutableStateOf("")
    fun fetchInitialData() {
        loadFriends()
        loadPending()
        loadInvitations()
    }

    suspend fun getUserById(id : String, setUser: (User) -> Unit) {
        val userDocRef = db.collection("Users").document(id)
        return try {
            val documentSnapshot = userDocRef.get().await()
            if (documentSnapshot.exists()) {
                val retrievedUser = documentSnapshot.toObject(User::class.java)!!
                if(retrievedUser.imageUri != ""){
                    loadUserProfileImage(id, retrievedUser, setUser)
                    retrievedUser.imageUri = "Loading"
                }
                setUser(retrievedUser)
            } else {
                setUser(User())
            }
        } catch (exception: Exception) {
            setUser(User())
        }
    }

    private fun loadUserProfileImage(id: String, user: User, setUser: (User) -> Unit){
        val storageReference = FirebaseStorage.getInstance().getReference("profileImages/$id.jpg")
        val localProfileImageFile = File.createTempFile("localProfileImage", ".jpg")
        Log.d(TAG, "Start downloading Friend Profile Image")
        storageReference
            .getFile(localProfileImageFile)
            .addOnSuccessListener {
                Log.d(TAG, "Friend Profile Image downloaded successfully")
                val userWithImage = User(
                    user.name,
                    user.nickname,
                    user.email,
                    user.birthdate,
                    user.sex,
                    user.city,
                    localProfileImageFile.toUri().toString()
                )
                Log.d(TAG, "Profile Image stored to ${userWithImage.imageUri}")
                setUser(userWithImage)
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Profile image download error: $e")
            }
    }

    fun getUserIdByNickname(nickname: String, callback: (String?) -> Unit) {
        val query = db.collection("Users").whereEqualTo("nickname", nickname).limit(1)
        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]
                val userId = document.id
                callback(userId)
            } else {
                callback(null)
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting user ID by nickname: $exception")
            callback(null)
        }
    }

    fun loadInvitations(){
        viewModelScope.launch(){
            invitations.clear()
            db.collection("Users/${Firebase.auth.uid}/Invitations")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "listen:error", e)
                    }
                    if(snapshots != null) {
                        for (dc in snapshots.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    Log.d(TAG, "New invitation: ${dc.document.data}")
                                    invitations.add(dc.document.toObject(Reservation::class.java))
                                }

                                DocumentChange.Type.MODIFIED -> {
                                    val updatedRes = dc.document.toObject(Reservation::class.java)
                                    invitations.removeIf {
                                        it.reservationId == updatedRes.reservationId
                                    }
                                    invitations.add(updatedRes)
                                    Log.d(TAG, "Modified Invitation: ${dc.document.data}")
                                }

                                DocumentChange.Type.REMOVED -> {
                                    Log.d(TAG, "Removed Invitation: ${dc.document.data}")
                                    val invitationToDelete = dc.document.toObject(Reservation::class.java)
                                    invitations.removeIf {
                                        it.reservationId == invitationToDelete.reservationId
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    fun addInvitationToReservations(invitation: Reservation){
        deleteInvitation(invitation)
        db.collection("Users/${Firebase.auth.uid}/Reservations").add(invitation)
    }

    fun deleteInvitation(invitation: Reservation){
        invitations.removeIf { it.reservationId == invitation.reservationId }

        db.collection("Users/${Firebase.auth.uid}/Invitations")
            .whereEqualTo("reservationId", invitation.reservationId)
            .get()
            .addOnSuccessListener {
                if(it.documents.size>0) {

                    db.collection("Users/${Firebase.auth.uid}/Invitations")
                        .document(it.documents[0].id)
                        .delete()
                        .addOnSuccessListener {
                            Log.d(TAG, "Invitation removed successfully")
                        }
                        .addOnFailureListener {
                            Log.w(TAG, "Error removing invitation")
                        }
                }
            }
    }

    private fun loadFriends(){
        viewModelScope.launch {
            friendsId.clear()
            db.collection("Users/${Firebase.auth.uid}/Friends")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "listen:error", e)
                    }
                    if(snapshots != null) {
                        for (dc in snapshots.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    Log.d(TAG, "New Friend: ${dc.document.data}")
                                    friendsId.add(dc.document.toObject(Friend::class.java))
                                }

                                DocumentChange.Type.MODIFIED -> {
                                    val updatedRes = dc.document.toObject(Friend::class.java)
                                    friendsId.removeIf {
                                        it.id == updatedRes.id
                                    }
                                    friendsId.add(updatedRes)
                                    Log.d(TAG, "Modified Friend: ${dc.document.data}")
                                }

                                DocumentChange.Type.REMOVED -> {
                                    Log.d(TAG, "Removed Friend: ${dc.document.data}")
                                    val friendToDelete = dc.document.toObject(Friend::class.java)
                                    friendsId.removeIf {
                                        it.id == friendToDelete.id
                                    }
                                }
                            }
                        }
                    }
                }


        }

    }
    private fun loadPending(){
        viewModelScope.launch (){
            friendsId.clear()
            db.collection("Users/${Firebase.auth.uid}/Pending")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "listen:error", e)
                    }
                    if(snapshots != null) {
                        for (dc in snapshots.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    Log.d(TAG, "New Pending: ${dc.document.data}")
                                    pendingId.add(dc.document.toObject(Pending::class.java))
                                }

                                DocumentChange.Type.MODIFIED -> {
                                    val updatedRes = dc.document.toObject(Pending::class.java)
                                    pendingId.removeIf {
                                        it.id == updatedRes.id
                                    }
                                    pendingId.add(updatedRes)
                                    Log.d(TAG, "Modified Pending: ${dc.document.data}")
                                }

                                DocumentChange.Type.REMOVED -> {
                                    Log.d(TAG, "Removed Pending: ${dc.document.data}")
                                    val pendingToDelete = dc.document.toObject(Pending::class.java)
                                    pendingId.removeIf {
                                        it.id == pendingToDelete.id
                                    }
                                }
                            }
                        }
                    }
                }


        }

    }
    fun searchFriend(ref:String){
        viewModelScope.launch {
            searchingFriends.clear()
            db.collection("Users").whereEqualTo("nickname",ref)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "listen:error", e)
                    }
                    if(snapshots != null) {
                        println("Lo snapshot non e' nullo!")
                        println("Il numero di documenti cambiati e' " + snapshots.documentChanges.size)
                        for (dc in snapshots.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    Log.d(TAG, "New User: ${dc.document.data}")
                                    searchingFriends.add(dc.document.toObject(User::class.java))
                                }

                                DocumentChange.Type.MODIFIED -> {
                                    val updatedRes = dc.document.toObject(User::class.java)
                                    searchingFriends.removeIf {
                                        it.nickname == updatedRes.nickname
                                    }
                                    searchingFriends.add(updatedRes)
                                    Log.d(TAG, "Modified User: ${dc.document.data}")
                                }

                                DocumentChange.Type.REMOVED -> {
                                    Log.d(TAG, "Removed User: ${dc.document.data}")
                                    val userToDelete = dc.document.toObject(User::class.java)
                                    searchingFriends.removeIf {
                                        it.nickname == userToDelete.nickname
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }
    fun addPending(id: User){
        val myid= Firebase.auth.uid
        if(myid!= null) {
            viewModelScope.launch {
                db.collection("Users")
                    .whereEqualTo("birthdate", id.birthdate)
                    .whereEqualTo("city", id.city)
                    .whereEqualTo("email", id.email)
                    .whereEqualTo("imageUri", id.imageUri)
                    .whereEqualTo("name", id.name)
                    .whereEqualTo("nickname", id.nickname)
                    .whereEqualTo("sex", id.sex)
                    .get()
                    .addOnSuccessListener { userdocument ->
                        db.collection("Users/${Firebase.auth.uid}/Pending")
                            .add(Pending(id = userdocument.documents[0].id, state = "Sent"))
                            .addOnSuccessListener {
                                Log.d(TAG, "Friend Added for the main user")

                                db.collection("Users/${userdocument.documents[0].id}/Pending")
                                    .add(Pending(id =myid,state="Received"))
                                    .addOnSuccessListener {
                                        Log.d(TAG, "Friend Added for the other user")

                                    }
                                    .addOnFailureListener {
                                        Log.d(
                                            TAG,
                                            "Error adding the friend for the other user"
                                        )

                                    }
                            }
                            .addOnFailureListener {
                                Log.d(
                                    TAG,
                                    "Error Adding the friend for the main user"
                                )
                            }
                    }


            }
        }
    }
    fun deleteFriend(id:String){
        viewModelScope.launch {
            db.collection("Users/${Firebase.auth.uid}/Friends")
                .whereEqualTo("id",id).get()
                .addOnSuccessListener { friendDocument ->

                    db.collection("Users/${Firebase.auth.uid}/Friends")
                        .document(friendDocument.documents[0].id)
                        .delete()
                        .addOnSuccessListener {

                            Log.d(TAG, "Friend Removed for the main user")

                            db.collection("Users/${id}/Friends").whereEqualTo("id",Firebase.auth.uid).get()
                                .addOnSuccessListener {otherfrienddocument ->
                                    db.collection("Users/${id}/Friends").document(otherfrienddocument.documents[0].id).delete()
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Friend Removed for the other user")

                                        }
                                        .addOnFailureListener {
                                            Log.d(TAG, "Error removing the friend for the other user")

                                        }
                                }
                                .addOnFailureListener{
                                    Log.d(TAG, "Error seeking  for the other user document")
                                }

                        }
                        .addOnFailureListener {

                            Log.d(TAG, "Error removing the friend for the main user")

                        }

                }
                .addOnFailureListener {
                    Log.d(TAG, "Error seeking for the friend of the main user")
                }
        }
    }
    fun deletePending(id:String){
        val myid= Firebase.auth.uid

        if(myid!=null){

            viewModelScope.launch {
                db.collection("Users/${Firebase.auth.uid}/Pending").whereEqualTo("id",id).get()
                    .addOnSuccessListener {
                        db.collection("Users/${Firebase.auth.uid}/Pending").document(it.documents[0].id).delete()
                            .addOnSuccessListener {
                                db.collection("Users/${id}/Pending").whereEqualTo("id",myid).get()
                                    .addOnSuccessListener { it2->
                                        if(it2.documents.size>0)
                                            db.collection("Users/${id}/Pending").document(it2.documents[0].id).delete()
                                                .addOnSuccessListener {
                                                    Log.d(TAG, "Deleted Friend for the main user and the secondary user")

                                                }
                                    }
                            }
                    }
            }
        }
    }
    fun addFriend(id:String){
        val myid=Firebase.auth.uid
        if(myid!=null){
            viewModelScope.launch{
                db.collection("Users/${Firebase.auth.uid}/Friends").add(Friend(id=id))
                    .addOnSuccessListener {
                        db.collection("Users/${Firebase.auth.uid}/Pending").whereEqualTo("id",id).get()
                            .addOnSuccessListener {
                                if(it.documents.size>0)
                                    db.collection("Users/${Firebase.auth.uid}/Pending").document(it.documents[0].id).delete()
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Added Friend for the main user")

                                        }
                                        .addOnFailureListener {
                                            println("Errore nel cancellare la richiesta pending")
                                        }
                            }

                    }
                db.collection("Users/${id}/Friends").add(Friend(id=myid))
                    .addOnSuccessListener {
                        db.collection("Users/${id}/Pending").whereEqualTo("id",myid).get()
                            .addOnSuccessListener {
                                if(it.documents.size>0) {
                                    db.collection("Users/${id}/Pending").document(it.documents[0].id)
                                        .delete()
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Added Friend for the secondary user")

                                        }
                                }
                            }
                    }
            }

        }
    }



}