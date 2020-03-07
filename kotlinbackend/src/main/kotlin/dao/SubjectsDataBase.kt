package dao

import model.quiz.essential.ChapterSnap
import model.quiz.essential.SubjectSnap
import org.dizitart.no2.Nitrite
import org.dizitart.no2.objects.ObjectRepository

open class SubjectsDataBase(val subjectData: Nitrite) {
    fun getSubjectsRepo(clazz: Int): ObjectRepository<SubjectSnap> {
        return subjectData.getRepository("class=$clazz", SubjectSnap::class.java)
    }

    fun getChaptersRepo(clazz: Int, subjectSnap: SubjectSnap): ObjectRepository<ChapterSnap> {
        return subjectData.getRepository("class=$clazz-${subjectSnap.slug}", ChapterSnap::class.java)
    }

    fun forEach(clazz: Int, work: (SubjectSnap, ChapterSnap) -> Unit) {
        getSubjectsRepo(clazz).find().toList().forEach { subjectSnap: SubjectSnap ->
            getChaptersRepo(clazz, subjectSnap).find().toList().forEach { chapterSnap: ChapterSnap ->
                work(subjectSnap, chapterSnap)
            }
        }
    }

    fun getSubject(clazz: Int, subjectSlug: String) =
            this.getSubjectsRepo(clazz).find().first { it.slug == subjectSlug }

    fun getChapter(clazz: Int, subjectSnap: SubjectSnap, chapterSlug: String) =
            this.getChaptersRepo(clazz, subjectSnap).find().first { it.slug == chapterSlug }
}