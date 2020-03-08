package routes

import SignUp
import dao.ViveDao
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.thymeleaf.ThymeleafContent

fun Route.signUp(dao: ViveDao, hashFunction: (String) -> String) {


    get<SignUp> {
        call.respond(ThymeleafContent("signup", mapOf("user" to "abc")))
    }
}
