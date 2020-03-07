package dao

import model.quiz.essential.ChapterSnap
import model.quiz.essential.SubjectSnap
import model.quiz.qbank.Question
import org.dizitart.no2.Nitrite
import org.dizitart.no2.objects.ObjectRepository


class QuestionsDataBase(subjectData: Nitrite, var questionData: Nitrite) : SubjectsDataBase(subjectData) {

    fun getQuestionRepo(clazz: Int, subjectSnap: SubjectSnap, chapterSnap: ChapterSnap): ObjectRepository<Question> {
        return questionData.getRepository("class=$clazz-${subjectSnap.id}-${chapterSnap.id}", Question::class.java)
    }

    fun commit() {
        subjectData.commit()
        questionData.commit()
    }
}


