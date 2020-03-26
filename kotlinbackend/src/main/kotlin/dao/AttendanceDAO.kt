package dao

import model.AttendanceModel
import model.Status
import model.currentDateString
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.Nitrite
import org.dizitart.no2.objects.ObjectRepository

class AttendanceDAO(private val nitrite: Nitrite) {

    private fun getAttendanceRepo(teacherID: String): ObjectRepository<AttendanceModel> {
        return nitrite.getRepository(teacherID, AttendanceModel::class.java)
    }

    private fun getStudentRepo(teacherID: String): ObjectRepository<String> {
        return nitrite.getRepository(teacherID, String::class.java)
    }

    private fun getTodaysAttendanceModel(teacherID: String): AttendanceModel {
        val studentRepo = getStudentRepo(teacherID)
        val idList = studentRepo.find()?.toList() ?: arrayListOf()
        val studentOf =
                idList.map { Status(it, AttendanceModel.UNKNOWN) }
        val attendanceModel = AttendanceModel(teacherID, studentOf)
        getAttendanceRepo(teacherID).insert(attendanceModel)
        return attendanceModel
    }

    fun getAttendance(teacherID: String) =
            getAttendanceRepo(teacherID)
                    .find(AttendanceModel::takerUserId eq currentDateString)
                    .firstOrNull() ?: getTodaysAttendanceModel(teacherID)

    fun updateAttendance(teacherID: String, attendanceModel: AttendanceModel) {
        val attendanceRepo = getAttendanceRepo(teacherID)
        attendanceRepo.update(attendanceModel)
    }

    fun addStudent(teacherID: String, studentID: String) {
        val studentRepo = getStudentRepo(teacherID)
        val firstOrNull = studentRepo.find()?.firstOrNull { it == studentID }
        if (firstOrNull == null) {
            studentRepo.insert(studentID)
        }
    }


}