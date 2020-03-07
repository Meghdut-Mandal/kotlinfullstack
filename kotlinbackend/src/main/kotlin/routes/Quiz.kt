package routes

import ChaptersRequest
import QuestionRequests
import SubjectsRequest
import dao.QuestionsDataBase
import io.ktor.application.call
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import model.quiz.essential.SubjectSnap
import model.quiz.qbank.Question
import org.dizitart.no2.FindOptions
import org.dizitart.no2.exceptions.ValidationException

class AbstractAPIResponse<T>(val statusCode: Int, val skip: Int = 0, val limit: Int, val dataList: List<T>)


@KtorExperimentalLocationsAPI
fun Route.quizLinks(questionsDataBase: QuestionsDataBase) {

    get<SubjectsRequest> {
        val find = questionsDataBase.getSubjectsRepo(it.clazz).find().toList()
        val apiResponse = AbstractAPIResponse<SubjectSnap>(200, 0, find.size, find)
        call.respond(apiResponse)
    }

    get<ChaptersRequest> { request ->
        val first = questionsDataBase.getSubject(request.clazz, request.subject)
        val find = questionsDataBase.getChaptersRepo(request.clazz, first).find().toList()
        val apiResponse = AbstractAPIResponse(200, 0, find.size, find)
        call.respond(apiResponse)
    }

    get<QuestionRequests> { request ->
        val subjectSnap = questionsDataBase.getSubject(request.clazz, request.subject)
        val chapterSnap =
                questionsDataBase.getChapter(request.clazz, subjectSnap, request.chapter)
        val find = try {
            questionsDataBase.getQuestionRepo(request.clazz, subjectSnap, chapterSnap).find(FindOptions(request.skip, 10)).toList()
        } catch (e: ValidationException) {
             arrayListOf<Question>()
        }
        val apiResponse = AbstractAPIResponse(200, request.skip, find.size, find)
        call.respond(apiResponse)
    }


}

