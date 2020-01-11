package dao

import RatingResponse
import org.dizitart.kno2.nitrite
import org.dizitart.no2.FindOptions
import java.io.File

class SchoolListDAO(val dbFile: File) {
    val db = nitrite {
        file = dbFile
    }
    val schoolsRepo = db.getRepository(RatingResponse::class.java)

    fun getTotalSchools(): Int {
        return schoolsRepo.find().totalCount()
    }

    fun getSchools(offset: Int, limit: Int): MutableList<RatingResponse> {
        return schoolsRepo.find(FindOptions.limit(offset, limit)).toList() ?: arrayListOf()
    }
}

fun main() {
    val schoolListDAO=SchoolListDAO(File("data/edugorrilas.db"))
    println("dao>>main ${schoolListDAO.getTotalSchools()}  ")
}