package routes

import KweetSession
import Login
import NoticeCreate
import PostNew
import ViewKweet
import dao.ViveDao
import gson
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.Parameters
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import model.Notice
import redirect
import securityCode
import verifyCode
import kotlin.random.Random

/**
 * Register routes for the [PostNew] route '/post-new'
 */
fun Route.postNew(dao: ViveDao, hashFunction: (String) -> String) {
    /**
     * A GET request returns a page with a form to post a new Kweet in the case the user
     * is logged also generating a [code] token to prevent.
     *
     * If the user is not logged it redirects to the [Login] page.
     */
    get<PostNew> {
        val user = call.sessions.get<KweetSession>()?.let { dao.user(it.userId) }

        if (user == null) {
            call.redirect(Login())
        } else {
            val date = System.currentTimeMillis()
            val code = call.securityCode(date, user, hashFunction)

            call.respond(FreeMarkerContent("new-kweet.ftl", mapOf("user" to user, "date" to date, "code" to code), user.userId))
        }
    }
    /**
     * A POST request actually tries to create a new [Notice].
     * It validates the `date`, `code` and `text` parameters and redirects to the login page on failure.
     * On success it creates the new [Notice] and redirect to the [ViewKweet] page to view that specific Kweet.
     */
    post<PostNew> {
        val user = call.sessions.get<KweetSession>()?.let { dao.user(it.userId) }

        val post = call.receive<Parameters>()
        val date = post["date"]?.toLongOrNull() ?: return@post call.redirect(it)
        val code = post["code"] ?: return@post call.redirect(it)
        val text = post["text"] ?: return@post call.redirect(it)

        if (user == null || !call.verifyCode(date, user, code, hashFunction)) {
            call.redirect(Login())
        } else {
//            val kweet=Kweet("sdsd","",23,"")
//            val id = dao.createKweet(user.userId, text, null)
//            call.redirect(ViewKweet(id.toString()))
        }
    }

    post<NoticeCreate> {
        val receiveText = call.receiveText()
        val kweet = gson.fromJson(receiveText, Notice::class.java)
        dao.createKweet(kweet.copy(id = System.currentTimeMillis() + Random.nextLong(34)))
        println("routes>>postNew  $kweet ")
        call.respond("Done Bro !!")
    }
}