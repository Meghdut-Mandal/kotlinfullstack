package model

import org.dizitart.no2.objects.Id
import org.joda.time.*
import java.io.*

data class Kweet(@Id val id: Int, val userId: String, val text: String, val date: String, val replyTo: Int?) : Serializable
