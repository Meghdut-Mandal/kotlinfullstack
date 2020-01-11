import io.ktor.application.call
import io.ktor.http.content.files
import io.ktor.http.content.resolveResource
import io.ktor.http.content.static
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route

@Location("/styles/main.css")
class MainCss()

/**
 * Register the styles, [MainCss] route (/styles/main.css)
 */
fun Route.styles() {
    /**
     * On a GET request to the [MainCss] route, it returns the `blog.css` file from the resources.
     *
     * Here we could preprocess or join several CSS/SASS/LESS.
     */
    get<MainCss> {
        call.respond(call.resolveResource("blog.css")!!)
    }
}