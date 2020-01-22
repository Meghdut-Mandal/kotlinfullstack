package model.quiz.questions


import com.google.gson.annotations.SerializedName

data class Questions_List_Response(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("status_code")
    var statusCode: Int = 0
)