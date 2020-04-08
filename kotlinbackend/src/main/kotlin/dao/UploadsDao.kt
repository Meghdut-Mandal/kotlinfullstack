package dao

import hash
import model.Upload
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.filters.text
import org.dizitart.no2.FindOptions
import org.dizitart.no2.Nitrite
import org.dizitart.no2.SortOrder
import java.io.File

interface UploadsDao {
    /*
     get the 10 recent Uploads made by the teacher
     */
    fun getRecentUploads(teacherId: String): List<Upload>


    /*
     update the status of upload of the file
     */
    fun updateStatus(uploadId: String, newStatus: String)

    /*
    Checks if the upload with the given ID exists
     */
    fun hasUpload(uploadId: String): Boolean

    /*
     Get the upload having the given ID
     */
    fun getUpload(uploadId: String): Upload

    /*
    Get the path of the file stored in the temp dir
     */
    fun getUploadFile(uploadId: String): File

    /*
    Will return the id of the upload from the database
     */
    fun addUpload(teacherId: String, subjectTaught: String, chapterName: String): String

    /*
     Get the uploads of the given teacher
     */
    fun getUploads(teacherId: String): List<Upload>

}

class UploadDaoImpl(val uploadDb: Nitrite, val parentDir: File = File("uploads")) : UploadsDao {

    val repository by lazy {
        uploadDb.getRepository(Upload::class.java)
    }

    override fun updateStatus(uploadId: String, newStatus: String) {
        val upload = getUpload(uploadId)
        repository.update(upload.copy(status = newStatus))
    }

    override fun getRecentUploads(teacherId: String): List<Upload> {
        println("dao>UploadDaoImpl>getRecentUploads   ")
        return repository.find(FindOptions.sort("timeStamp", SortOrder.Descending)).filter { it.teacherID == teacherId }.take(10).toList()
    }

    override fun hasUpload(uploadId: String): Boolean {
        println("model>UploadDaoImpl>hasUpload   ")
        return repository.find(Upload::id eq uploadId).any()
    }

    override fun getUpload(uploadId: String): Upload {
        return repository.find(Upload::id eq uploadId).first()
    }

    override fun getUploadFile(uploadId: String): File {
        val file = File(parentDir, "$uploadId.pdf")
        file.parentFile.mkdirs()
        return file
    }

    override fun addUpload(teacherId: String, subjectTaught: String, chapterName: String): String {
        val hash = genHash(teacherId, subjectTaught, chapterName)
        val upload = repository.find(Upload::id eq hash).firstOrNull()
                ?: Upload(hash, teacherId, subjectTaught, chapterName, Upload.NOT_RECEIVED).also {
                    repository.insert(it)
                }
        return upload.id
    }

    private fun genHash(teacherId: String, subjectTaught: String, chapterName: String) =
            hash(teacherId, subjectTaught, chapterName)

    override fun getUploads(teacherId: String): List<Upload> {
        return repository.find(Upload::teacherID text teacherId).toList()
    }

}