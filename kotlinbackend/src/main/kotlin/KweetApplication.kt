import com.google.gson.Gson
import dao.*
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.locations
import io.ktor.request.header
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.thymeleaf.Thymeleaf
import io.ktor.util.hex
import model.Teacher
import model.User
import model.getErrorResponse
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import org.slf4j.event.Level
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import routes.*
import java.io.File
import java.math.BigInteger
import java.net.URI
import java.security.MessageDigest
import java.text.DateFormat
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.crypto.spec.SecretKeySpec


val port = Integer.valueOf(System.getenv("PORT") ?: "8085")
val gson = Gson()

/*
 * Classes used for the locations feature to build urls and register routes.
 */
@Location("/")
class Index

@Location("/boot")
class BootRequest

@Location("/school/notices/routes.delete")
class DeleteNotices


@Location("/student/")
class StudentAPI {

    @Location("/notes/")
    class Notes(val subject_id: String)

    @Location("/subjects/")
    class Subjects {


        @Location("image/{slug}")
        class Image(val slug: String)
    }

}

@Location("/notes/{id}/{pageno}")
class NotePageRequest(val id: String, val pageno: Int)

@Location("/teacher/")
class TeacherAPI {
    @Location("/register")
    class SignUpPage

    @Location("/remove/")
    class Remove

    @Location("/login")
    class LogInRequest

    @Location("/about/")
    class Info

    @Location("/uploads/")
    class Uploads

    @Location("start/{upload_id}")
    class StartConversion(val upload_id: String)

    @Location("/upload_id/")
    class UploadID

    @Location("/recent")
    class RecentUploads


    @Location("/upload/{upload_id}")
    class UploadNotes(val upload_id: String)
}


@Location("/kweet/{id}/routes.delete")
class KweetDelete(val id: Int)

@Location("/view_kweet/{id}")
data class ViewKweet(val id: String)

@Location("/user/image/{userID}")
data class UserImageRequest(val userID: String)


@Location("/user/{user}")
data class UserPage(val user: String)

@Location("/register")
data class Register(val userId: String = "", val displayName: String = "", val email: String = "", val error: String = "", val phoneNumber: String = "")

@Location("/login")
data class Login(val userId: String = "", val error: String = "")


@Location("/nptel")
data class NptelData(val skip: Long = 0, val limit: Long = 20)

@Location("/logout")
class Logout

@Location("/signup")
class SignUp

@Location("quiz/subjects/{clazz}")
class SubjectsRequest(val clazz: Int)

@Location("quiz/chapters")
class ChaptersRequest(val clazz: Int, val subject: String)

@Location("quiz/questions")
class QuestionRequests(val clazz: Int, val subject: String, val chapter: String, val skip: Int = 0)

@Location("noted/")
class NoteRequest(val clazz: Int, val subject: String, val chapter: String)

@Location("note/file")
class NoteFileRequest(val clazz: Int, val subject: String, val chapter: String, val fileName: String)


@Location("/school/notices")
data class Notices(val start: Int = 0)

@Location("/school/list/{offset}")
data class SchoolsList(val offset: Int = 0)

@Location("/carreir_lib")
class CarrierLib


@Location("/attandance/{teacherID}")
class AttendanceRequest(val teacherID: String) {

    @Location("/todays")
    class Model(val attendanceRequest: AttendanceRequest)

    @Location("/add/{studentID}")
    class AddStudent(val attendanceRequest: AttendanceRequest, val studentID: String)

}

/**
 * Represents a session in this site containing the userId.
 */
data class KweetSession(val userId: String)

/**
 * Hardcoded secret hash key used to hash the passwords, and to authenticate the sessions.
 */
val hashKey = hex("6819b57a326945c1968f45236589")

/**
 * File where the database is going to be stored.
 */
val dir = File("build/db")

/**
 * HMac SHA1 key spec for the password hashing.
 */
val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

/**
 * Constructs a facade with the database, connected to the DataSource configured earlier with the [dir]
 * for storing the database.
 */
