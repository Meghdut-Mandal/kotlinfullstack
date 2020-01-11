package model

import org.dizitart.no2.objects.Id
import java.io.*

data class User(@Id val userId: String, val email: String, val displayName: String, val passwordHash: String,val phoneNumber:String) : Serializable
