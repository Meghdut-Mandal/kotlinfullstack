package model.quiz.qbank


import com.google.gson.annotations.SerializedName

data class QBank_Response(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("meta")
    var meta: Meta = Meta(),
    @SerializedName("status")
    var status: String = "",
    @SerializedName("status_code")
    var statusCode: Int = 0
)