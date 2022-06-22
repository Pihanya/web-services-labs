package ru.gostev.jaxws.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import ru.gostev.core.ErrorCodes
import ru.gostev.core.exception.ServiceException
import ru.gostev.jaxws.cli.util.generateHardcodedAuthToken
import ru.gostev.jaxws.cli.util.printError
import ru.gostev.jaxws.cli.util.printUnknownError
import ru.gostev.jaxws.cli.util.stringify
import ru.gostev.jaxws.cli.util.toDate
import ru.gostev.jaxws.service.PersonSoapService

class CreateCommand constructor(
    private val personSoapService: PersonSoapService,
) : CliktCommand(name = "create", help = "Create a new person") {

    private val firstName: String by option(help = "Person's first name").required()

    private val secondName: String by option(help = "Person's second name").required()

    private val birthPlace: String by option(help = "Birth place").required()

    private val birthDate: LocalDate by option(help = "Birth date in format yyyy-mm-dd (e.g. \"1984-05-27\")")
        .convert {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            LocalDate.parse(it, formatter)
        }
        .required()

    override fun run() {
        try {
            personSoapService.create(
                firstName = firstName,
                secondName = secondName,
                birthPlace = birthPlace,
                birthDate = birthDate.toDate(),
                authToken = generateHardcodedAuthToken(),
            ).also { person -> println("Created person: ${person.stringify()}") }
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
