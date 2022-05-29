package ru.gostev.jaxws.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import ru.gostev.core.ErrorCodes
import ru.gostev.core.exception.ServiceException
import ru.gostev.jaxws.cli.util.printUnknownError
import ru.gostev.jaxws.cli.util.stringify
import ru.gostev.jaxws.cli.util.toDate
import ru.gostev.jaxws.model.dto.PersonSoapDto
import ru.gostev.jaxws.service.PersonSoapService

class FindCommand constructor(
    private val personSoapService: PersonSoapService,
) : CliktCommand(name = "find", help = "Find persons by filter") {

    private val firstName: String? by option(help = "Person's first name")

    private val secondName: String? by option(help = "Person's second name")

    private val birthPlace: String? by option(help = "Birth place")

    private val birthDateFrom: LocalDate? by option(
        help = "Birth date in format yyyy-mm-dd (e.g. \"1984-05-27\")"
    ).convert {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        LocalDate.parse(it, formatter)
    }

    private val birthDateTo: LocalDate? by option(
        help = "Birth date in format yyyy-mm-dd (e.g. \"2022-04-25\")"
    ).convert {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        LocalDate.parse(it, formatter)
    }

    override fun run() {
        val foundPersons: List<PersonSoapDto> = try {
            personSoapService.findByFilter(
                firstName = firstName,
                secondName = secondName,
                birthPlace = birthPlace,
                birthDateFrom = birthDateFrom?.toDate(),
                birthDateTo = birthDateTo?.toDate(),
            )
        } catch (ex: ServiceException) {
            when (ex.code) {
                ErrorCodes.Persons001UnknownError -> printUnknownError()
                else -> printUnknownError()
            }
        }
        when {
            foundPersons.isEmpty() -> println("Could not find any person by given filter")
            (foundPersons.size == 1) -> println("Found a single person: ${foundPersons.first().stringify()}")
            else -> println(
                "Found persons:\n" + foundPersons.joinToString(
                    separator = "\n",
                    limit = 10,
                    transform = PersonSoapDto::stringify
                )
            )
        }
    }
}
