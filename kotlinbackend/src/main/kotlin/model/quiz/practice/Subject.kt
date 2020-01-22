package model.quiz.practice


import com.google.gson.annotations.SerializedName

data class Subject(
    @SerializedName("goal_count")
    var goalCount: Int = 0,
    @SerializedName("icon_identifier")
    var iconIdentifier: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("sequence")
    var sequence: Int = 0,
    @SerializedName("slug")
    var slug: String = "",
    @SerializedName("status")
    var status: String = ""
)