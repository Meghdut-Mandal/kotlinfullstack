package model.quiz.essential

import com.google.gson.annotations.SerializedName
import org.dizitart.no2.objects.Id

data class SubjectSnap(
        val slug: String,
        val name: String,
        @Id
        @SerializedName("id")
        var id: Int,
        var isExploreComplete: Boolean = false
)

data class ChapterSnap(
        @Id
        @SerializedName("id")
        var id: Int,
        val name: String,
        val slug: String,
        var isExploreComplete: Boolean = false,
        var startPage: Int = 1
)