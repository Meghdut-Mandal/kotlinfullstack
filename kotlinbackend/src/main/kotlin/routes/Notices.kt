package routes

import Notices
import Post
import PostMakeResponse
import PostResponse
import dao.ViveDao
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route


fun Route.notices(dao: ViveDao, hashFunction: (String) -> String) {
    get<Notices> { notices ->
        val total = dao.getNoticeCount()
        val data = dao.getNotices(notices.start, 10)
        val postResponse = PostResponse(total, total, "200", data)
        call.respond(postResponse)
    }

    post<Notices> {
        val receive = call.receive<Post>()
        val id = dao.insertNotice(receive)
        call.respond(PostMakeResponse(id, "success"))
    }
}
