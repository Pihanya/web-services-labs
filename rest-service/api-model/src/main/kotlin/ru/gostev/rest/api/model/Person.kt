package ru.gostev.rest.api.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
class Person(

    val id: Long,

    val firstName: String,

    val secondName: String,

    val birthPlace: String,

    val birthDate: LocalDate,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Person

        if (id != other.id) return false
        if (firstName != other.firstName) return false
        if (secondName != other.secondName) return false
        if (birthPlace != other.birthPlace) return false
        if (birthDate != other.birthDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + secondName.hashCode()
        result = 31 * result + birthPlace.hashCode()
        result = 31 * result + birthDate.hashCode()
        return result
    }

    override fun toString(): String = buildString {
        append(
            "Person(",
            "id=", id,
            ", firstName=", firstName,
            ", secondName=", secondName,
            ", birthPlace=", birthPlace,
            ", birthDate=", birthDate,
            ")"
        )
    }
}
