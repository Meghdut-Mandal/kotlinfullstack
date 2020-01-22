package model.quiz.qbank


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("active_page")
    var activePage: String = "",
    @SerializedName("board_slug")
    var boardSlug: String = "",
    @SerializedName("chapter")
    var chapter: Chapter = Chapter(),
    @SerializedName("difficulty_counts")
    var difficultyCounts: DifficultyCounts = DifficultyCounts(),
    @SerializedName("end")
    var end: Int = 0,
    @SerializedName("filters")
    var filters: List<Filter> = listOf(),
    @SerializedName("global_difficulty_counts")
    var globalDifficultyCounts: GlobalDifficultyCounts = GlobalDifficultyCounts(),
    @SerializedName("goals")
    var goals: List<Goal> = listOf(),
    @SerializedName("has_full_access")
    var hasFullAccess: Boolean = false,
    @SerializedName("is_locked")
    var isLocked: Boolean = false,
    @SerializedName("klass_slug")
    var klassSlug: String = "",
    @SerializedName("mesasge")
    var mesasge: String = "",
    @SerializedName("n_attempted")
    var nAttempted: Int = 0,
    @SerializedName("n_global_locked_questions")
    var nGlobalLockedQuestions: Int = 0,
    @SerializedName("n_global_open_questions")
    var nGlobalOpenQuestions: Int = 0,
    @SerializedName("n_global_questions")
    var nGlobalQuestions: Int = 0,
    @SerializedName("n_questions")
    var nQuestions: Int = 0,
    @SerializedName("n_unattempted")
    var nUnattempted: Int = 0,
    @SerializedName("page")
    var page: Int = 0,
    @SerializedName("questions")
    var questions: List<Question> = listOf(),
    @SerializedName("questions_per_page")
    var questionsPerPage: Int = 0,
    @SerializedName("start")
    var start: Int = 0,
    @SerializedName("subject")
    var subject: Subject = Subject(),
    @SerializedName("tab")
    var tab: String = "",
    @SerializedName("test_source")
    var testSource: String = "",
    @SerializedName("test_type")
    var testType: String = ""
)