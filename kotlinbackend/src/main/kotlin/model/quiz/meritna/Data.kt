package model.quiz.meritna


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("file_name")
    var fileName: String = "",
    @SerializedName("rn_file_name")
    var rnFileName: String = "",
    @SerializedName("rn_file_text")
    var rnFileText: String = "",
    @SerializedName("rn_text")
    var rnText: String = ""
)