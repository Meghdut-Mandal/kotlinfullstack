package model.quiz.qbank


import com.google.gson.annotations.SerializedName

data class Subject(
    @SerializedName("goal_count")
    var goalCount: Int = 0,
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("in_syllabus_lo_ids")
    var inSyllabusLoIds: List<Int> = listOf(),
    @SerializedName("in_syllabus_tu_ids")
    var inSyllabusTuIds: List<Any> = listOf(),
    @SerializedName("in_syllabus_tu_v2_ids")
    var inSyllabusTuV2Ids: List<Int> = listOf(),
    @SerializedName("name")
    var name: String = "",
    @SerializedName("sequence")
    var sequence: Int = 0,
    @SerializedName("slug")
    var slug: String = "",
    @SerializedName("status")
    var status: String = ""
)