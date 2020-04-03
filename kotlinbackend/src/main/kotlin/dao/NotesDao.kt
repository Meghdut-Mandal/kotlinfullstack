package dao

import gson
import hash
import model.SubjectTaught
import model.notes.Note
import model.notes.NotePage
import model.quiz.essential.ChapterSnap
import model.quiz.essential.SubjectSnap
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.Nitrite
import java.io.File

class NotesDao(notesDb: Nitrite, subjectData: Nitrite, val root: File) : SubjectsDataBase(subjectData) {

    private val repository by lazy {
        notesDb.getRepository(Note::class.java)
    }

    fun addNote(subjectTaught: SubjectTaught, name: String, pagesList: List<NotePage>): Boolean {
        val hash = genHash(subjectTaught, name)
        if (!hasNote(hash)) {
            val note = Note(hash, name, pagesList, pagesList.size)
            repository.insert(note)
            return true
        }
        return false
    }

    fun hasNote(noteID: String): Boolean {
        return repository.find(Note::id eq noteID).any()
    }

    fun getNote(clazz: Int, subjectSnap: SubjectSnap, chapterSnap: ChapterSnap): Note {
        val notesFolder = getNotesFolder(clazz, subjectSnap, chapterSnap)
        val imageList = notesFolder.listFiles()?.toList() ?: arrayListOf()
        imageList.sortedBy { it.nameWithoutExtension }
        val notePages =
                imageList.mapIndexed { index, file -> NotePage(index, "Page $index", file.name) }
        val note =
                Note(hash("" + Math.random()), "Class $clazz ${subjectSnap.name} - ${chapterSnap.name}", notePages, notePages.size)
        return note
    }

    fun getNotesFolder(clazz: Int, subjectSnap: SubjectSnap, chapterSnap: ChapterSnap) =
            File(root, hash("$clazz/${subjectSnap.slug}/${chapterSnap.slug}"))

    fun getNotesFolder(subjectTaught: SubjectTaught, chapterName: String) = File(root, genHash(subjectTaught, chapterName))

    private fun genHash(subjectTaught: SubjectTaught, chapterName: String) =
            hash(gson.toJson(subjectTaught) + chapterName)
}