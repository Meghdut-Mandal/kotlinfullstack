package routes

import TeacherRequest.SignUpPage
import dao.TeacherDao
import io.ktor.application.call
import io.ktor.http.Parameters
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.thymeleaf.ThymeleafContent
import model.Teacher
import java.security.MessageDigest
import java.util.*


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
fun hash(string: String): String {
    val data = string.toByteArray()
    val digester = MessageDigest.getInstance("SHA-256")
    digester.update(data)
    return Base64.getEncoder().encodeToString(digester.digest())
}

fun Route.teachers(teacherDao: TeacherDao) {
    get<SignUpPage> {
        call.respond(ThymeleafContent("teacher_signup", mapOf()))
    }
    post<SignUpPage> {
        val post = call.receive<Parameters>()
        val userName = post["name"]
                ?: return@post call.respond(ThymeleafContent("teacher_signup", mapOf("error" to "The name Should not be empty")))
        val userEmail = post["email"]
                ?: return@post call.respond(ThymeleafContent("teacher_signup", mapOf("error" to "The Email Should not be empty")))
        val userPassword = post["psw"]
                ?: return@post call.respond(ThymeleafContent("teacher_signup", mapOf("error" to "The Password Should not be empty")))
        if (teacherDao.hasTeacher(userEmail)) {
            return@post call.respond(ThymeleafContent("teacher_signup", mapOf("error" to "The email has been already used. Try some other Email.")))
        } else {
            val teacher =
                    Teacher(userEmail, hash(userEmail + userPassword), userName, arrayListOf())
            val sucess = teacherDao.addTeacher(teacher)
            if (sucess)
                call.respond("Registration done sucessfully for $teacher")
            else call.respond("Some internal error is there !")
        }
    }
}