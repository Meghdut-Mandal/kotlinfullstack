package routes

import ImageConverter
import TeacherRequest
import TeacherRequest.SignUpPage
import com.google.gson.Gson
import dao.SubjectTaughtDao
import dao.TeacherDao
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
import model.*


fun Route.teachers(imageConverter: ImageConverter, teacherDao: TeacherDao, uploadsDao: UploadsDao, subjectTaughtDao: SubjectTaughtDao) {
    val stringResponseError = StringResponse(300, "Error in parameters")

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
                call.respond(ThymeleafContent("registration_sucesspage", mapOf()))
            else call.respond("Some internal error is there !")
        }
    }

    post<TeacherRequest.Uploads> {
        val post = call.receive<Parameters>()
        val id = post["email"] ?: return@post call.respond(stringResponseError)
        val uploads = uploadsDao.getUploads(id)
        call.respond(HttpStatusCode.OK, uploads)
    }

    get<TeacherRequest.Remove> {
        call.respond(ThymeleafContent("remove_teacher", mapOf()))
    }
    post<TeacherRequest.Info> {
        val post = call.receive<Parameters>()
        val id = post["email"] ?: return@post call.respond(stringResponseError)
        val teacher = teacherDao.getTeacher(id)
                ?: return@post call.respond(stringResponseError.copy(message = "Invalid Email"))
        call.respond(teacher.copy(hash = ""))
    }

    post<TeacherRequest.UploadID> {
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

    get<TeacherRequest.UploadNotes> {
        println("routes>>teachers   ")
        if (uploadsDao.hasUpload(it.upload_id)) {
            val upload = uploadsDao.getUpload(it.upload_id)
            return@get call.respond(upload)
        } else return@get call.respond(StringResponse(300, "Invaid Upload ID"))
    }

    post<TeacherRequest.UploadNotes> {
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
            val file = uploadsDao.getUploadFile(uploadId1)
            filePart!!.streamProvider().use { its ->
                // copy the stream to the file with buffering
                file.outputStream().buffered().use {
                    // note that this is blocking
                    its.copyTo(it)
                    println("routes>>teachers   ")
                    uploadsDao.updateStatus(uploadId1, Upload.RECEIVED)
                    Thread {
                        imageConverter.processUpload(uploadId1)
                    }.start()
                    return@post call.respond(StringResponse(200, "Successfully uploaded "))
                }
            }
            return@post call.respond(StringResponse(300, "Error in upload"))
        } else {
            call.respond(StringResponse(300, "Error in upload"))
        }
        multipart.forEachPart { part ->
            // make sure to dispose of the part after use to prevent leaks
            part.dispose()
        }


    }
}
