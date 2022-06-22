package ru.gostev.jaxws.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.long
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import ru.gostev.core.ErrorCodes
import ru.gostev.core.exception.ServiceException
import ru.gostev.jaxws.cli.util.generateHardcodedAuthToken
import ru.gostev.jaxws.cli.util.printError
import ru.gostev.jaxws.cli.util.printUnknownError
import ru.gostev.jaxws.cli.util.stringify
import ru.gostev.jaxws.cli.util.toDate
import ru.gostev.jaxws.model.dto.PersonSoapDto
import ru.gostev.jaxws.service.PersonSoapService

class UpdateCommand constructor(
    private val personSoapService: PersonSoapService,
) : CliktCommand(name = "update", help = "Update an existing person") {

    private val id: Long by option(help = "Person identifier").long().required()

    private val firstName: String? by option(help = "Person's first name")

    private val secondName: String? by option(help = "Person's second name")

    private val birthPlace: String? by option(help = "Birth place")

    private val birthDate: LocalDate? by option(
        help = "Birth date in format yyyy-mm-dd (e.g. \"1984-05-27\")"
    ).convert {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        LocalDate.parse(it, formatter)
    }

    override fun run() {
        val existingPerson: PersonSoapDto = try {
            personSoapService.findById(id)
        } catch (ex: ServiceException) {
            when (ex.code) {
                ErrorCodes.Persons006EntityNotFound -> printError("Entity with id $id was not found")
                ErrorCodes.Persons001UnknownError -> printUnknownError()
                else -> printUnknownError()
            }
        }

        val updatedPerson: PersonSoapDto = try {
            personSoapService.update(
                id = id,
                firstName = this@UpdateCommand.firstName ?: existingPerson.firstName!!,
                secondName = this@UpdateCommand.secondName ?: existingPerson.secondName!!,
                birthPlace = this@UpdateCommand.birthPlace ?: existingPerson.birthPlace!!,
                birthDate = this@UpdateCommand.birthDate?.toDate()
                    ?: existingPerson.birthDate?.toDate()!!,
                authToken = generateHardcodedAuthToken(),
            )
        } catch (ex: ServiceException) {
            when (ex.code) {
                ErrorCodes.Persons002FirstNameIsBlank -> printError("First name cannot be blank")
                ErrorCodes.Persons003SecondNameIsBlank -> printError("Second name cannot be blank")
                ErrorCodes.Persons004BirthPlaceIsBlank -> printError("Birth place cannot be blank")
                ErrorCodes.Persons005BirthDateIsIncorrect -> printError("Birth date is incorrect")
                ErrorCodes.Persons006EntityNotFound -> printError("Entity with id $id was not found")
                ErrorCodes.Persons001UnknownError -> printUnknownError()
                else -> printUnknownError()
            }
        }

        println("Updated person by id $id: ${updatedPerson.stringify()}")
    }
}
