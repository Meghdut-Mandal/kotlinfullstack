package model.quiz.practice


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("active_page")
    var activePage: String = "",
    @SerializedName("chapters")
    var chapters: List<Chapter> = listOf(),
    @SerializedName("free_trial")
    var freeTrial: FreeTrial = FreeTrial(),
    @SerializedName("is_locked")
    var isLocked: Boolean = false,
    @SerializedName("n_total_goals")
    var nTotalGoals: Int = 0,
    @SerializedName("recommended_chapter")
    var recommendedChapter: RecommendedChapter = RecommendedChapter(),
    @SerializedName("show_coachmark")
    var showCoachmark: Boolean = false,
    @SerializedName("subject")
    var subject: Subject = Subject(),
    @SerializedName("subjects")
    var subjects: Subjects = Subjects(),
    @SerializedName("tab")
    var tab: String = "",
    @SerializedName("user_stats")
    var userStats: UserStats = UserStats()
)