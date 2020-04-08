package model

import org.dizitart.no2.IndexType
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices

@Indices(Index(value = "teacherID", type = IndexType.NonUnique))
data class Upload(@Id val id: String, val teacherID: String, val subjectTaughtID: String, val chapterName: String, val status: String, val timeStamp: Long = System.currentTimeMillis()) {
    companion object {
        const val RECEIVED = "received"
        const val PROCESSED = "processed"
        const val ERROR = "error"
        const val NOT_RECEIVED = "not.received"
    }
}