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
//        // Tries to get the user from the session (null if failure)
//        val user = call.sessions.get<KweetSession>()?.let { dao.user(it.userId) }
//
//        // Obtains several list of kweets using different sortings and filters.
//        val top = dao.top(10).map { dao.getKweet(it) }
//        val latest = dao.latest(10).map { dao.getKweet(it) }
//
//        // Generates an ETag unique string for this route that will be used for caching.
//        val etagString =
//                user?.userId + "," + top.joinToString { it?.id.toString() } + latest.joinToString { it?.id.toString() }
//        val etag = etagString.hashCode()
//
//        // Uses FreeMarker to render the page.
//        call.respond(FreeMarkerContent("index.ftl", mapOf("top" to top, "latest" to latest, "user" to user), etag.toString()))
    }

    get<BootRequest> {
        call.respond("Booted !")
    }
}
