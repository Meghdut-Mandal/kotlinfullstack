package routes

import DeleteNotices
import KweetDelete
import KweetSession
import ViewKweet
import dao.ViveDao
import io.ktor.application.call
import io.ktor.http.Parameters
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import redirect
import verifyCode

/**
 * Registers a route for deleting deleting kweets.
 */
fun Route.delete(dao: ViveDao, hashFunction: (String) -> String) {

    get<DeleteNotices> {
        dao.resetData()
        call.respondText { "Done " }
    }

    // Uses the location feature to register a post route for '/kweet/{id}/routes.delete'.
    post<KweetDelete> {
        // Tries to get (null on failure) the user associated to the current KweetSession
        val user = call.sessions.get<KweetSession>()?.let { dao.user(it.userId) }

        // Receives the Parameters date and code, if any of those fails to be obtained,
        // it redirects to the tweet page without deleting the kweet.
        val post = call.receive<Parameters>()
        val id = it.id.toString()
        val date = post["date"]?.toLongOrNull() ?: return@post call.redirect(ViewKweet(id))
        val code = post["code"] ?: return@post call.redirect(ViewKweet(id))
        val kweet = dao.getKweet(id.toLong())

        // Verifies that the kweet user matches the session user and that the code and the date matches, to prevent CSFR.
        if (user == null || kweet?.userId != user.userId || !call.verifyCode(date, user, code, hashFunction)) {
            call.redirect(ViewKweet(id))
        } else {
            dao.deleteKweet(id.toInt())
        }
    }
}
