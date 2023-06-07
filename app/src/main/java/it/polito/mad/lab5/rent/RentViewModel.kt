package it.polito.mad.lab5.rent

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import it.polito.mad.lab5.db.TimeSlot
import it.polito.mad.lab5.db.PlayGrounds
import it.polito.mad.lab5.db.Sport
import it.polito.mad.lab5.db.Reservation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date


class RentViewModel(application: Application) : AndroidViewModel(application) {
    val setReservationEditDialog = mutableStateOf<(Reservation) -> Unit>({})
    var db = FirebaseFirestore.getInstance()

    val sportsList1 = mutableStateListOf<String>()
    val playgroundNamesList = mutableStateListOf<String>()
    val playgroundsList = mutableStateListOf<PlayGrounds>()
    val fullDates = mutableListOf<Date>()
    val freeSlots = mutableStateListOf<TimeSlot>()
    val selectedSport = mutableStateOf("Sport")
    val selectedPlayground = mutableStateOf("")
    val selectedDate = mutableStateOf<Date?>(null)
    val selectedTimeSlot = mutableStateOf<TimeSlot?>(null)
    val customRequest = mutableStateOf("")
    val reservationToUpdateId = mutableStateOf("")

    private lateinit var reg: ListenerRegistration
    private lateinit var regFullDates: ListenerRegistration
    private val datesMap = mutableMapOf<Date, Int>()

    val isEdit = mutableStateOf(false)

    fun resetValues(){
        selectedSport.value = "Sport"
        selectedPlayground.value = ""
        selectedDate.value = null
        customRequest.value = ""
    }

    fun loadFreeSlots() {
        try {
            reg.remove()
        }
        catch (_: Exception){}

        Log.i(TAG, "reg listener removed")
        db.collection("TimeSlot")
            .orderBy("oraInizio")
            .get()
            .addOnSuccessListener { timeSlotDocument ->
                freeSlots.clear()
                for (tsd in timeSlotDocument){
                    val timeSlot = tsd.toObject<TimeSlot>()
                    freeSlots.add(TimeSlot(timeSlot.oraInizio,timeSlot.oraFine))
                }

                viewModelScope.launch(Dispatchers.IO) {
                    //reg = db.collection("Users/${Firebase.auth.uid}/Reservations")
                    Log.i(TAG, "start freeSlot listener")
                    reg = db.collection("Reservations")
                        .whereEqualTo("playgroundName", selectedPlayground.value)
                        .whereEqualTo("date", selectedDate.value)
                        .addSnapshotListener { snapshots, e ->
                            if (e != null) {
                                Log.w(TAG, "listen:error", e)
                            }
                            if(snapshots != null) {
                                for (dc in snapshots.documentChanges) {
                                    when (dc.type) {
                                        DocumentChange.Type.ADDED -> {
                                            Log.d(TAG, "New reservation: ${dc.document.data}")
                                            val reservationToDelete = dc.document.toObject(Reservation::class.java)
                                            freeSlots.removeIf {
                                                (it.oraInizio == reservationToDelete.oraInizio) && !(isEdit.value && (it.oraInizio == selectedTimeSlot.value!!.oraInizio))
                                            }
                                        }

                                        DocumentChange.Type.MODIFIED -> {
                                            Log.d(TAG, "Modified reservation: ${dc.document.data}")
                                        }

                                        DocumentChange.Type.REMOVED -> {
                                            Log.d(TAG, "Removed reservation: ${dc.document.data}")
                                            val res = dc.document.toObject(Reservation::class.java)
                                            freeSlots.add(TimeSlot(res.oraInizio, res.oraFine))
                                            freeSlots.sortBy { it.oraInizio }
                                        }
                                    }
                                }
                            }
                        }
                }
            }
            .addOnFailureListener{
                Log.w(TAG, "Error loading free slots: $it")
            }
    }



