package it.polito.mad.lab5.db

class User(name: String,
           nickname: String,
           email: String,
           birthdate: String,
           sex: String,
           city: String,
           imageUri: String) {

    constructor() : this ("","","","","","","")
    val name = name
    val nickname = nickname
    val email = email
    val birthdate = birthdate
    val sex = sex
    val city = city
    var imageUri = imageUri

    override fun toString(): String{
        return "$name $email $birthdate"
    }
}