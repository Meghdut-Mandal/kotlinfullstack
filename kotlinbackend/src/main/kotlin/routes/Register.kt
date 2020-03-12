package routes

import KweetSession
import Register
import UserPage
import dao.ViveDao
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import model.User
import redirect

/**
 * Register routes for user registration in the [Register] route (/register)
 */
fun Route.register(dao: ViveDao, hashFunction: (String) -> String) {

    post<Register> {
        val user = call.receive<User>()
        dao.createUser(user)
        call.respond("Sucess !")
    }


    /**
     * A GET request would show the registration form (with an error if specified by the URL in the case there was an error in the form processing)
     * If the user is already logged, it redirects the client to the [UserPage] instead.
     */
    get<Register> {
        val user = call.sessions.get<KweetSession>()?.let { dao.user(it.userId) }
        if (user != null) {
            call.redirect(UserPage(user.userId))
        } else {
            call.respond(FreeMarkerContent("register.ftl", mapOf("pageUser" to User(it.userId, it.email, it.displayName, "", it.phoneNumber), "error" to it.error), ""))
        }
    }
}
