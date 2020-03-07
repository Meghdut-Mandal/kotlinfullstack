package routes

import KweetSession
import UserPage
import dao.ViveDao
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions

/**
 * Register the [UserPage] route '/user/{user}',
 * with the user profile.
 */
fun Route.userPage(dao: ViveDao) {
    /**
     * A GET request will return a page with the profile of a given user from its [UserPage.user] name.
     * If the user doesn't exists, it will return a 404 page instead.
     */
    get<UserPage> {
        val user = call.sessions.get<KweetSession>()?.let { dao.user(it.userId) }
        val pageUser = dao.user(it.user)

        if (pageUser == null) {
            call.respond(HttpStatusCode.NotFound.description("User ${it.user} doesn't exist"))
        } else {
            val kweets = dao.userKweets(it.user).map { dao.getKweet(it) }

            call.respond(FreeMarkerContent("user.ftl", mapOf("user" to user, "pageUser" to pageUser, "kweets" to kweets), "etag"))
        }
    }
}
