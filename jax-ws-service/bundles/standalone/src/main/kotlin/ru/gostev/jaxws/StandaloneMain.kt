package ru.gostev.jaxws

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import jakarta.xml.ws.Endpoint
import mu.KLogger
import mu.KotlinLogging
import ru.gostev.core.dao.PersonDao
import ru.gostev.core.dao.impl.StandalonePersonDao
import ru.gostev.core.service.PersonService
import ru.gostev.core.service.impl.PersonServiceImpl
import ru.gostev.jaxws.service.PersonSoapService
import ru.gostev.jaxws.service.impl.PersonSoapServiceImpl

private val logger: KLogger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    System.setProperty(
        "com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace",
        "false"
    )

    val entityManagerFactory: EntityManagerFactory = Persistence.createEntityManagerFactory("ru.gostev.web")
    val entityManager: EntityManager = entityManagerFactory.createEntityManager()

    val personDao: PersonDao = StandalonePersonDao(entityManager)
    val personService: PersonService = PersonServiceImpl(personDao)
    val personSoapService: PersonSoapService = PersonSoapServiceImpl(personService)

    val readServiceUrl = "http://127.0.0.1:8080/jaxws/PersonService"
    Endpoint.publish(readServiceUrl, personSoapService)
    logger.info { "Server started!" }
}
