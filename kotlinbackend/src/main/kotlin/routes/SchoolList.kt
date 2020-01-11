package routes

import SchoolsList
import dao.SchoolListDAO
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.thymeleaf.ThymeleafContent


fun Route.schoolList(schoolListDAO: SchoolListDAO) {
    get<SchoolsList>{
        val schools = schoolListDAO.getSchools(it.offset, 20)
        call.respond(ThymeleafContent("schools", mapOf("list" to schools,"nextPage" to "/school/list/${it.offset+schools.size}")))
    }
}