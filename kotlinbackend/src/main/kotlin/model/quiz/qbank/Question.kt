package model.quiz.qbank


import com.google.gson.annotations.SerializedName
import org.dizitart.no2.objects.Id

data class Question(
    @SerializedName("already_attempted")
    var alreadyAttempted: Boolean = false,
    @SerializedName("assertion")
    var assertion: String = "",
    @SerializedName("can_ask_doubt")
    var canAskDoubt: Boolean = false,
    @SerializedName("choices")
    var choices: List<Choice> = listOf(),
    @SerializedName("correctly_answered")
    var correctlyAnswered: Boolean = false,
    @SerializedName("disable_bookmark")
    var disableBookmark: Boolean = false,
    @SerializedName("hint")
    var hint: String = "",
    @SerializedName("hint_available")
    var hintAvailable: Boolean = false,
    @SerializedName("hint_image")
    var hintImage: String = "",
    @SerializedName("is_bookmarked")
    var isBookmarked: Boolean = false,
    @SerializedName("level")
    var level: Int = 0,
    @SerializedName("multiple_correct")
    var multipleCorrect: Boolean = false,
    @SerializedName("passage")
    var passage: String = "",
    @SerializedName("passage_footer")
    var passageFooter: String = "",
    @SerializedName("passage_header")
    var passageHeader: String = "",
    @SerializedName("passage_image")
    var passageImage: String = "",
    @SerializedName("question")
    var question: String = "",
    @Id
    @SerializedName("question_id")
    var questionId: Int = 0,
    @SerializedName("question_image")
    var questionImage: String = "",
    @SerializedName("question_level")
    var questionLevel: Int = 0,
    @SerializedName("question_linked")
    var questionLinked: Boolean = false,
    @SerializedName("question_linked_to_id")
    var questionLinkedToId: Any? = null,
    @SerializedName("question_lo_ids")
    var questionLoIds: List<Int> = listOf(),
    @SerializedName("question_status")
    var questionStatus: String = "",
    @SerializedName("question_style")
    var questionStyle: String = "",
    @SerializedName("reason")
    var reason: String = "",
    @SerializedName("sequence_no")
    var sequenceNo: Int = 0,
    @SerializedName("solution")
    var solution: String = "",
    @SerializedName("solution_available")
    var solutionAvailable: Boolean = false,
    @SerializedName("solution_image")
    var solutionImage: String = "",
    @SerializedName("solution_links")
    var solutionLinks: List<Any> = listOf()
)

val Question.difficultlyLevel: String
    get() {
        return when (this.level) {
            1 -> "Easy"
            2 -> "Medium"
            else -> "Difficult"
        }
    }