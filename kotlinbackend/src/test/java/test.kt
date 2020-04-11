import com.google.gson.Gson
import model.Batch
import model.StringResponse
import model.SubjectTaught
import okhttp3.*
import java.io.File
import java.util.concurrent.TimeUnit


val parent = "http://localhost:8085"
val client = OkHttpClient().newBuilder().readTimeout(1, TimeUnit.MINUTES)
        .build()


fun register_user(email: String, password: String) {

    val mediaType = MediaType.parse("application/x-www-form-urlencoded")
    val body =
            RequestBody.create(mediaType, "name=Meghdut Mandal&email=$email&psw=$password")
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
            .addFormDataPart("uploadFile", file.absolutePath, countingRequestBody)
            .addFormDataPart("uploadid",id)
            .build()
    val request: Request = Request.Builder()
            .url("http://localhost:8080/upload")
            .method("POST", body)
            .build()
    val response = client.newCall(request).execute()
    println(">>uploadFile  ${response.body()?.string()} ")
}

fun allFilesTest() {
    val subjectmap = getSubjects()
    val (email, batch) = register()

    subjectmap.forEach { pair ->
        File("test").listFiles()?.toList()!!.parallelStream().forEach {
            try {
                val subjectTaught = SubjectTaught("edds"+Math.random(), batch, pair.second, pair.first)
                val id = upload_create(email, subjectTaught, it.nameWithoutExtension)
                uploadFile(it, id)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

private fun register(): Pair<String, Batch> {
    val email = "meghdut.windows@gmail.com"
    val batch = Batch(12, "D")
    register_user(email, "meghdut")
    return Pair(email, batch)
}

fun singleFileTest() {
    val (email, batch) = register()
    val subjectTaught = SubjectTaught("edds"+Math.random(), batch, "Hola", "hola")
    val file=File("test").listFiles()?.first()
    val id = upload_create(email, subjectTaught,file?.name!!)
    uploadFile(file, id)
}

private fun getSubjects(): List<Pair<String, String>> {
    return listOf("physics" to "Physics",
            "chemistry" to "Chemistry", "maths" to "Maths", "biology" to "Biology",
            "english" to "English", "history" to "History", "civics" to "Civics",
            "geography" to "Geography", "general-knowledge" to "General Knowledge",
            "economics" to "Economics", "elements-of-book-keeping-and-accountancy" to "Elements of Book Keeping and Accountancy",
            "elements-of-business" to "Elements of Business", "political-science" to "Political Science", "business-studies" to "Business Studies",
            "accountancy" to "Accountancy", "legal-studies" to "Legal Studies", "physical-education" to "Physical Education",
            "information-practices" to "Information Practices", "computer" to "Computer", "hindi" to "Hindi", "sanskrit" to "Sanskrit")
}

fun main() {
    singleFileTest()
}