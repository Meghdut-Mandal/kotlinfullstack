package model

import org.dizitart.no2.objects.Id
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Status(val userID: String, val status: String)

data class AttendanceModel(val takerUserId: String, val statusList: List<Status>, @Id val date: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)) {
    companion object {
        const val PRESENT = "attendance.present"
        const val ABSENT = "attendance.absent"
        const val UNKNOWN = "attendance.unknown"
    }
}

val currentDate
    get() = LocalDateTime.now()

val currentDateString
    get() = currentDate.format(DateTimeFormatter.ISO_DATE_TIME)

fun main() {
    val attendance = AttendanceModel("232323", emptyList())
    println("model>>main  $attendance ")
}