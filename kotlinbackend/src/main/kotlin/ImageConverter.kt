import dao.NotesDao
import model.Upload
import model.UploadsDao
import model.notes.NotePage
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO


class ImageConverter(private val uploadsDao: UploadsDao, private val notesDao: NotesDao) {


    fun processUpload(uploadId: String) {
        try {
            val upload = uploadsDao.getUpload(uploadId)
            val pdfFile = uploadsDao.getUploadFile(uploadId)
            val outDir = notesDao.getNotesFolder(upload.subjectTaughtID, upload.chapterName)
            outDir.mkdirs()

            val document: PDDocument = PDDocument.load(pdfFile)
            val pdfRenderer = PDFRenderer(document)

            val numberOfPages = document.numberOfPages
            println(">ImageConverter>processUpload  Total files to be converting -> $numberOfPages ")
//             600 dpi give good image clarity but size of each image is 2x times of 300 dpi.
//           Ex:  1. For 300dpi 04-Request-Headers_2.png expected size is 797 KB
//                2. For 600dpi 04-Request-Headers_2.png expected size is 2.42 MB
            val fileExtension = "jpg"

            val dpi = 150
            // use less dpi for to save more space in harddisk. For professional usage you can use more than 300dpi
            val pageList = arrayListOf<NotePage>()
            for (i in 0 until numberOfPages) {
                val outPutFile =
                        File(outDir, "p$i.$fileExtension")
                val bImage =
                        pdfRenderer.renderImageWithDPI(i, dpi.toFloat(), ImageType.RGB)
                outPutFile.outputStream().use { fileOutputStream: FileOutputStream ->
                    //                    XZOutputStream(fileOutputStream, LZMA2Options(8)).use { xzStream ->
                    ImageIO.write(bImage, fileExtension, fileOutputStream)
                    pageList.add(NotePage(i, "pg$i", "/notes/${outDir.name}/${outPutFile.name}"))
//                    }
                }

            }
            document.close()
            println(">ImageConverter>processUpload  Converted Images are saved at -> " + "${outDir.absolutePath} ")
            notesDao.addNote(upload.subjectTaughtID, upload.chapterName, pageList)
            uploadsDao.updateStatus(uploadId, Upload.PROCESSED)
        }
        catch (e: Exception) {
            e.printStackTrace()
            uploadsDao.updateStatus(uploadId, Upload.ERROR)
        }
    }

}