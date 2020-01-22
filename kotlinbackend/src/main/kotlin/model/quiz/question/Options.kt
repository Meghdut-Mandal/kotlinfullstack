package model.quiz.spree.question


import com.google.gson.annotations.SerializedName

data class Options(
    @SerializedName("option")
    var option: List<Option> = listOf()
)