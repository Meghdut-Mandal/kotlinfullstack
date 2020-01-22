package model.quiz.qbank


import com.google.gson.annotations.SerializedName

data class Goal(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("is_locked")
    var isLocked: Boolean = false,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("no")
    var no: Int = 0
)