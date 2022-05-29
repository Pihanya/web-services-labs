package ru.gostev.rest

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import mu.KLogger
import mu.KotlinLogging
import ru.gostev.core.dao.PersonDao
import ru.gostev.core.dao.impl.StandalonePersonDao
import ru.gostev.core.service.PersonService
import ru.gostev.core.service.impl.PersonServiceImpl
import ru.gostev.rest.plugin.configureHttp
import ru.gostev.rest.plugin.configureMonitoring
import ru.gostev.rest.plugin.configureRouting
import ru.gostev.rest.plugin.configureSerialization

private val logger: KLogger = KotlinLogging.logger {}

fun main() {
    val entityManagerFactory: EntityManagerFactory = Persistence.createEntityManagerFactory("ru.gostev.web")
    val entityManager: EntityManager = entityManagerFactory.createEntityManager()

    val personDao: PersonDao = StandalonePersonDao(entityManager)
    val personService: PersonService = PersonServiceImpl(personDao)

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting(personService)
        configureSerialization()
        configureMonitoring()
        configureHttp()
    }.start(wait = true)

    logger.info { "Server started!" }
}
