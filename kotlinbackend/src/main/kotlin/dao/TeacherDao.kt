package dao

import model.SubjectTaught
import model.Teacher
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.Nitrite

interface TeacherDao {
    fun addTeacher(teacher: Teacher): Boolean

    fun getTeacher(id: String): Teacher?

    fun addSubject(teacherId: String, subjectTaught: SubjectTaught)

    fun hasTeacher(id: String): Boolean
}


class TeacherDaoImpl(val teacherDb: Nitrite) : TeacherDao {
    val repository by lazy {
        teacherDb.getRepository(Teacher::class.java)
    }

    override fun addTeacher(teacher: Teacher): Boolean {
        val count = repository.find(Teacher::id eq teacher.id).count()
        if (count == 0) {
            repository.insert(teacher)
            return true
        }
        return false
    }

    override fun getTeacher(id: String): Teacher? {
        return repository.find(Teacher::id eq id).firstOrNull()
    }

    override fun addSubject(teacherId: String, subjectTaught: SubjectTaught) {
        getTeacher(teacherId)?.let {
            it.subjects.add(subjectTaught)
            repository.update(getTeacher(teacherId))
        }
    }

    override fun hasTeacher(id: String) = getTeacher(id) != null
}