private val dao: ViveDao = DAONitrateDataBase(File("data/data.db"))
private val schoolsDao = SchoolListDAO(File("data/edugorrilas.db"))
//DAOFacadeCache(DAOFacadeDatabase(Database.connect(pool)), File(dir.parentFile, "ehcache"))

/**
 * Entry Point of the application. This function is referenced in the
 * resources/application.conf file inside the ktor.application.modules.
 *
 * For more information about this file: https://ktor.io/servers/configuration.html#hocon-file
 */
private val subjectsData = getDb("subjectsData")

private fun getDb(fileName: String): Nitrite = nitrite {
    file = File("data/$fileName.db")
    compress = true
    autoCompact = true
}

private val questionData = getDb("questionsData")
private val attendanceData = getDb("attendance")
private val teachersDao = getDb("teacher")
private val uploadsData = getDb("uploads")
private val notesData = getDb("notes")
private val subjectTaughtData = getDb("subjects_taught")

private val subjectsTaughtDao = SubjectTaughtDaoImpl(subjectTaughtData)
private val uploadsDao = UploadDaoImpl(uploadsData)
private val attendanceDAO = AttendanceDAO(attendanceData)
private val teacherDao: TeacherDao = TeacherDaoImpl(teachersDao)


private val questionsDataBase = QuestionsDataBase(subjectsData, questionData)
private val notesDao = NotesDao(notesData, subjectsData, File("notes"))

fun main() {
    embeddedServer(Netty, port, module = Application::main).start()
    println(">>main  Running at http://localhost:$port/ ")
}

private val imageConverter by lazy { ImageConverter(uploadsDao, notesDao) }


@KtorExperimentalLocationsAPI
fun Application.main() {
    // First we initialize the database.
    dao.init()
    // And we subscribe to the stop event of the application, so we can also close the [ComboPooledDataSource] [pool].
    // Now we call to a main with the dependencies as arguments.
    // Separating this function with its dependencies allows us to provide several modules with
    // the same code and different datasources living in the same application,
    // and to provide mocked instances for doing integration tests.
    mainWithDependencies(dao)
}


@KtorExperimentalLocationsAPI
fun Application.mainWithDependencies(dao: ViveDao) {
    // This adds automatically Date and Server headers to each response, and would allow you to configure
    // additional headers served to each response.
    install(CORS)

    {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.AccessControlAllowHeaders)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        anyHost()
        maxAge = Duration.ofDays(1)
        header("key")
    }
    install(CallLogging) {
        level = Level.INFO
    }
