package model

import com.google.gson.annotations.SerializedName
import org.dizitart.no2.objects.Id
import java.io.Serializable

data class Notice(@Id val id: Long,
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
                  val userName: String) : Serializable


