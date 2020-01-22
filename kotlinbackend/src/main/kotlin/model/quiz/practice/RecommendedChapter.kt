package model.quiz.practice


import com.google.gson.annotations.SerializedName

data class RecommendedChapter(
    @SerializedName("attempted")
    var attempted: Boolean = false,
    @SerializedName("chapter_lessons_count")
    var chapterLessonsCount: Int = 0,
    @SerializedName("chapter_no")
    var chapterNo: Int = 0,
    @SerializedName("difficulty_level")
    var difficultyLevel: String = "",
    @SerializedName("duplicate_of")
    var duplicateOf: Any? = null,
    @SerializedName("goals_completed")
    var goalsCompleted: Int = 0,
    @SerializedName("has_question_bank")
    var hasQuestionBank: Boolean = false,
    @SerializedName("has_question_sets")
    var hasQuestionSets: Boolean = false,
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("is_last_viewed")
    var isLastViewed: Boolean = false,
    @SerializedName("n_concepts")
    var nConcepts: Int = 0,
    @SerializedName("n_questions")
    var nQuestions: Int = 0,
    @SerializedName("n_stories")
    var nStories: Int = 0,
    @SerializedName("n_videos")
    var nVideos: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("percent_completed")
    var percentCompleted: Int = 0,
    @SerializedName("percentage_completion")
    var percentageCompletion: Int = 0,
    @SerializedName("previous_exam_questions")
    var previousExamQuestions: List<Any> = listOf(),
    @SerializedName("required_effort")
    var requiredEffort: Int = 0,
    @SerializedName("skilltest_enabled")
    var skilltestEnabled: Boolean = false,
    @SerializedName("slug")
    var slug: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("top_performers")
    var topPerformers: List<Any> = listOf(),
    @SerializedName("total_goals")
    var totalGoals: Int = 0,
    @SerializedName("total_practicing_users")
    var totalPracticingUsers: Int = 0,
    @SerializedName("watched_videos")
    var watchedVideos: Int = 0
)