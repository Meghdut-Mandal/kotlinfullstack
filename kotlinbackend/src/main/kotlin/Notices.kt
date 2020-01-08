import dao.ViveDao
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route


fun Route.notices(dao: ViveDao, hashFunction: (String) -> String) {
    get<Notices> { notices ->
        val total = dao.getNoticeCount()
        val data = dao.getNotices(notices.start, 10)
        val postResponse=PostResponse(total, total, "200",data)
        call.respond(postResponse)
    }
}
