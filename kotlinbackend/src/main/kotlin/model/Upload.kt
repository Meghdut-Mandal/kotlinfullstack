package model

import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index

@Index("teacherID")
data class Upload(@Id val id: String, val teacherID: String, val subjectTaught: SubjectTaught, val chapterName: String, val status: String) {
    companion object {
        const val RECEIVED = "received"
        const val PROCESSED = "processed"
        const val ERROR = "error"
        const val NOT_RECEIVED = "not.received"
    }
}