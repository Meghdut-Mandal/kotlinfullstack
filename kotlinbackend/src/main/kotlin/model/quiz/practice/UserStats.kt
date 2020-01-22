package model.quiz.practice


import com.google.gson.annotations.SerializedName

data class UserStats(
    @SerializedName("subject_completion")
    var subjectCompletion: Int = 0
)