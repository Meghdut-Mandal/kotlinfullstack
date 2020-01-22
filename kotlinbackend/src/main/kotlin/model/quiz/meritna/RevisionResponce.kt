package model.quiz.meritna


import com.google.gson.annotations.SerializedName

data class RevisionResponce(
    @SerializedName("data")
    var `data`: List<Data> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("response_code")
    var responseCode: Int = 0,
    @SerializedName("status")
    var status: Int = 0
)