//    println(">>mainWithDependencies   ")
    install(DefaultHeaders)

    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
    install(StatusPages) {
        exception<Throwable> { cause ->
            cause.printStackTrace()
            call.respond(HttpStatusCode.NotImplemented, cause.getErrorResponse())
        }
        status(HttpStatusCode.NotFound)
        {
            call.respond(HttpStatusCode.NotFound, "Sorry Please check the Url")
        }
    }
    // This uses use the logger to log every call (request/response)
    // Automatic '304 Not Modified' Responses
    install(ConditionalHeaders)
    // Supports for Range, Accept-Range and Content-Range headers
    install(PartialContent)
    // Allows to use classes annotated with @Location to represent URLs.
    // They are typed, can be constructed to generate URLs, and can be used to register routes.
    install(Locations)
    // Adds support to generate templated responses using FreeMarker.
    // We configure it specifying the path inside the resources to use to get the template files.
    // You can use <!-- @ftlvariable --> to annotate types inside the templates
    // in a way that works with IntelliJ IDEA Ultimate.
    // You can check the `resources/templates/*.ftl` files for reference.
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    // Configure the session to be represented by a [KweetSession],
    // using the SESSION cookie to store it, and transforming it to be authenticated with the [hashKey].
    // it is sent in plain text, but since it is authenticated can't be modified without knowing the secret [hashKey].
    install(Sessions) {
        cookie<KweetSession>("SESSION") {
            transform(SessionTransportTransformerMessageAuthentication(hashKey))
        }
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    install(SinglePageApplication) {
        // main page file name to be served
        defaultPage = "index.html"
        spaRoute = "/web"
        // folder in which look for you spa files, either
        // inside bundled resources or current working directory
        folderPath = "react/"
        useFiles = true

    }


    // Provides a hash function to be used when registering the resources.
    val hashFunction = { s: String -> hash(s) }

    // Register all the routes available to the application.
    // They are split in several methods and files, so it can scale for larger
    // applications keeping a reasonable amount of lines per file.
    routing {
        //        styles()
        index(dao)
//        postNew(dao, hashFunction)
        delete(dao, hashFunction)
        userPage(dao)

        viewKweet(dao, hashFunction)
        login(dao, hashFunction)
        register(dao, hashFunction)
        notices(dao, hashFunction)
        nptel()
        schoolList(schoolsDao)
        quizLinks(questionsDataBase)
        notesLinks(notesDao)
        attandanceHelper(attendanceDAO)
        carrerLibrary(dao, hashFunction)
        if (!teacherDao.hasTeacher("meghdut.windows@gmail.com")) {
            teacherDao.addTeacher(
                    Teacher("meghdut.windows@gmail.com", hash("meghdut.windows@gmail.com", "meghdut"),
                            "Meghdut mandal", arrayListOf()))
        }
        teachers(imageConverter, teacherDao, uploadsDao, subjectsTaughtDao, notesDao)
        student(subjectsTaughtDao, notesDao)
        static("styles") {
            resources("styles/")
        }
        static("carreir_lib") {
            resources("templates/carreir_lib/")
        }
        static("/") {
            resources("templates/sample_site")
            resource("/home", "templates/sample_site/home.html")
        }
    }
}


fun hash(vararg items: Any): String {
    return hash(items.joinToString { gson.toJson(it) })
}

/**
 * Method that hashes a [password] by using the globally defined secret key [hmacKey].
 */
private fun hash(string: String): String {
    val digester = MessageDigest.getInstance("MD5")
    val bytes = digester.digest(string.toByteArray())
    val bigno = BigInteger(1, bytes)
    return bigno.toString(36)
}

/**
 * Allows to respond with a absolute redirect from a typed [location] instance of a class annotated
 * with [Location] using the Locations feature.
 */
suspend fun ApplicationCall.redirect(location: Any) {
    val host = request.host()
    val portSpec = request.port().let { if (it == 80) "" else ":$it" }
    val address = host + portSpec

    respondRedirect("http://$address${application.locations.href(location)}")
}

/**
 * Generates a security code using a [hashFunction], a [date], a [user] and an implicit [HttpHeaders.Referrer]
 * to generate tokens to prevent CSRF attacks.
 */
fun ApplicationCall.securityCode(date: Long, user: User, hashFunction: (String) -> String) =
        hashFunction("$date:${user.userId}:${request.host()}:${refererHost()}")

/**
 * Verifies that a code generated from [securityCode] is valid for a [date] and a [user] and an implicit [HttpHeaders.Referrer].
 * It should match the generated [securityCode] and also not be older than two hours.
 * Used to prevent CSRF attacks.
 */
fun ApplicationCall.verifyCode(date: Long, user: User, code: String, hashFunction: (String) -> String) =
        securityCode(date, user, hashFunction) == code
                && (System.currentTimeMillis() - date).let { it > 0 && it < TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS) }

/**
 * Obtains the [refererHost] from the [HttpHeaders.Referrer] header, to check it to prevent CSRF attacks
 * from other domains.
 */
fun ApplicationCall.refererHost() = request.header(HttpHeaders.Referrer)?.let { URI.create(it).host }

/**
 * Pattern to validate an `userId`
 */
private val userIdPattern = "[a-zA-Z0-9_\\.]+".toRegex()

/**
 * Validates that an [userId] (that is also the user name) is a valid identifier.
 * Here we could add additional checks like the length of the user.
 * Or other things like a bad word filter.
 */
internal fun userNameValid(userId: String) = userId.matches(userIdPattern)
// rsync -r -v --progress -e ssh ubuntu@54.251.185.59:/  questionsData.db