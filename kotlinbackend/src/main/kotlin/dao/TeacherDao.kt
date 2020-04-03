package dao

import model.Teacher
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.Nitrite

interface TeacherDao {
    fun addTeacher(teacher: Teacher): Boolean

    fun getTeacher(id: String): Teacher?

    fun addSubject(teacherId: String, subjectTaughID: String)

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

    override fun addSubject(teacherId: String, subjectTaughID: String) {
        println("dao>TeacherDaoImpl>addSubject   ")
        getTeacher(teacherId)?.let {
            if (it.subjects.find { it == subjectTaughID } == null) {
                it.subjects.add(subjectTaughID)
                repository.update(it)
            }
        }
    }

    override fun hasTeacher(id: String) = getTeacher(id) != null
}