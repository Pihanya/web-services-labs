package ru.gostev.rest.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.datetime.LocalDate
import ru.gostev.core.ErrorCodes
import ru.gostev.core.exception.ServiceException
import ru.gostev.rest.api.model.Person
import ru.gostev.rest.api.model.PersonUpdateRequest
import ru.gostev.rest.cli.util.printError
import ru.gostev.rest.cli.util.printUnknownError
import ru.gostev.rest.client.PersonClient

class UpdateCommand constructor(
    private val personClient: PersonClient,
) : CliktCommand(name = "update", help = "Update an existing person") {

    private val id: Long by option(help = "Person identifier").long().required()

    private val firstName: String? by option(help = "Person's first name")

    private val secondName: String? by option(help = "Person's second name")

    private val birthPlace: String? by option(help = "Birth place")

    private val birthDate: LocalDate? by option(
        help = "Birth date in format yyyy-mm-dd (e.g. \"1984-05-27\")"
    ).convert { LocalDate.parse(it) }

    override fun run() {
        val existingPerson: Person = try {
            personClient.findById(id)
        } catch (ex: ServiceException) {
            when (ex.code) {
                ErrorCodes.Persons006EntityNotFound -> printError("Entity with id $id was not found")
                ErrorCodes.Persons001UnknownError -> printUnknownError()
                else -> printUnknownError()
            }
        }

        val updatedPerson: Person = try {
            personClient.update(
                id = id,
                PersonUpdateRequest(
                    firstName = this@UpdateCommand.firstName ?: existingPerson.firstName,
                    secondName = this@UpdateCommand.secondName ?: existingPerson.secondName,
                    birthPlace = this@UpdateCommand.birthPlace ?: existingPerson.birthPlace,
                    birthDate = this@UpdateCommand.birthDate ?: existingPerson.birthDate,
                )
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

        println("Updated person by id $id: $updatedPerson")
    }
}
