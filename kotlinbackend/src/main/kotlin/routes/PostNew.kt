package routes

import NoticeCreate
import dao.ViveDao
import gson
import io.ktor.application.call
import io.ktor.locations.post
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Route
import model.Notice

/**
 * Register routes for the [PostNew] route '/post-new'
 */
fun Route.postNew(dao: ViveDao, hashFunction: (String) -> String) {

/*
A new notice with a new id is created
 */
    post<NoticeCreate> {
        val receiveText = call.receiveText()
        val kweet = gson.fromJson(receiveText, Notice::class.java)
        val notice = kweet.copy(id = System.currentTimeMillis() + System.nanoTime())
        dao.createKweet(notice)
        println("routes>>postNew  $notice ")
        call.respond(notice)
    }
}