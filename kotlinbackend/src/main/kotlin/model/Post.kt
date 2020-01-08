import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlin.random.Random


data class PostResponse(
        @SerializedName("total")
        val total:Int,
        @SerializedName("start")
        val start:Int,
        @SerializedName("status")
        val status:String,
        @SerializedName("postList")
        val postList:List<Post>
)

data class Post(
        @SerializedName("postId")
        val id:Long,
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
    val toList = (1..12).map { Post(
            Random.nextInt(200).toLong(),
            12,
            "Patna,Bihar",
            "Tomorrow the school will be closed ",
            "2W",
            "https://randomuser.me/api/portraits/med/men/75.jpg",
            "Vivek Kr Yadav"
    ) }.toList()
    val status=PostResponse(200,10,"200",toList)
    val str = Gson().toJsonTree(status)
    println("com.vive.vivesap.models.post>>>main $str ")
}