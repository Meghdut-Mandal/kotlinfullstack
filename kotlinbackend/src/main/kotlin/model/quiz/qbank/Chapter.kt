package model.quiz.qbank


import com.google.gson.annotations.SerializedName

data class Chapter(
    @SerializedName("difficulty_level")
    var difficultyLevel: Double = 0.0,
    @SerializedName("goal_count")
    var goalCount: Int = 0,
    @SerializedName("has_question_sets")
    var hasQuestionSets: Boolean = false,
    @SerializedName("has_report")
    var hasReport: Boolean = false,
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("in_syllabus_tu_ids")
    var inSyllabusTuIds: List<Any> = listOf(),
    @SerializedName("in_syllabus_tu_v2_ids")
    var inSyllabusTuV2Ids: List<Int> = listOf(),
    @SerializedName("name")
    var name: String = "",
    @SerializedName("parent_id")
    var parentId: Int = 0,
    @SerializedName("required_effort")
    var requiredEffort: Double = 0.0,
    @SerializedName("sequence")
    var sequence: Int = 0,
    @SerializedName("slug")
    var slug: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("subject_id")
    var subjectId: Int = 0,
    @SerializedName("subject_name")
    var subjectName: String = "",
    @SerializedName("subject_slug")
    var subjectSlug: String = ""
)