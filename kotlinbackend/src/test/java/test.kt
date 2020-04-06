import com.google.gson.Gson
import model.Batch
import model.StringResponse
import model.SubjectTaught
import okhttp3.*
import java.io.File


val parent = "http://viveeduserv.in"
val client = OkHttpClient().newBuilder()
        .build()


fun register_user(email: String) {

    val mediaType = MediaType.parse("application/x-www-form-urlencoded")
    val body =
            RequestBody.create(mediaType, "name=Meghdut Mandal&email=$email&psw=mmx32newton")
    val request: Request = Request.Builder()
            .url("$parent/teacher/register")
            .method("POST", body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
    val response: Response = client.newCall(request).execute()

    val string = response.body()?.string()
    if (string!!.contains("Registration Completed"))
        println(">>register_user  Sucess ")
    else println(">>register_user  Not sucessfull ")
}

fun upload_create(email: String, subjectTaught: SubjectTaught, chapterName: String): String {
    val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("teacherid", email)
            .addFormDataPart("taughtby", Gson().toJson(subjectTaught))
            .addFormDataPart("chaptername", chapterName)
            .build()
    val request: Request = Request.Builder()
            .url("$parent/teacher/upload_id")
            .method("POST", body)
            .addHeader("Content-Type", "multipart/form-data;")
            .build()
    val response = client.newCall(request).execute()
    val fromJson = Gson().fromJson(response.body()?.string(), StringResponse::class.java)
    return fromJson.message
}

fun uploadFile(file: File, id: String) {

    val countingRequestBody =
            CountingRequestBody(RequestBody.create(MediaType.parse("application/octet-stream"), file)) { bytesWritten, totalLength ->
                print("\r>>uploadFile  progress ${(bytesWritten * 100) / totalLength} ")
            }
    val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("abcd", file.absolutePath, countingRequestBody)
            .build()
    val request: Request = Request.Builder()
            .url("$parent/teacher/upload/$id")
            .method("POST", body)
            .build()
    val response = client.newCall(request).execute()
    println(">>uploadFile  ${response.body()?.string()} ")
}

fun main() {
    val email = "meghdut.windows@gmail.com"
//    register_user(email)
    val chapters = listOf("chapter 1 ", "chapter 2", "chpter 3", "chapter 5")
    listOf("physics", "chemistry", "hindi", "bengali", "geography").forEach { subject ->
        val subjectTaught = SubjectTaught("edds", Batch(12, "D"), subject, "id$subject")
        chapters.forEach { chapter ->
            val id = upload_create(email, subjectTaught, chapter)
            println(">>main  Upload done ")
        }
    }
    val subjectTaught = SubjectTaught("sds", Batch(12, "A"), "Physics", "physics")
    val id = upload_create(email, subjectTaught, "Physics")
    uploadFile(File("C:\\myFiles\\Books\\Advanced Engineering Mathematics 10th Edition.pdf"), id)
}