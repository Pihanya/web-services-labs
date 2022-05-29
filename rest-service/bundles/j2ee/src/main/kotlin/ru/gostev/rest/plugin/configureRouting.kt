@file:JvmMultifileClass
@file:JvmName("KtorPlugins")

package ru.gostev.rest.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import io.ktor.util.pipeline.PipelineInterceptor
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import ru.gostev.core.dao.impl.CdiPersonDao
import ru.gostev.core.service.impl.PersonServiceImpl
import ru.gostev.rest.api.personRoutes

val redirectToLinkedIn = object : PipelineInterceptor<Unit, ApplicationCall> {
    override suspend fun invoke(ctx: PipelineContext<Unit, ApplicationCall>, p2: Unit) = with(ctx) {
        call.respondRedirect("https://www.linkedin.com/in/pihanya")
    }
}

@Suppress("unused")
fun Application.configureRouting() {
    routing {
        get("/", redirectToLinkedIn)
        route("/api/v1") {
            get("/", redirectToLinkedIn)
            personRoutes(
                run {
                    val entityManagerFactory: EntityManagerFactory =
                        Persistence.createEntityManagerFactory("ru.gostev.web")
                    val entityManager: EntityManager = entityManagerFactory.createEntityManager()

                    val personDao = CdiPersonDao().apply {
                        this.entityManager = entityManager
                    }
                    PersonServiceImpl(personDao)
                }
            )
        }
    }
}
