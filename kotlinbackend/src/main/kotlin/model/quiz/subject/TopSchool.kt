package model.quiz.subject


import com.google.gson.annotations.SerializedName

data class TopSchool(
    @SerializedName("name")
    var name: String = "",
    @SerializedName("no_of_students")
    var noOfStudents: String = ""
)