package ru.gostev.rest.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import ru.gostev.core.service.PersonService
import ru.gostev.rest.api.personRoutes

private suspend fun PipelineContext<*, ApplicationCall>.redirectToLinkedIn() =
    call.respondRedirect("https://www.linkedin.com/in/pihanya")

fun Application.configureRouting(personService: PersonService) {
    routing {
        route("/rest") {
            get("/") { redirectToLinkedIn() }
            route("/api/v1") {
                get("/") { redirectToLinkedIn() }
                personRoutes(personService)
            }
        }
    }
}
