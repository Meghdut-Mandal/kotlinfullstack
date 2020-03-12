package routes

import AttandanceRequest
import dao.AttendanceDAO
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.response.respond
import io.ktor.routing.Route
import model.AttendanceModel

fun Route.attandanceHelper(attendanceDAO: AttendanceDAO) {

    get<AttandanceRequest> {
        val attendance = attendanceDAO.getAttendance(it.teacherID)

        call.respond(attendance)

    }

    post<AttendanceModel> {

    }
}