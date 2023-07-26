package com.example.recyclerlistacontatos

import java.io.Serializable

data class Contacts(var titleImage : Int, var nameContact : String, var numberContact : String, var visibility: Boolean = false) : Serializable {

}
