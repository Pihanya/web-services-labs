package ru.gostev.jaxws.cli.util

import ru.gostev.jaxws.model.dto.PersonSoapDto

fun PersonSoapDto.stringify() = buildString {
    append("Person(")
    append("id=", id, ", ")
    append("firstName=", firstName, ", ")
    append("secondName=", secondName, ", ")
    append("birthPlace=", birthPlace, ", ")
    append("birthDate=", birthDate)
    append(")")
}
