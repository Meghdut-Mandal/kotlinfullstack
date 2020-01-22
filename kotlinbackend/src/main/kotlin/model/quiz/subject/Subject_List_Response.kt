package model.quiz.subject


import com.google.gson.annotations.SerializedName
/*
  from "https://www.toppr.com/api/v5.1/subjects/"
 */
data class Subject_List_Response(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("status_code")
    var statusCode: Int = 0
)