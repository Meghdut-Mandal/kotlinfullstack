package model.quiz.spree.question


import com.google.gson.annotations.SerializedName

data class Question_data(
    @SerializedName("answer")
    var answer: Answer = Answer(),
    @SerializedName("id")
    var id: String = "",
    @SerializedName("mark")
    var mark: String = "",
    @SerializedName("options")
    var options: Options = Options(),
    @SerializedName("penalty")
    var penalty: String = "",
    @SerializedName("searchtext")
    var searchtext: String = "",
    @SerializedName("spayee:objective")
    var spayeeObjective: String = "",
    @SerializedName("subject")
    var subject: String = "",
    @SerializedName("tag")
    var tag: List<String> = listOf(),
    @SerializedName("text")
    var text: String = "",
    @SerializedName("type")
    var type: String = ""
)