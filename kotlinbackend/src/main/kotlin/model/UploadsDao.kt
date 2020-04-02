package model

import gson
import org.dizitart.kno2.filters.eq
import org.dizitart.no2.Nitrite
import java.io.File

interface UploadsDao {

    /*
    Get the path of the file stored in the temp dir
     */
    fun getUploadFile(uploadId: String): File

    /*
    Will return the id of the upload from the database
     */
    fun addUpload(teacherId: String, subjectTaught: SubjectTaught, chapterName: String): String

    /*
     Get the uploads of the given teacher
     */
    fun getUploads(teacherId: String): List<Upload>

}

class UploadDaoImpl(val uploadDb: Nitrite, val hashFunc: (String) -> String, val parentDir: File = File("uploads")) : UploadsDao {

    val repository by lazy {
        uploadDb.getRepository(Upload::class.java)
    }

    override fun getUploadFile(uploadId: String): File {
        val file = File(parentDir, "$uploadId.pdf")
        file.parentFile.mkdirs()
        return file
    }

    override fun addUpload(teacherId: String, subjectTaught: SubjectTaught, chapterName: String): String {
        val hash = hashFunc(teacherId + gson.toJson(subjectTaught) + chapterName)
        val upload = repository.find(Upload::id eq hash).firstOrNull()
                ?: Upload(hash, teacherId, subjectTaught, chapterName, Upload.NOT_RECEIVED).also {
                    repository.insert(it)
                }
        return upload.id
    }

    override fun getUploads(teacherId: String): List<Upload> {
        return repository.find(Upload::teacherID eq teacherId).toList()
    }

}