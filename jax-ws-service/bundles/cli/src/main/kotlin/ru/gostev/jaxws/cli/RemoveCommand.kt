package ru.gostev.jaxws.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.long
import ru.gostev.core.ErrorCodes
import ru.gostev.core.exception.ServiceException
import ru.gostev.jaxws.cli.util.generateHardcodedAuthToken
import ru.gostev.jaxws.cli.util.printUnknownError
import ru.gostev.jaxws.service.PersonSoapService

class RemoveCommand constructor(
    private val personSoapService: PersonSoapService,
) : CliktCommand(name = "remove", help = "Remove an existing person") {

    private val id: Long by option(help = "Person identifier").long().required()

    override fun run() {
        val deleted: Boolean = try {
            personSoapService.deleteById(
                id = id,
                authToken = generateHardcodedAuthToken(),
            )
        } catch (ex: ServiceException) {
            when (ex.code) {
                ErrorCodes.Persons001UnknownError -> printUnknownError()
                else -> printUnknownError()
            }
        }
        if (deleted) {
            println("Person with id $id has been removed")
        } else {
            println("Person with id $id was not removed")
        }
    }
}
