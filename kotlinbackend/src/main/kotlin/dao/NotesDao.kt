package dao

import model.notes.Note
import model.notes.NotePage
import model.quiz.essential.ChapterSnap
import model.quiz.essential.SubjectSnap
import org.dizitart.no2.Nitrite
import java.io.File

class NotesDao(subjectData: Nitrite, val root: File) : SubjectsDataBase(subjectData) {
    fun getNotes(clazz: Int, subjectSnap: SubjectSnap, chapterSnap: ChapterSnap): Note {
        val notesFolder = getNotesFolder(clazz, subjectSnap, chapterSnap)
        val imageList = notesFolder.listFiles()?.toList() ?: arrayListOf()
        imageList.sortedBy { it.nameWithoutExtension }
        val notePages =
                imageList.mapIndexed { index, file -> NotePage(index, "Page $index", file.name) }
        val note =
                Note("Class $clazz ${subjectSnap.name} - ${chapterSnap.name}", notePages, notePages.size)
        return note
    }

    fun getNotesFolder(clazz: Int, subjectSnap: SubjectSnap, chapterSnap: ChapterSnap) =
            File(root, "$clazz/${subjectSnap.slug}/${chapterSnap.slug}")
}