import com.google.gson.annotations.SerializedName
import org.dizitart.no2.objects.Id


data class PostResponse(
        @SerializedName("total")
        val total: Int,
        @SerializedName("start")
        val start: Int,
        @SerializedName("status")
        val status: String,
        @SerializedName("postList")
        val postList: List<Post>
)

data class PostMakeResponse(val id: Long, val status: String)
data class Post(
        @Id
        @SerializedName("postId")
        val id: Long,
        @SerializedName("userID")
        val userId: String,
        @SerializedName("likesCount")
        val likesCount: Int,
        @SerializedName("postLocation")
        val postLocation: String,
        @SerializedName("postText")
        val postText: String,
        @SerializedName("postTime")
        val postTime: String,
        @SerializedName("userImage")
        val userImage: String,
        @SerializedName("userName")
        val userName: String
)

fun main() {


}