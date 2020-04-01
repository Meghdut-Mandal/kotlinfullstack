package routes

import TeacherRequest
import dao.TeacherDao
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.thymeleaf.ThymeleafContent


/*
@Location("/teacher/")
class TeacherRequest
{
    @Location("/register")
    class SignUpPage

    @Location("/login")
    class LogInRequest
}
 */

fun Route.teachers(teacherDao: TeacherDao) {
    get<TeacherRequest.SignUpPage> {
        call.respond(ThymeleafContent("teacher_signup", mapOf()))
    }
    post<TeacherRequest.SignUpPage> {

        call.respond("done bro !!")
    }
}