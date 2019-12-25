import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.content.*
import io.ktor.response.header
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.io.File


fun main() {
    embeddedServer(Netty, 8080, module = Application::module).start()
}

fun Route.recursiveAdd(folder: File) {
    files(folder)
    static(folder.name) {
        folder.listFiles().filter { it.isDirectory }.forEach {
            println(">>recursiveAdd  $it ")
            recursiveAdd(it)
        }
    }
}
fun Application.module() {
    install(DefaultHeaders)

    install(Routing) {
        get("/api/ping/{count?}") {
            println(">>main  pinged ")
            var count: Int = Integer.valueOf(call.parameters["count"] ?: "1")
            if (count < 1) {
                count = 1
            }

            val obj = Array(count, { i -> Entry("$i: fuck you ") })
            val gson = Gson()
            val str = gson.toJson(obj)
            call.response.header("Access-Control-Allow-Origin", "*")
            call.respondText(str, ContentType.Application.Json)
        }

        static("/") {
            val folder = File("${System.getProperty("user.dir")}/web")
            staticRootFolder = folder
            recursiveAdd(folder)
            file("index.html")
            default("index.html")

        }

    }
}


data class Entry(val message: String)