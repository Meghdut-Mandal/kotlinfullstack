package model.quiz.spree.question


import com.google.gson.annotations.SerializedName

data class Option(
    @SerializedName("content")
    var content: String = "",
    @SerializedName("id")
    var id: Int = 0
)