package model.quiz.spree.question


import com.google.gson.annotations.SerializedName

data class CorrectOptions(
    @SerializedName("option")
    var option: List<Int> = listOf()
)