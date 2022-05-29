package ru.gostev.rest.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.long
import ru.gostev.core.ErrorCodes
import ru.gostev.core.exception.ServiceException
import ru.gostev.rest.cli.util.printUnknownError
import ru.gostev.rest.client.PersonClient

class RemoveCommand constructor(
    private val personClient: PersonClient,
) : CliktCommand(name = "remove", help = "Remove an existing person") {

    private val id: Long by option(help = "Person identifier").long().required()

    override fun run() {
        val deleted: Boolean = try {
            personClient.deleteById(id = id)
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
