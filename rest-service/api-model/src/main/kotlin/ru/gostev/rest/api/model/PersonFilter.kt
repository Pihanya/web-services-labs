package ru.gostev.rest.api.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
class PersonFilter(

    val firstName: String? = null,

    val secondName: String? = null,

    val birthPlace: String? = null,

    val birthDateFrom: LocalDate? = null,

    val birthDateTo: LocalDate? = null
) {

    override fun toString(): String = buildString {
        append(
            "PersonFilter(",
            "firstName=", firstName,
            ", secondName=", secondName,
            ", birthPlace=", birthPlace,
            ", birthDateFrom=", birthDateFrom,
            ", birthDateTo=", birthDateTo,
            ")"
        )
    }
}
