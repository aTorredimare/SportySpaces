package it.polito.mad.lab5.profile

import android.app.Application
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import it.polito.mad.lab5.db.Sport
import it.polito.mad.lab5.db.User
import it.polito.mad.lab5.db.UserSports
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File


class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private var dbReal = Firebase.firestore
    var selectedSports: SnapshotStateList<UserSports> = mutableStateListOf()
    var allSports : SnapshotStateList<Sport> = mutableStateListOf()
    val name = mutableStateOf("")
    val nickname = mutableStateOf("")
    val email = mutableStateOf(Firebase.auth.currentUser!!.email!!)
    val birthdate = mutableStateOf("")
    val sex = mutableStateOf("")
    val city = mutableStateOf("")
    val imageUri = mutableStateOf("")
    val changePhotoExpanded = mutableStateOf(false)
    val editMode = mutableStateOf(false)
    var showDialog = mutableStateOf(false)


    fun fetchInitialData() {
        fetchUser()
        fetchUserSports()
        fetchAllSports()
    }

    private fun saveProfileImage(){

        val storageReference = FirebaseStorage.getInstance().reference.child("profileImages/${Firebase.auth.uid}.jpg")
        if (imageUri.value.isNotEmpty()) {
            storageReference
                .putFile(Uri.parse(imageUri.value))
                .addOnSuccessListener { Log.d(TAG, "Profile Image loaded successfully") }
                .addOnFailureListener { e -> Log.w(TAG, "Profile Image loading error: $e") }
        }
    }

    private fun loadProfileImage() {
        val storageReference = FirebaseStorage.getInstance().getReference("profileImages/${Firebase.auth.uid}.jpg")
        val localProfileImageFile = File.createTempFile("localProfileImage", ".jpg")
        storageReference
            .getFile(localProfileImageFile)
            .addOnSuccessListener {
                Log.d(TAG, "Profile Image downloaded successfully")
                imageUri.value = localProfileImageFile.toUri().toString()
                Log.d(TAG, "Profile Image stored to ${imageUri.value}")
            }
            .addOnFailureListener { e: Exception ->
                Log.w(TAG, "Profile image download error: $e")
            }
    }

    private fun fetchUser() {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userDocument =
                    dbReal.collection("Users").document(Firebase.auth.uid!!).get().await()
                val downloadedUser = userDocument.toObject(User::class.java)
                Log.d(TAG, "Downloaded User ${downloadedUser.toString()}")

                name.value = downloadedUser!!.name
                nickname.value = downloadedUser.nickname
                email.value = downloadedUser.email
                birthdate.value = downloadedUser.birthdate
                sex.value = downloadedUser.sex
                city.value = downloadedUser.city
                imageUri.value = downloadedUser.imageUri

                Log.d(TAG, "User fetched successfully")

                if(imageUri.value != "")
                    loadProfileImage()
            } catch (e: Exception) {
                Log.w(TAG, "Error fetching user: $e")

            }
        }

    }
    fun fetchUserSports() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userSportDocument = dbReal.collection("Users/${Firebase.auth.uid}/Sports").get().await()
                selectedSports.clear()

                for (document in userSportDocument){
                    document.data
                    val userSport = document.toObject(UserSports::class.java)
                    Log.d(TAG, "Downloaded userSport: ${document.data} ${userSport.sportName} ${userSport.level}")
                    selectedSports.add(userSport)
                }
                Log.d(TAG, "UserSports fetched successfully")

            } catch (e: Exception) {
                Log.w(TAG, "Error fetching UserSports: $e")
            }
        }
    }
    private fun fetchAllSports() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sportDocuments = dbReal.collection("Sport").get().await()
                allSports.clear()
                for (document in sportDocuments){
                    val sport = document.toObject(Sport::class.java)
                    allSports.add(sport)
                }
                Log.d(TAG, "AllSports fetched successfully")
            } catch (e: Exception) {
                Log.w(TAG, "Error fetching AllSports: $e")
            }
        }
    }
    fun updateUser() {
        val userDocument = dbReal
            .collection("Users")
            .document(Firebase.auth.uid!!)

        userDocument
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    userDocument
                        .update(
                            "name", name.value,
                            "nickname", nickname.value,
                            "birthdate", birthdate.value,
                            "city", city.value,
                            "email", email.value,
                            "sex", sex.value,
                            "imageUri", imageUri.value
                        )
                        .addOnSuccessListener{
                            Log.d(TAG, "User updated successfully")
                        }
                        .addOnFailureListener {e ->
                            Log.w(TAG, "Error updating user: $e")
                        }

                }
                else {
                    val newUser = User(
                        name = name.value,
                        nickname = nickname.value,
                        email = email.value,
                        birthdate = birthdate.value,
                        sex = sex.value,
                        city = city.value,
                        imageUri = imageUri.value
                    )
                    userDocument
                        .set(newUser)
                        .addOnSuccessListener{
                            Log.d(TAG, "User added successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding user: $e")
                        }
                }
            }
        if(imageUri.value != "")
            saveProfileImage()
    }
    fun updateUserSports() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val batch = dbReal.batch()

                val userSports = dbReal.collection("Users/${Firebase.auth.uid}/Sports")

                userSports.get()
                    .addOnSuccessListener { querySnapshot ->

                        for (document in querySnapshot) {
                            batch.delete(document.reference)
                        }
                        for(UserSport in selectedSports)
                        {
                            val id = userSports.document()
                            val newDocument = hashMapOf(
                                "sportName" to UserSport.sportName,
                                "level" to UserSport.level,
                            )
                            batch.set(id,newDocument)
                        }

                        batch.commit()
                            .addOnSuccessListener {
                                Log.d(TAG, "User Sports updated successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error during Sports update: ${e.message}")
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error during Sports fetching: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.w(TAG, "Exception occurred = $e")
            }
        }
    }

}