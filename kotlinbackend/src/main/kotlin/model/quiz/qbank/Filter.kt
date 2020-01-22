package model.quiz.qbank


import com.google.gson.annotations.SerializedName

data class Filter(
    @SerializedName("allow_group_disable")
    var allowGroupDisable: Boolean = false,
    @SerializedName("is_multi_select")
    var isMultiSelect: Boolean = false,
    @SerializedName("options")
    var options: List<Option> = listOf(),
    @SerializedName("title")
    var title: String = "",
    @SerializedName("type")
    var type: String = ""
)