package routes

import ImageConverter
import TeacherAPI
import TeacherAPI.SignUpPage
import com.google.gson.Gson
import dao.SubjectTaughtDao
import dao.TeacherDao
import dao.UploadsDao
import gson
import hash
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.thymeleaf.ThymeleafContent
import model.StringResponse
import model.SubjectTaught
import model.Teacher

val stringResponseError = StringResponse(300, "Error in parameters")

fun Route.teachers(imageConverter: ImageConverter, teacherDao: TeacherDao, uploadsDao: UploadsDao, subjectTaughtDao: SubjectTaughtDao) {

    post<TeacherAPI.LogInRequest> {
        val post = call.receive<Parameters>()

        val email = post["email"]
                ?: return@post call.respond(ThymeleafContent("teacher_signup", mapOf("error" to "The Email Should not be empty")))
        val password = post["psw"]
                ?: return@post call.respond(ThymeleafContent("teacher_signup", mapOf("error" to "The Password Should not be empty")))

        val teacher = teacherDao.getTeacher(email)
                ?: return@post call.respond(StringResponse(1, "Teacher not found"))
        val calculated = hash(email, password)
        if (teacher.hash == calculated)
            return@post call.respond(StringResponse(0, gson.toJson(teacher)))
        else return@post call.respond(StringResponse(2, "Incorrect Password"))

    }
    ///  attendance.db  data.db  dummy  edugorrilas.db  notes.db  questionsData.db  subjectsData.db  subjects_taught.db  teacher.db  uploads.db


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
                    Teacher(userEmail, hash(userEmail, userPassword), userName, arrayListOf())
            val sucess = teacherDao.addTeacher(teacher)
            if (sucess)
                call.respond(ThymeleafContent("registration_sucesspage", mapOf()))
            else call.respond("Some internal error is there !")
        }
    }

    post<TeacherAPI.Uploads> {
        val post = call.receive<Parameters>()
        val id = post["email"] ?: return@post call.respond(stringResponseError)
        val uploads = uploadsDao.getUploads(id)
        call.respond(HttpStatusCode.OK, uploads)
    }

    get<TeacherAPI.Remove> {
        call.respond(ThymeleafContent("remove_teacher", mapOf()))
    }
    post<TeacherAPI.Info> {
        val post = call.receive<Parameters>()
        val id = post["email"] ?: return@post call.respond(stringResponseError)
        val teacher = teacherDao.getTeacher(id)
                ?: return@post call.respond(stringResponseError.copy(message = "Invalid Email"))
        call.respond(teacher.copy(hash = ""))
    }

    post<TeacherAPI.UploadID> {
        val post = call.receive<Parameters>()
        val teacherID = post["teacherid"] ?: return@post call.respond(stringResponseError)
        val taught = post["taughtby"] ?: return@post call.respond(stringResponseError)
        val chapterName = post["chaptername"] ?: return@post call.respond(stringResponseError)
        val (id, batch, subjectName, subjectSlug) = Gson().fromJson(taught, SubjectTaught::class.java)

        val subjectID =
                subjectTaughtDao.addSubject(batch, subjectName, subjectSlug)
        teacherDao.addSubject(teacherID, subjectID)


        val upload = uploadsDao.addUpload(teacherID, subjectID, chapterName)

        return@post call.respond(StringResponse(200, upload))
    }

    get<TeacherAPI.UploadNotes> {
        println("routes>>teachers   ")
        if (uploadsDao.hasUpload(it.upload_id)) {
            val upload = uploadsDao.getUpload(it.upload_id)
            return@get call.respond(upload)
        } else return@get call.respond(StringResponse(300, "Invaid Upload ID"))
    }

    post<TeacherAPI.UploadNotes> {
        val multipart = call.receiveMultipart()
        var uploadId: String? = null
        var filePart: PartData.FileItem? = null
        multipart.forEachPart { part ->
            // if part is a file (could be form item)
            if (part is PartData.FileItem) {
                filePart = part
            } else if (part is PartData.FormItem)
                uploadId = part.value

        }
        if (uploadId != null) {
            val uploadId1 = uploadId!!
            uploadsDao.hasUpload(uploadId1)
            if (uploadsDao.hasUpload(uploadId1)) {
                val file = uploadsDao.getUploadFile(uploadId1)
                filePart!!.streamProvider().use { inputStream ->
                    // copy the stream to the file with buffering
                    file.outputStream().buffered().use { outputStream ->
                        // note that this is blocking
                        inputStream.copyTo(outputStream)
                        imageConverter.processUpload(uploadId1)
                        return@post call.respond(StringResponse(200, "Successfully uploaded "))
                    }
                }
            } else return@post call.respond(StringResponse(300, "Upload not registered "))
        } else {
            call.respond(StringResponse(300, "Error in upload"))
        }
        multipart.forEachPart { part ->
            // make sure to dispose of the part after use to prevent leaks
            part.dispose()
        }


    }
}
