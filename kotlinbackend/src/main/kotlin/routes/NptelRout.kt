package routes

import NptelData
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import org.dizitart.kno2.nitrite
import org.dizitart.no2.objects.Id
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File
import kotlin.streams.toList

val Element.href: String
    get() {
        var attr = attr("href")
        if (attr.contains("#")) {
            val l = attr.indexOf("#")
            attr = attr.substring(0, l)
        }
        return attr
    }

fun String?.parseHtml() = Jsoup.parse(this)

class Nptel {


    data class Course(
            val name: String, @Id val url: String,
            val dicipline: String,
            val prof: String,
            val institute: String,
            val lectureList: MutableList<Lecture> = arrayListOf(),
            val bookMarks: MutableList<BookMark> = arrayListOf()
    )

    data class BookMark(val name: String, val startIndex: Int)
    data class Lecture(
            val topicName: String,
            val lectureName: String,
            val lectureNo: Int,
            val youtubeId: String,
            val nptelVideo: String
    )

    private val npteldb by lazy { nitrite { file = File("data/nptel.db") } }

    fun fetchAllCourcesData() {
        val repository = npteldb.getRepository(Course::class.java)

        val file = File("nptel.html")
        val document = file.readText().parseHtml()
        document.select("tr").parallelStream().forEach { element ->
            try {
                val elements = element.select("td")
                val name = elements[0].text()
                val ref = "https://nptel.ac.in/" + elements[0].select("a").first().href
                val dicipline = elements[1].text()
                val prof = elements[2].text()
                val instiute = elements[4].text()
                val lectureList = loadCourse(ref)
                val bookMarks = lectureList.groupBy { it.topicName }.map {
                    BookMark(it.key, it.value.map { it.lectureNo }.min() ?: 0)
                }.toMutableList()
                repository.insert(Course(name, ref, dicipline, prof, instiute, lectureList, bookMarks))
                println("  $name = $ref = $dicipline = $prof =$instiute  ")
            }
            catch (e: Exception) {
            }
        }
    }


    fun getData(): MutableList<Course> {
        val repository = npteldb.getRepository(Course::class.java)
        if (repository.find().count() == 0)
            fetchAllCourcesData()
        return repository.find().toList()
    }

    fun String.getDoc(): Document {

        return Jsoup.connect(this).execute().parse()
    }

    private fun loadCourse(url: String): MutableList<Lecture> {
        val doc = url.getDoc()
        doc.select("#ul_nav").first()
        return doc.select("div.course_lectures_list").select("a[href=#]").flatMap { topicHeader ->
            val topicName = topicHeader.text()
            topicHeader.parent().select("li.first").first().select("a[onclick]").map { select ->
                val lectureName = select.text()
                val videodetails =
                        select.attr("onclick").substringAfter("(").replace(")", "").split(",")

                val lectN0 = videodetails[0].toInt()
                var youtubeId = videodetails[1].substringAfter("'").substringBefore("'")
                if (!youtubeId.endsWith("pdf"))
                    youtubeId = "https://www.youtube.com/embed/$youtubeId"

                var contentUrl =
                        if (videodetails.size == 3) videodetails[2].substringAfter("'").substringBefore("'") else "NONE"
                if (!contentUrl.contains("NONE"))
                    contentUrl = "https://nptel.ac.in$contentUrl"

                Lecture(topicName, lectureName, lectN0, youtubeId, contentUrl)
            }
        }.toMutableList()
    }
}

val nptel = Nptel()

fun Route.nptel() {

    get<NptelData> {
        val toList = nptel.getData().stream().skip(it.skip).limit(it.limit).toList()
        call.respond(toList)
    }
}