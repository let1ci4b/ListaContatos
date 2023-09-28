package com.example.recyclerlistacontatos.models

import java.io.Serializable

data class Contacts(var titleImage : String, var nameContact : String, var numberContact : String, var isChecked : Boolean) : Serializable
