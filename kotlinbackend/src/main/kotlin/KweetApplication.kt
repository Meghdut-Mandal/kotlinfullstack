import dao.DAONitrateDataBase
import dao.SchoolListDAO
import dao.ViveDao
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.content.files
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.locations
import io.ktor.request.header
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.thymeleaf.Thymeleaf
import io.ktor.util.hex
import model.User
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import routes.*
import java.io.File
import java.net.URI
import java.text.DateFormat
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random


val port = Integer.valueOf(System.getenv("PORT"))

/*
 * Classes used for the locations feature to build urls and register routes.
 */
@Location("/")
class Index

@Location("/post-new")
class PostNew

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
 * Pool of JDBC connections used.
 */


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
fun main() {

    embeddedServer(Netty, port, module = Application::main).start()
    println(">>main  Running at http://localhost:$port/ ")
}

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


fun Application.mainWithDependencies(dao: ViveDao) {
    if (dao.getNoticeCount() == 0) {
        (1..120).map {
            Post(
                    Random.nextInt(200).toLong(),
                    12,
                    "Patna,Bihar",
                    "Tomorrow the school will be closed ",
                    "2W",
                    "https://randomuser.me/api/portraits/med/men/75.jpg",
                    "Vivek Kr Yadav"
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
            val errorMessage = """
                Error !! 
                Short Message :- $cause.localizedMessage
                Stack Trace   :-${cause.stackTrace.joinToString(separator = "\n")}
            """.trimIndent()
            call.respondText { errorMessage }
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
        postNew(dao, hashFunction)
        delete(dao, hashFunction)
        userPage(dao)
        viewKweet(dao, hashFunction)
        login(dao, hashFunction)
        register(dao, hashFunction)
        signUp(dao, hashFunction)
        notices(dao, hashFunction)
        schoolList(schoolsDao)
        static("styles") {
            resources("styles/")
        }
        static("carreir_lib") {
            resources("templates/carreir_lib/")

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
    val host = request.host() ?: "localhost"
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