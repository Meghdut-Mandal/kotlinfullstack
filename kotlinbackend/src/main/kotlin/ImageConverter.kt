import dao.NotesDao
import dao.UploadsDao
import model.Upload
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.concurrent.Executors
import javax.imageio.ImageIO


class ImageConverter(private val uploadsDao: UploadsDao, private val notesDao: NotesDao) {
    private val executor = Executors.newFixedThreadPool(2)

    fun processUpload(uploadId: String) = executor.submit {
        var pdfFile: File? = null

        uploadsDao.updateStatus(uploadId, Upload.RECEIVED)
        try {
            val upload = uploadsDao.getUpload(uploadId)
            pdfFile = uploadsDao.getUploadFile(uploadId)
            val outDir = notesDao.getNotesFolder(upload.subjectTaughtID, upload.chapterName)
            outDir.mkdirs()
            PDDocument.load(pdfFile).use { document ->
                val pdfRenderer = PDFRenderer(document)
                val numberOfPages = document.numberOfPages
                println(">ImageConverter>processUpload  Total files to be converting -> $numberOfPages $upload ")
//             600 dpi give good image clarity but size of each image is 2x times of 300 dpi.
//           Ex:  1. For 300dpi 04-Request-Headers_2.png expected size is 797 KB
//                2. For 600dpi 04-Request-Headers_2.png expected size is 2.42 MB
                val fileExtension = "jpg"

                val dpi = 150
                // use less dpi for to save more space in harddisk. For professional usage you can use more than 300dpi
                val average = (0 until numberOfPages).toList().map { i ->
                    val current = System.nanoTime()
                    val outPutFile =
                            File(outDir, "p$i.$fileExtension")
                    val bImage =
                            pdfRenderer.renderImageWithDPI(i, dpi.toFloat(), ImageType.RGB)
                    outPutFile.outputStream().use { fileOutputStream ->
                        //                    XZOutputStream(fileOutputStream, LZMA2Options(8)).use { xzStream ->
//                    println(">ImageConverter>processUpload  Writing pdffile image $i $uploadId thread ${Thread.currentThread().name} ")
                        ImageIO.write(bImage, fileExtension, fileOutputStream)
                    }
                    System.nanoTime() - current
                }.average()
                println(">ImageConverter>processUpload  Converted $upload AVG nanotime  $average ")
                notesDao.addNote(upload.subjectTaughtID, upload.chapterName, numberOfPages)
                uploadsDao.updateStatus(uploadId, Upload.PROCESSED)
            }
            pdfFile.delete()
        }
        catch (e: Exception) {
            pdfFile?.delete()
            e.printStackTrace()
            println(">ImageConverter>processUpload   ")
            uploadsDao.updateStatus(uploadId, Upload.ERROR)
        }
    }

}