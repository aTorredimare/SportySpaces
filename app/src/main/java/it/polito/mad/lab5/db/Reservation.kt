package it.polito.mad.lab5.db

import java.util.Date

class Reservation(
    var reservationId: String,  // document id of the same reservation document, saved in the 'Reservations' collection
    val userId: String,
    val discipline: String,
    val playgroundName : String,
    val date: Date,
    val oraInizio: Int,
    val oraFine : Int,
    val customRequest: String
){
    constructor() : this("", "", "", "", Date(), -1, -1, "")

    constructor(res: Reservation): this(res.reservationId, res.userId, res.discipline, res.playgroundName, res.date, res.oraInizio, res.oraFine, res.customRequest)

    override fun toString(): String {
        return "$discipline $playgroundName $date $oraInizio-$oraFine $customRequest"
    }
}
