package model.quiz.subject


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("chapter_design_v3")
    var chapterDesignV3: Boolean = false,
    @SerializedName("flag_extend_trial")
    var flagExtendTrial: Boolean = false,
    @SerializedName("is_search_enabled")
    var isSearchEnabled: Boolean = false,
    @SerializedName("offline_video_expiry_duration")
    var offlineVideoExpiryDuration: Int = 0,
    @SerializedName("school_reminder_duration")
    var schoolReminderDuration: Int = 0,
    @SerializedName("search_delay_time")
    var searchDelayTime: Int = 0,
    @SerializedName("search_experiments")
    var searchExperiments: List<String> = listOf(),
    @SerializedName("show_school_reminder")
    var showSchoolReminder: Boolean = false,
    @SerializedName("subjects")
    var subjects: List<Subject> = listOf(),
    @SerializedName("top_schools")
    var topSchools: List<TopSchool> = listOf()
)