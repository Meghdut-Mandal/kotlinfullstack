package routes

import NotePageRequest
import StudentAPI
import dao.NotesDao
import dao.SubjectTaughtDao
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.Route
import model.Batch
import model.StringResponse
import java.io.File

fun Route.student(subjectTaughtDao: SubjectTaughtDao, notesDao: NotesDao) {

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

    get<NotePageRequest> {
        val file = File("notes/${it.id}", "p${it.pageno}.jpg")
        if (file.exists())
            call.respondFile(file)
        else call.respond(HttpStatusCode.NotFound, StringResponse(404, "Page not found"))
    }

}
