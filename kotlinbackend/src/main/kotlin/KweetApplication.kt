import com.google.gson.Gson
import dao.*
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
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
import model.User
import model.getErrorResponse
import org.dizitart.kno2.nitrite
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import routes.*
import java.io.File
import java.net.URI
import java.text.DateFormat
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random


val port = Integer.valueOf(System.getenv("PORT") ?: "8085")
val gson = Gson()

/*
 * Classes used for the locations feature to build urls and register routes.
 */
@Location("/")
class Index

@Location("/boot")
class BootRequest

@Location("/school/notices/delete")
class DeleteNotices


@Location("/kweet/{id}/delete")
class KweetDelete(val id: Int)

@Location("/view_kweet/{id}")
data class ViewKweet(val id: String)

@Location("/user/{user}")
data class UserPage(val user: String)

@Location("/register")
data class Register(val userId: String = "", val displayName: String = "", val email: String = "", val error: String = "", val phoneNumber: String = "")

@Location("/login")
data class Login(val userId: String = "", val error: String = "")

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

@Location("note/")
class NoteRequest(val clazz: Int, val subject: String, val chapter: String)

@Location("note/file")
class NoteFileRequest(val clazz: Int, val subject: String, val chapter: String, val fileName: String)


@Location("/school/notices")
data class Notices(val start: Int = 0)

@Location("/school/list/{offset}")
data class SchoolsList(val offset: Int = 0)

@Location("/carreir_lib")
class CarrierLib

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
val dao: ViveDao = DAONitrateDataBase(File("data/data.db"))
val schoolsDao = SchoolListDAO(File("data/edugorrilas.db"))
//DAOFacadeCache(DAOFacadeDatabase(Database.connect(pool)), File(dir.parentFile, "ehcache"))

/**
 * Entry Point of the application. This function is referenced in the
 * resources/application.conf file inside the ktor.application.modules.
 *
 * For more information about this file: https://ktor.io/servers/configuration.html#hocon-file
 */