    fun fetchAllSports() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sportsDocuments = db.collection("Sport").get().await()
                sportsList1.clear()
                for (sportDoc in sportsDocuments){
                    val sportName = sportDoc.toObject<Sport>().discipline
                    sportsList1.add(sportName)
                }
            } catch (e: Exception) {
                println("Exception occurred = $e")
            }
        }
    }

    fun loadPlaygrounds() {
        db.collection("Playground")
            .whereEqualTo("sportName", selectedSport.value)
            .get()
            .addOnSuccessListener { documents ->
                //val playgroundsList = mutableListOf<String>()
                playgroundNamesList.clear()
                playgroundsList.clear()
                for (document in documents) {
                    Log.d(TAG, "Document fetched for playground: ${document.data}")
                    playgroundNamesList.add(document.getString("playgroundName") ?: "")
                    playgroundsList.add(document.toObject(PlayGrounds::class.java))
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun loadFullDates() {
        datesMap.clear()
        fullDates.clear()
        viewModelScope.launch(Dispatchers.IO) {
            //reg = db.collection("Users/${Firebase.auth.uid}/Reservations")
            reg = db.collection("Reservations")
                .whereEqualTo("playgroundName", selectedPlayground.value)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w(TAG, "listen:error", e)
                    }
                    if(snapshots != null) {
                        for (dc in snapshots.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    Log.d(TAG, "New reservation: ${dc.document.data}")
                                    val reservation = dc.document.toObject(Reservation::class.java)
                                    val date = reservation.date // assuming 'date' field in Reservation class
                                    datesMap[date] = datesMap.getOrDefault(date, 0) + 1
                                }

                                DocumentChange.Type.MODIFIED -> {
                                    Log.d(TAG, "Modified reservation: ${dc.document.data}")
                                }

                                DocumentChange.Type.REMOVED -> {
                                    Log.d(TAG, "Removed reservation: ${dc.document.data}")
                                    val reservation = dc.document.toObject(Reservation::class.java)
                                    val date = reservation.date // assuming 'date' field in Reservation class
                                    datesMap[date] = datesMap.getOrDefault(date, 1) - 1
                                }
                            }
                            fullDates.addAll(datesMap.filter { it.value >= 12 }.keys.toList())
                        }
                    }
                }
        }
    }

    fun saveReservation(/*reservation: Reservation*/) {
        val reservation = Reservation(
            reservationId = "",
            userId = Firebase.auth.uid!!,
            discipline = selectedSport.value,
            playgroundName = selectedPlayground.value,
            date = selectedDate.value!!,
            oraInizio = selectedTimeSlot.value!!.oraInizio,
            oraFine = selectedTimeSlot.value!!.oraFine,
            customRequest = customRequest.value
        )
        if(!isEdit.value) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    db.collection("Reservations")
                        .add(reservation)
                        .addOnSuccessListener {
                            reservation.reservationId =
                                it.id   //inside user reservation, save a pointer to the same reservation inside 'Reservations' collection

                            db.collection("Users/${Firebase.auth.uid}/Reservations")
                                .add(reservation)
                                .addOnSuccessListener { documentReference ->
                                    Log.d(
                                        TAG,
                                        "DocumentSnapshot written with ID: ${documentReference.id}"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    db.collection("Reference").document(it.id).delete()
                                    Log.w(TAG, "Error adding document to 'User/Reservations': $e")
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document to 'Reservations': ", e)
                        }
                } catch (e: Exception) {
                    Log.w(TAG, "Exception occurred = $e")
                }
            }
        }
        else {
            viewModelScope.launch {
                db.collection("Users/${Firebase.auth.uid}/Reservations")
                    .whereEqualTo("reservationId", reservationToUpdateId.value)
                    .get()
                    .addOnSuccessListener{ reservationDocuments ->// delete from User/Reservations
                        val reservationToUpdate = reservationDocuments.documents[0].toObject(Reservation::class.java)
                        val resId = reservationToUpdate!!.reservationId
                        reservation.reservationId = resId
                        // update in 'Users/uid/Reservations
                        db.collection("Users/${Firebase.auth.uid}/Reservations")
                            .document(reservationDocuments.documents[0].id)
                            .set(reservation)
                            .addOnSuccessListener {
                                // delete from Reservations
                                reservation.reservationId = ""
                                db.collection("Reservations").document(resId).set(reservation)
                                    .addOnSuccessListener{
                                        Log.d(TAG, "Reservation updated successfully from 'Reservations' and 'User/Reservations'")
                                        setReservationEditDialog.value(Reservation())
                                        isEdit.value = false
                                    }
                                    .addOnFailureListener { e ->
                                        db.collection("Users/${Firebase.auth.uid}/Reservations")
                                            .document(reservationDocuments.documents[0].id)
                                            .set(reservationToUpdate)
                                        Log.w(TAG, "Error updating reservation from 'Reservations': $e")
                                    }
                            }
                            .addOnFailureListener {
                                Log.w(TAG, "Error updating reservation in 'Users/uid/Reservations")
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error fetching reservation to update: $e")
                    }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        reg.remove()
        Log.d(TAG, "reservations listener removed")
        regFullDates.remove()
        Log.d(TAG, "fullDates listener removed")
    }
}