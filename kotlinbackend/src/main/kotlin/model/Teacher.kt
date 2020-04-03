package model

import org.dizitart.no2.objects.Id

data class Teacher(@Id val id: String, val hash: String, val name: String, val subjects: MutableList<String>)