private val subjectsData = nitrite {
    file = File("subjectsData.db")
    compress = true
    autoCompact = true
}
private val questionData = nitrite {
    file = File("questionsData.db")
    compress = true
    autoCompact = true
}
private val questionsDataBase = QuestionsDataBase(subjectsData, questionData)
private val notesDao = NotesDao(subjectsData, File("notes"))
fun main() {
    embeddedServer(Netty, port, module = Application::main).start()
    println(">>main  Running at http://localhost:$port/ ")
}

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
    if (dao.getNoticeCount() == 0) {
        val names =
                arrayListOf("Mr Ralph Wilson", "Mrs Louella Webb", "Miss Hailey Hernandez", "Mrs Sheila Chavez", "Mr Ken Miller", "Mr Eugene Schmidt", "Ms Victoria Davis", "Mr Noah Clark", "Ms Shannon Hawkins", "Mr Oscar Prescott", "Ms Mattie Harrison", "Miss Sally Schmidt", "Miss Diana Walker", "Ms Isobel Barnett", "Mr Tommy Carroll", "Mr Levi Edwards", "Mr Gene Porter", "Mr Alex Hudson", "Miss Dolores Dixon", "Mr Duane Richards", "Mrs Roberta Reed", "Mr Ryan Roberts", "Mr Alvin Sutton", "Mr Eric Gonzalez", "Miss Ida Austin", "Mrs Jill Boyd", "Ms Kim Lewis", "Ms June Howell", "Mrs Arianna Mcdonalid", "Ms Bonnie Snyder", "Mrs Amber Morrison", "Mr Bob Ferguson", "Ms Tonya Pena", "Mr Ian Rivera", "Ms Lucille Davidson", "Mr Scott Martin", "Mr Neil Phillips", "Mr Alfred Cruz", "Mr Troy Gomez", "Mrs Ella Dunn", "Miss Carmen Phillips", "Mr Hunter King", "Mr Caleb Moore", "Mr Dan Wells", "Mr Ernest Smith", "Mrs Janet Miles", "Mrs Bessie Howell", "Mrs Julie Peck", "Mr Wayne Stewart", "Mrs Connie Simpson", "Ms Clara Arnold", "Mr Ray Murray", "Mr Herman Thompson", "Mr Gregory Larson", "Miss Constance Rodriguez", "Miss Gwendolyn Morgan", "Mr Victor Edwards", "Mr Nelson Kennedy", "Ms Carolyn Powell", "Mr Duane Torres", "Mr Philip Mcdonalid", "Mr Connor Neal", "Mr Erik Herrera", "Mrs Vickie Snyder", "Mr Alex Graham", "Ms Bessie Ford", "Mr Jerry Jordan", "Ms Claudia Lane", "Miss Arianna Bradley", "Miss Priscilla Jones", "Mr Maurice Brown", "Miss Leta Smith", "Ms Nevaeh Lowe", "Mr Kirk Lee", "Mr Liam Gonzalez", "Mr Bob Nichols", "Mr Vernon Crawford", "Mr Darrell Arnold", "Mrs Leta Olson", "Mrs Lily Bowman", "Mrs Hilda Anderson", "Mr Jack Jensen", "Mr Ronnie Carroll", "Mrs Lorraine Spencer", "Ms Arlene Spencer", "Ms Amanda Perry", "Ms Sherri Gilbert", "Mr Johnny Frazier", "Mr Marc Pena", "Miss Rita Wilson", "Mrs Claudia Anderson", "Mr Lance Boyd", "Mr Adam Rodriguez", "Mr Ralph Ruiz", "Ms Christine Rose", "Ms Vanessa Jacobs", "Mr Ronnie Oliver", "Mrs Alice Pena", "Mr Kurt Ellis", "Miss Martha Wagner", "Ms Gabriella Wagner", "Miss Stacey Williamson", "Mr Alexander Hoffman", "Mr Sergio Tucker", "Mr Adrian Fleming", "Miss Jean Sutton", "Miss Jenny Williams", "Mr Terrance Arnold", "Mr Derrick Campbell", "Miss Harper Patterson", "Ms Vanessa Spencer", "Miss Bertha Ellis", "Miss Glenda Reynolds", "Mr Ernest Carlson", "Mrs Louella Neal", "Ms Sherry Reyes", "Ms Carole Bishop", "Miss Celina Butler", "Mrs Marcia Frazier", "Miss Tamara Soto", "Ms Shelly Hawkins", "Ms Myrtle Bradley", "Mr Cory Harrison", "Mr Ruben Torres", "Ms Monica Stanley", "Mr Kevin Gardner", "Mr Jar Franklin", "Miss Tracey Little", "Mr Rodney Garcia", "Mr Steven Stephens", "Mr Gavin Hudson", "Miss Connie Harrison", "Mr Corey Jacobs", "Mr Francis Ryan", "Miss Jennifer Robertson", "Mr Clyde Thompson", "Mrs Vivan Reynolds", "Ms Pauline Long", "Mr Felix Gomez", "Ms Nevaeh Myers", "Mr Roland Lee", "Mrs Rita Stewart", "Miss Gwendolyn Flores", "Mr Adam Lee", "Mr Salvador Schmidt", "Ms Lois Carlson", "Miss Nellie Richards", "Mr Dustin Lewis", "Ms Shannon Beck", "Mrs Sherry Stone", "Mrs Arlene Mitchell", "Mr Clifton Walker", "Mr Erik Matthews", "Mr Shawn Johnston", "Ms Addison Holt", "Ms Billie Rogers", "Mr Benjamin Sanders", "Mrs Carla Miller", "Ms Veronica Mitchell", "Ms Clara Hoffman", "Miss Sherry George", "Mr Flenn Davis", "Miss Marsha Hoffman", "Mr Christian Rodriquez", "Mr Corey Cole", "Ms Juanita Hughes", "Mr Cecil Howard", "Ms Stella Rose", "Mrs Leah Snyder", "Mr Harold Prescott", "Mrs Gina Graves", "Mrs Diana May", "Mr Phillip Peterson", "Mr Jeremiah Gordon", "Mr Gene Ferguson", "Mr Jeremy Pena", "Miss Natalie Fernandez", "Mrs Alicia Smith", "Mr Owen Mckinney", "Mrs Heidi Stephens", "Mrs Allison Perry", "Mrs April Schmidt", "Mrs Isabella Evans", "Mr Greg Ray", "Miss Sheila Robinson", "Mrs Alyssa Cole", "Mrs Bella Welch", "Miss Bertha Hart", "Miss Cassandra Mason", "Mrs Delores Barrett", "Ms Alyssa Neal", "Mr Mario Johnson", "Miss Terra Hamilton", "Mr Noah Wagner", "Mr Jim Stone", "Mrs Hannah Long", "Mr Jeffrey Shaw", "Mr Andrew Miles", "Mr Howard Craig", "Ms Heidi Perry", "Ms Christy Montgomery", "Ms Debra Ward", "Mrs Sara Ross", "Mr Brett Owens", "Mrs Christy Garrett", "Miss Regina Simpson", "Mr Ken Gardner", "Mr Shawn Ramirez", "Mr Stephen Hill", "Mrs Naomi Riley", "Ms Sally Wood", "Miss Erin Miller", "Mr Stanley Campbell", "Miss Beverley Elliott", "Mr Walter Coleman", "Ms Sophia Ellis", "Mr Victor Chavez", "Miss Vivan White", "Miss Alma Green", "Mr Mark Banks", "Mrs Terry Bates", "Mr Marion Terry", "Mr Javier Henderson", "Mr Edgar Holland", "Mr Alan Lowe", "Ms Alexa Miles", "Mr Jamie Welch", "Ms Glenda Sullivan", "Mr Robert Gonzales", "Miss Cindy Stanley", "Mr Ted Gutierrez", "Miss Patsy Hall", "Miss Doris Kennedy", "Mrs Kristina Johnson", "Mr Arnold Ray", "Mr Ray Harper", "Miss Leta Mendoza", "Mr Christian Morgan", "Ms Jenny Thomas", "Ms Serenity Murray", "Mrs Marjorie Bennett", "Mr Jeremiah Miles", "Mr Elijah Davis", "Miss Kenzi Boyd", "Mr Dean Lynch", "Ms Michelle Hale", "Mrs Eleanor May", "Miss Kristen Jones", "Mr Austin Sanders", "Ms Leah Richardson", "Mr Harry Carroll", "Mrs Josephine Bowman", "Miss Tara Stone", "Mr Andre Welch", "Ms Annette Hopkins", "Mr Claude Mitchelle", "Mr Jeremiah Medina", "Mr Steven Cox", "Mr Christopher Webb", "Miss Wilma Silva", "Miss Gwendolyn Murray", "Mrs Susan Payne", "Mr Brian Hale", "Mr Carter Campbell", "Miss Michelle Jensen", "Mr Franklin Burton", "Ms Eva Jensen", "Mr Roland Gray", "Mr Eddie Lowe", "Mr Douglas Harvey", "Mr Maurice Ford", "Mr Tom Taylor", "Ms Edna Martinez", "Mr Perry Crawford", "Mrs Ann Stevens", "Mrs Veronica Dunn", "Mr Harvey Green", "Mr Raymond Evans", "Mrs Monica Fletcher", "Ms Jamie Morales", "Ms Maxine Hoffman", "Mr Cameron Peters", "Miss Marcia Terry", "Mrs Myrtle Reid", "Mr Rodney Holt", "Miss Amelia Coleman", "Miss Peyton Carpenter", "Miss Meghan Cruz", "Miss Tamara Watkins", "Mr Elmer Lowe", "Mr Enrique Kuhn", "Ms Tammy Cooper", "Ms Brooklyn Gordon", "Miss Deanna Matthews", "Mrs Anna Hart", "Ms Louise Berry", "Mrs Ava Elliott", "Mr Gabriel Moreno", "Ms Annette Gonzalez", "Mr Eli Ford")

        (1..120).map {
            Post(
                    0,
                    "ddsds",
                    Random.nextInt(40),
                    "Patna,Bihar",
                    "Tomorrow the school will be closed ",
                    "2W",
                    "https://randomuser.me/api/portraits/med/men/${Random.nextInt(100)}.jpg",
                    names[Random.nextInt(names.size)]
            )
        }.forEach {
            dao.insertNotice(it)
        }
    }
    // This adds automatically Date and Server headers to each response, and would allow you to configure
    // additional headers served to each response.
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
            call.respond(cause.getErrorResponse())
        }
    }
    // This uses use the logger to log every call (request/response)
    install(CallLogging)
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
        signUp(dao, hashFunction)
        notices(dao, hashFunction)
        schoolList(schoolsDao)
        quizLinks(questionsDataBase)
        notesLinks(notesDao)
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
        static("notes") {
            resources("templates/notes/")
        }
        carrerLibrary(dao, hashFunction)
    }
}

/**
 * Method that hashes a [password] by using the globally defined secret key [hmacKey].
 */
fun hash(password: String): String {
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
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