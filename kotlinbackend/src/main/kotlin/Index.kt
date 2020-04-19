import dao.ViveDao
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route

/**
 * Register the index route of the website.
 */
fun Route.index(dao: ViveDao) {
//    // Uses the location feature to register a get route for '/'.
    get<Index> {
        call.respondRedirect("/home")
    }


    get<BootRequest> {
        call.respond("Booted !")
    }
}
