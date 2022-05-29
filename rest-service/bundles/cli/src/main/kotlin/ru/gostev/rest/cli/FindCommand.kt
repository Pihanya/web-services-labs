package ru.gostev.rest.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.datetime.LocalDate
import ru.gostev.core.ErrorCodes
import ru.gostev.core.exception.ServiceException
import ru.gostev.rest.api.model.Person
import ru.gostev.rest.api.model.PersonFilter
import ru.gostev.rest.cli.util.printUnknownError
import ru.gostev.rest.client.PersonClient

class FindCommand constructor(
    private val personClient: PersonClient,
) : CliktCommand(name = "find", help = "Find persons by filter") {

    private val firstName: String? by option(help = "Person's first name")

    private val secondName: String? by option(help = "Person's second name")

    private val birthPlace: String? by option(help = "Birth place")

    private val birthDateFrom: LocalDate? by option(
        help = "Birth date in format yyyy-mm-dd (e.g. \"1984-05-27\")"
    ).convert { LocalDate.parse(it) }

    private val birthDateTo: LocalDate? by option(
        help = "Birth date in format yyyy-mm-dd (e.g. \"2022-04-25\")"
    ).convert { LocalDate.parse(it) }

    override fun run() {
        val foundPeople: List<Person> = try {
            personClient.findByFilter(
                PersonFilter(
                    firstName = firstName,
                    secondName = secondName,
                    birthPlace = birthPlace,
                    birthDateFrom = birthDateFrom,
                    birthDateTo = birthDateTo,
                )
            )
        } catch (ex: ServiceException) {
            when (ex.code) {
                ErrorCodes.Persons001UnknownError -> printUnknownError()
                else -> printUnknownError()
            }
        }
        when {
            foundPeople.isEmpty() -> println("Could not find any person by given filter")
            (foundPeople.size == 1) -> println("Found a single person: ${foundPeople.first()}")
            else -> println("Found persons:\n" + foundPeople.joinToString(separator = "\n", limit = 10))
        }
    }
}
