package model.quiz.spree.question


import com.google.gson.annotations.SerializedName

data class Answer(
    @SerializedName("correctOptions")
    var correctOptions: CorrectOptions = CorrectOptions(),
    @SerializedName("questionId")
    var questionId: String = "",
    @SerializedName("solution")
    var solution: Solution = Solution()
)