package it.polito.mad.lab5.db

data class Rating(
    var id: String? = "",
    var field: String? = "",
    var reviewText: String? = "",
    var score: Int? = 0,
    var user: User? = User(),
    var date: String? = ""
)