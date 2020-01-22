package model.quiz.questions


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("assessment_method")
    var assessmentMethod: String = "",
    @SerializedName("last_action")
    var lastAction: String = "",
    @SerializedName("last_node_id")
    var lastNodeId: Int = 0,
    @SerializedName("last_question_id")
    var lastQuestionId: Int = 0,
    @SerializedName("passage_qid_question_map")
    var passageQidQuestionMap: PassageQidQuestionMap = PassageQidQuestionMap(),
    @SerializedName("qids_tree")
    var qidsTree: QidsTree = QidsTree(),
    @SerializedName("questions")
    var questions: List<Question> = listOf()
)