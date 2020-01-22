package model.quiz.questions


import com.google.gson.annotations.SerializedName

data class Choice(
    @SerializedName("choice")
    var choice: String = "",
    @SerializedName("choice_id")
    var choiceId: Int = 0,
    @SerializedName("image")
    var image: String = "",
    @SerializedName("is_right")
    var isRight: Boolean = false,
    @SerializedName("label")
    var label: String = ""
)