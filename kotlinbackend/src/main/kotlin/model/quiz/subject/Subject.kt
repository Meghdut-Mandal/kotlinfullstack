package model.quiz.subject


import com.google.gson.annotations.SerializedName
import org.dizitart.no2.objects.Id

data class Subject(
    @SerializedName("goal_count")
    var goalCount: Int = 0,
    @SerializedName("goal_progress")
    var goalProgress: Int = 0,
    @SerializedName("goals_completed")
    var goalsCompleted: Int = 0,
    @SerializedName("icon_identifier")
    var iconIdentifier: String = "",
    @Id
    @SerializedName("id")
    var id: Int,
    @SerializedName("live_challenge_enabled")
    var liveChallengeEnabled: Boolean = false,
    @SerializedName("n_chapters")
    var nChapters: Int = 0,
    @SerializedName("n_concepts")
    var nConcepts: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("slug")
    var slug: String = "",
    @SerializedName("video_count")
    var videoCount: Int = 0,
    var isExploreComplete: Boolean = false
)