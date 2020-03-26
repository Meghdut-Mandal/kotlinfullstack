package routes

import AttendanceRequest
import dao.AttendanceDAO
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import model.AbstractAPIResponse
import model.AttendanceModel

fun Route.attandanceHelper(attendanceDAO: AttendanceDAO) {

    get<AttendanceRequest.Model> {
        val attendance = attendanceDAO.getAttendance(it.attendanceRequest.teacherID)
        call.respond(attendance)
    }

    post<AttendanceRequest.Model> {
        val receive = call.receive<AttendanceModel>()
        attendanceDAO.updateAttendance(it.attendanceRequest.teacherID, receive)
        call.respond(AbstractAPIResponse(200, "Sucessfully Updated"))
    }

    post<AttendanceRequest.AddStudent> {
        attendanceDAO.addStudent(it.attendanceRequest.teacherID, it.studentID)
        call.respond(AbstractAPIResponse(200, "Sucessfully Updated"))
    }
}