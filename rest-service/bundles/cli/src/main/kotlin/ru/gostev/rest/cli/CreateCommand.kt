package ru.gostev.rest.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import kotlinx.datetime.LocalDate
import ru.gostev.core.ErrorCodes
import ru.gostev.core.exception.ServiceException
import ru.gostev.rest.api.model.PersonCreationRequest
import ru.gostev.rest.cli.util.printError
import ru.gostev.rest.cli.util.printUnknownError
import ru.gostev.rest.client.PersonClient

class CreateCommand constructor(
    private val personClient: PersonClient,
) : CliktCommand(name = "create", help = "Create a new person") {

    private val firstName: String by option(help = "Person's first name").required()

    private val secondName: String by option(help = "Person's second name").required()

    private val birthPlace: String by option(help = "Birth place").required()

    private val birthDate: LocalDate by option(
        help = "Birth date in format yyyy-mm-dd (e.g. \"1984-05-27\")"
    ).convert { LocalDate.parse(it) }.required()

    override fun run() {
        try {
            personClient.create(
                PersonCreationRequest(
                    firstName = firstName,
                    secondName = secondName,
                    birthPlace = birthPlace,
                    birthDate = birthDate,
                )
            ).also { person -> println("Created person: $person") }
        } catch (ex: ServiceException) {
            when (ex.code) {
                ErrorCodes.Persons002FirstNameIsBlank -> printError("First name cannot be blank")
                ErrorCodes.Persons003SecondNameIsBlank -> printError("Second name cannot be blank")
                ErrorCodes.Persons004BirthPlaceIsBlank -> printError("Birth place cannot be blank")
                ErrorCodes.Persons005BirthDateIsIncorrect -> printError("Birth date is incorrect")
                ErrorCodes.Persons001UnknownError -> printUnknownError()
                else -> printUnknownError()
            }
        }
    }
}
