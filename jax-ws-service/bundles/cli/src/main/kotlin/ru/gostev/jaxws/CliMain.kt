package ru.gostev.jaxws

import com.github.ajalt.clikt.core.subcommands
import java.net.URL
import ru.gostev.jaxws.cli.PersonsCli
import ru.gostev.jaxws.cli.CreateCommand
import ru.gostev.jaxws.cli.FindCommand
import ru.gostev.jaxws.cli.RemoveCommand
import ru.gostev.jaxws.cli.UpdateCommand
import ru.gostev.jaxws.client.PersonService_Service
import ru.gostev.jaxws.client.PersonSoapServiceDelegate
import ru.gostev.jaxws.service.PersonSoapService

private const val WSDL_URL_ENV_VARIABLE: String = "WSDL_URL"

/**
 * Link to standalone WSDL URL
 */
private const val STANDALONE_WSDL_URL: String = "http://localhost:8080/jaxws/PersonService?wsdl"

private fun resolveWsdlUrl(): URL =
    System.getenv()
        .getOrDefault(key = WSDL_URL_ENV_VARIABLE, defaultValue = STANDALONE_WSDL_URL)
        .let(::URL)

fun main(args: Array<String>) {
    val personService = PersonService_Service(resolveWsdlUrl())
    val personServicePort = personService.personServicePort
    val personSoapService: PersonSoapService = PersonSoapServiceDelegate(personServicePort)

    PersonsCli()
        .subcommands(
            CreateCommand(personSoapService),
            FindCommand(personSoapService),
            UpdateCommand(personSoapService),
            RemoveCommand(personSoapService),
        )
        .main(args)
}
