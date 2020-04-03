package dao

import hash
import model.Batch
import model.SubjectTaught
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.Nitrite

interface SubjectTaughtDao {

    /*
    Get the subject taught in a particular class and section 
     */
    fun getSubjects(batch: Batch): List<SubjectTaught>

    /*
     Add a subject to the list of subjects
     */
    fun addSubject(batch: Batch, subjectName: String, subjectSlug: String): String


    /*

     */
    fun hasSubject(subjectId: String): Boolean

}

class SubjectTaughtDaoImpl(val subjectsDb: Nitrite) : SubjectTaughtDao {

    val repository by lazy {
        subjectsDb.getRepository(SubjectTaught::class.java)
    }

    override fun getSubjects(batch: Batch): List<SubjectTaught> {
        return repository.find().filter { it.batch == batch }.toList()
    }

    override fun addSubject(batch: Batch, subjectName: String, subjectSlug: String): String {
        val hashed = genHash(batch, subjectName, subjectSlug)
        return if (!hasSubject(hashed)) {
            val subjectTaught = SubjectTaught(hashed, batch, subjectName, subjectSlug)
            repository.insert(subjectTaught)
            hashed
        } else hashed

    }

    private fun genHash(batch: Batch, subjectName: String, subjectSlug: String) =
            hash(batch, subjectName, subjectSlug)

    override fun hasSubject(subjectId: String): Boolean {
        return repository.find(SubjectTaught::id eq subjectId).any()
    }


}