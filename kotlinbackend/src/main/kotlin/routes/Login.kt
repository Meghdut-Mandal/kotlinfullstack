package routes

import Index
import KweetSession
import Login
import Logout
import UserPage
import dao.ViveDao
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import redirect
import userNameValid

/**
 * Registers the [Login] and [Logout] routes '/login' and '/logout'.
 */
fun Route.login(dao: ViveDao, hash: (String) -> String) {
    /**
     * A GET request to the [Login], would respond with the login page
     * (unless the user is already logged in, in which case it would redirect to the user's page)
     */
    get<Login> {
        val user = call.sessions.get<KweetSession>()?.let { dao.user(it.userId) }

        if (user != null) {
            call.redirect(UserPage(user.userId))
        } else {
            call.respond(FreeMarkerContent("login.ftl", mapOf("userId" to it.userId, "error" to it.error), ""))
        }
    }

    /**
     * A POST request to the [Login] actually processes the [Parameters] to validate them, if valid it sets the session.
     * It will redirect either to the [Login] page with an error in the case of error,
     * or to the [UserPage] if the login was successful.
     */
    post<Login> {
        val post = call.receive<Parameters>()
        val userId = post["userId"] ?: return@post call.redirect(it)
        val password = post["password"] ?: return@post call.redirect(it)

        val error = Login(userId)

        val login = when {
            userId.length < 4 -> null
            password.length < 6 -> null
            !userNameValid(userId) -> null
            else -> dao.user(userId, hash(password))
        }

        if (login == null) {
            call.redirect(error.copy(error = "Invalid username or password"))
        } else {
            call.sessions.set(KweetSession(login.userId))
            call.redirect(UserPage(login.userId))
        }
    }

    /**
     * A GET request to the [Logout] page, removes the session and redirects to the [Index] page.
     */
    get<Logout> {
        call.sessions.clear<KweetSession>()
        call.redirect(Index())
    }
}
