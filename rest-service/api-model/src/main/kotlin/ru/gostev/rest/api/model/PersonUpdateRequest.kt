package ru.gostev.rest.api.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
class PersonUpdateRequest(

    val firstName: String,

    val secondName: String,

    val birthPlace: String,

    val birthDate: LocalDate,
) {

    override fun toString(): String = buildString {
        append(
            "PersonUpdateRequest(",
            ", firstName='", firstName, '\'',
            ", secondName=", secondName,
            ", birthPlace='", birthPlace, '\'',
            ", birthDate=", birthDate,
            ")"
        )
    }
}
