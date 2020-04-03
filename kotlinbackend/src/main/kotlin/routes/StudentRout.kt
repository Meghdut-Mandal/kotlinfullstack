package routes

import StudentAPI
import dao.NotesDao
import dao.SubjectTaughtDao
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import model.Batch

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

}
