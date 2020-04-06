package routes

import NotePageRequest
import StudentAPI
import dao.NotesDao
import dao.SubjectTaughtDao
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.LocalFileContent
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.Route
import model.Batch
import model.StringResponse
import java.io.File

fun Route.student(subjectTaughtDao: SubjectTaughtDao, notesDao: NotesDao, subjectImageDir: File = File("sub_image")) {

    post<StudentAPI.Subjects> {
        val batch = call.receive<Batch>()
        val subjects = subjectTaughtDao.getSubjects(batch)
        return@post call.respond(subjects)
    }

    get<StudentAPI.Notes> {
        val id = it.subject_id
        val notes = notesDao.getNotes(id)
        call.respond(notes)
    }

    get<StudentAPI.Subjects.Image> {
        val file = File(subjectImageDir, "${it.slug}.svg")
        if (file.exists())
            call.respond(LocalFileContent(file))
        else call.respond(LocalFileContent(File(subjectImageDir, "default.svg")))
    }


    get<NotePageRequest> {
        val file = File("notes/${it.id}", "p${it.pageno}.jpg")
        if (file.exists())
            call.respondFile(file)
        else call.respond(HttpStatusCode.NotFound, StringResponse(404, "Page not found"))
    }

}
