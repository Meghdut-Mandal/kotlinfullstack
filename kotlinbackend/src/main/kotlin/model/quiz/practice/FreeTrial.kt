package model.quiz.practice


import com.google.gson.annotations.SerializedName

data class FreeTrial(
    @SerializedName("is_freemium_user")
    var isFreemiumUser: Boolean = false
)