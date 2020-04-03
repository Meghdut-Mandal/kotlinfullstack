package routes

import NoteFileRequest
import NoteRequest
import dao.NotesDao
import io.ktor.application.call
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.Route

@KtorExperimentalLocationsAPI
fun Route.notesLinks(notesDao: NotesDao) {

    get<NoteRequest> { noteRequest ->
        val subjectSnap = notesDao.getSubject(noteRequest.clazz, noteRequest.subject)
        val chapterSnap =
                notesDao.getChapter(noteRequest.clazz, subjectSnap, noteRequest.chapter)
        val notes = notesDao.getNote(noteRequest.clazz, subjectSnap, chapterSnap)
        call.respond(notes)
    }


    get<NoteFileRequest> { noteRequest ->
        val subjectSnap = notesDao.getSubject(noteRequest.clazz, noteRequest.subject)
        val chapterSnap =
                notesDao.getChapter(noteRequest.clazz, subjectSnap, noteRequest.chapter)
        val noteFolder = notesDao.getNotesFolder(noteRequest.clazz, subjectSnap, chapterSnap)
        call.respondFile(noteFolder, noteRequest.fileName)
    }

}