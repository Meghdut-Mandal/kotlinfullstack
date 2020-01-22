package model.quiz.spree.question


import com.google.gson.annotations.SerializedName

data class Solution(
    @SerializedName("text")
    var text: String = ""
)