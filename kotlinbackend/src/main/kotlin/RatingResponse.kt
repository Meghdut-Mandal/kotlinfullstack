import com.google.gson.annotations.SerializedName
import org.dizitart.no2.objects.Id

data class RatingResponse(
        @SerializedName("address")
        val address: String = "",
        @SerializedName("aggregateRating")
        val aggregateRating: AggregateRating = AggregateRating(),
        @SerializedName("@context")
        val context: String = "",
        @SerializedName("image")
        val image: String = "",
        @SerializedName("name")
        val name: String = "",
        @SerializedName("@type")
        val type: String = "",
        @Id
        @SerializedName("url")
        val url: String = ""
)
data class AggregateRating(
        @SerializedName("bestRating")
        val bestRating: String = "",
        @SerializedName("ratingValue")
        val ratingValue: String = "",
        @SerializedName("reviewCount")
        val reviewCount: String = "",
        @SerializedName("@type")
        val type: String = "",
        @SerializedName("worstRating")
        val worstRating: String = ""
)