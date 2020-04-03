package model

import com.google.gson.Gson
import org.dizitart.no2.objects.Id

data class Batch(val clazz: Int, val section: String)

data class SubjectTaught(@Id val id: String, val batch: Batch, val subjectName: String, val subjectSlug: String)

fun main() {
    println("model>>main  ${Gson().toJson(SubjectTaught("ssjnsdjd", Batch(12, "A"), "Physics", "physics"))} ")
}