package it.polito.mad.lab5.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity
data class PlayGrounds(
    val sportName: String? = "",
    val playgroundName : String? = "",
    val hourlyRate: Int? = 0
    )
