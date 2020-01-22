package model.quiz.qbank


import com.google.gson.annotations.SerializedName

data class Option(
    @SerializedName("count")
    var count: Int = 0,
    @SerializedName("key")
    var key: String = "",
    @SerializedName("label")
    var label: String = ""
)