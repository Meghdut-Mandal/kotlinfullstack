package model

import com.google.gson.Gson

data class Batch(val clazz: Int, val section: String)

data class SubjectTaught(val batch: Batch, val subjectName: String, val subjectSlug: String)

fun main() {
    println("model>>main  ${Gson().toJson(SubjectTaught(Batch(12, "A"), "Physics", "physics"))} ")
}