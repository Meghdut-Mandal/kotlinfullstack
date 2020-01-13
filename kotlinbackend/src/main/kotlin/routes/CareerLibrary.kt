package routes

import CarrierLib
import dao.ViveDao
import io.ktor.application.call
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.thymeleaf.ThymeleafContent

val lib =
        arrayListOf("accessory.html", "actuarial_science.html", "advertising.html", "allied_medicine.html", "animation.html",
                "applied arts.html", "architecture.html", "audiologist.html", "aviation.html", "cabin crew.html", "carrer.html",
                "cartoonist.html", "civil_services.html", "computer_application_and_it.html", "design.html", "distribution_and_logistics.html",
                "economists.html", "education and training.html", "entrepreneurship.html", "ethical_hacking.html", "optometrists.html")
data class Carrer(val location:String,val name:String)
fun Route.carrerLibrary(dao: ViveDao, hashFunction: (String) -> String) {
    get<CarrierLib> {
        val map= lib.map { Carrer("/carreir_lib/$it" , it.replace("html", "")) }
        call.respond(ThymeleafContent("carreers", mapOf("list" to map)))

    }
}
