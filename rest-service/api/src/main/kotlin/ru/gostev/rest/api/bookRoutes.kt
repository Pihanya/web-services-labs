package ru.gostev.rest.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.util.pipeline.PipelineContext
import java.io.PrintWriter
import java.io.StringWriter
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import ru.gostev.core.ErrorCodes
import ru.gostev.core.exception.ServiceException
import ru.gostev.core.model.dto.PersonSaveDto
import ru.gostev.core.service.PersonService
import ru.gostev.rest.api.kotlin.extension.asErrorResponse
import ru.gostev.rest.api.model.ErrorModel
import ru.gostev.rest.api.model.ErrorResponse
import ru.gostev.rest.api.model.PersonCreationRequest
import ru.gostev.rest.api.model.PersonUpdateRequest

private typealias ApiPersonFilter = ru.gostev.rest.api.model.PersonFilter
private typealias ModelPersonFilter = ru.gostev.core.model.dto.PersonFilter

private typealias ApiPerson = ru.gostev.rest.api.model.Person
private typealias ModelPerson = ru.gostev.core.model.dto.PersonDto

fun Route.personRoutes(personService: PersonService) {

    route("/persons") {

        post {
            runService {
                val request = call.receive<PersonCreationRequest>()
                personService.save(
                    PersonSaveDto(
                        firstName = request.firstName,
                        secondName = request.secondName,
                        birthPlace = request.birthPlace,
                        birthDate = request.birthDate.toJavaLocalDate(),
                    )
                ).let(::toApiPerson)
            }
        }

        get("{id?}") {
            runService {
                val id = call.parameters["id"]?.toLong() ?: return@get call.respondText(
                    "Missing id",
                    status = HttpStatusCode.BadRequest
                )
                personService.findById(id)
                    .let(::toApiPerson)
            }
        }

        post("/find-by-filter") {
            callService(
                fn = {
                    val filter = call.receive<ApiPersonFilter>()
                    personService.findByFilter(
                        ModelPersonFilter(
                            firstName = filter.firstName,
                            secondName = filter.secondName,
                            birthPlace = filter.birthPlace,
                            birthDateFrom = filter.birthDateFrom?.toJavaLocalDate(),
                            birthDateTo = filter.birthDateTo?.toJavaLocalDate(),
                        )
                    ).map(::toApiPerson)
                },
                callback = { foundPersons: List<ApiPerson> ->
                    if (foundPersons.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        call.respond(foundPersons)
                    }
                }
            )
        }

        put("{id?}") {
            runService {
                val id = call.parameters["id"]?.toLong() ?: return@put call.respondText(
                    "Missing id",
                    status = HttpStatusCode.BadRequest
                )
                val request = call.receive<PersonUpdateRequest>()

                personService.save(
                    PersonSaveDto(
                        id = id,
                        firstName = request.firstName,
                        secondName = request.secondName,
                        birthPlace = request.birthPlace,
                        birthDate = request.birthDate.toJavaLocalDate(),
                    )
                ).let(::toApiPerson)
            }
        }

        delete("{id?}") {
            callService(
                fn = {
                    val id = call.parameters["id"]?.toLong() ?: return@delete call.respondText(
                        "Missing id",
                        status = HttpStatusCode.BadRequest
                    )
                    personService.remove(id)
                },
                callback = { removed ->
                    if (removed) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            )
        }
    }
}

private fun toApiPerson(entity: ModelPerson): ApiPerson = ApiPerson(
    id = entity.id,
    firstName = entity.firstName,
    secondName = entity.secondName,
    birthPlace = entity.birthPlace,
    birthDate = entity.birthDate.toKotlinLocalDate(),
)

private suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.runService(
    fn: PipelineContext<Unit, ApplicationCall>.() -> T,
): Unit = callService(
    fn = fn,
    callback = { result -> call.respond(result) }
)

private suspend inline fun <T> PipelineContext<Unit, ApplicationCall>.callService(
    fn: PipelineContext<Unit, ApplicationCall>.() -> T,
    callback: PipelineContext<Unit, ApplicationCall>.(T) -> Unit = {},
): Unit = try {
    callback.invoke(this, fn())
} catch (ex: ServiceException) {
    val errorResponse = ex.asErrorResponse()
    call.respond(
        HttpStatusCode.BadRequest,
        errorResponse
    )
} catch (ex: Exception) {
    call.respond(
        HttpStatusCode.InternalServerError,
        ErrorResponse(
            ErrorModel(
                code = ErrorCodes.Persons001UnknownError.code,
                message = buildString {
                    append(ex.javaClass.canonicalName)
                    if (ex.message != null) {
                        append(": ", ex.message)
                    }
                    val stackTraceWriter = StringWriter().also { sw ->
                        ex.printStackTrace(
                            PrintWriter(sw)
                        )
                    }
                    append(stackTraceWriter.toString())
                }
            )
        )
    )
}
