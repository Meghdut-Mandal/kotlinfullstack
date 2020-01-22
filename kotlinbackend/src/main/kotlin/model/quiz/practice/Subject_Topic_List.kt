package model.quiz.practice


import com.google.gson.annotations.SerializedName

/*
  response from "https://www.toppr.com/api/v5.1/class-11/practice/physics/"
 */
data class Subject_Topic_List(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("meta")
    var meta: Any? = null,
    @SerializedName("status")
    var status: String = "",
    @SerializedName("status_code")
    var statusCode: Int = 0
)