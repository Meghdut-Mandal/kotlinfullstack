package dao

import model.quiz.essential.ChapterSnap
import model.quiz.essential.SubjectSnap
import model.quiz.qbank.Question
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import org.dizitart.no2.objects.ObjectRepository
import java.io.File


class QuestionsDataBase(val subjectData: Nitrite, var questionData: Nitrite) {

    fun getSubjectsRepo(clazz: Int): ObjectRepository<SubjectSnap> {
        return subjectData.getRepository("class=$clazz", SubjectSnap::class.java)
    }

    fun getChaptersRepo(clazz: Int, subjectSnap: SubjectSnap): ObjectRepository<ChapterSnap> {
        return subjectData.getRepository("class=$clazz-${subjectSnap.slug}", ChapterSnap::class.java)
    }

    fun getQuestionRepo(clazz: Int, subjectSnap: SubjectSnap, chapterSnap: ChapterSnap): ObjectRepository<Question> {
        return questionData.getRepository("class=$clazz-${subjectSnap.id}-${chapterSnap.id}", Question::class.java)
    }

    fun forEach(clazz: Int, work: (SubjectSnap, ChapterSnap) -> Unit) {
        getSubjectsRepo(clazz).find().toList().forEach { subjectSnap: SubjectSnap ->
            getChaptersRepo(clazz, subjectSnap).find().toList().forEach { chapterSnap: ChapterSnap ->
                work(subjectSnap, chapterSnap)
            }
        }
    }

    fun commit() {
        subjectData.commit()
        questionData.commit()
    }
}


private val subjectsData = nitrite {
    file = File("subjectsData.db")
    compress = true
    autoCompact = true
}
private val questionData = nitrite {
    file = File("questionsData.db")
    compress = true
    autoCompact = true
}
private val questionsDataBase = QuestionsDataBase(subjectsData, questionData)
// data in class 9 physics motion
fun main() {
    (6..12).forEach { clazz ->
        val subjectsRepo = questionsDataBase.getSubjectsRepo(clazz)
        subjectsRepo.find().forEach { subjectSnap ->
            val chaptersRepo = questionsDataBase.getChaptersRepo(clazz, subjectSnap)
            chaptersRepo.find().forEach { chapterSnap ->
                val questionRepo =
                        questionsDataBase.getQuestionRepo(clazz, subjectSnap, chapterSnap)
                                questionRepo.find().forEach {
                    println("dao>>main  $it ")
                }

            }
        }

    }
}
