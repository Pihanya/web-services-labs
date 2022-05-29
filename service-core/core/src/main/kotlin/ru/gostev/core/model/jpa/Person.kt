package ru.gostev.core.model.jpa

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.LocalDate
import ru.gostev.core.model.jpa.Person.Companion.TABLE_NAME

@Entity
@Table(name = TABLE_NAME)
open class Person {

    @get:Id
    @get:GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "${TABLE_NAME}_seq")
    @get:SequenceGenerator(name = "${TABLE_NAME}_seq", sequenceName = "seq_$TABLE_NAME", allocationSize = 1)
    @get:Column(name = "id", nullable = false)
    open var id: Long? = null

    @get:Column(name = "first_name", nullable = false)
    open var firstName: String? = null

    @get:Column(name = "second_name", nullable = false)
    open var secondName: String? = null

    @get:Column(name = "birth_place", nullable = false)
    open var birthPlace: String? = null

    @get:Column(name = "birth_date", nullable = false)
    open var birthDate: LocalDate? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Person

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = if (id != null) id.hashCode() else 0

    override fun toString(): String = "Person(id=$id)"

    companion object {

        const val TABLE_NAME: String = "person"

        const val ID: String = "id"

        const val FIRST_NAME: String = "firstName"

        const val SECOND_NAME: String = "secondName"

        const val BIRTH_PLACE: String = "birthPlace"

        const val BIRTH_DATE: String = "birthDate"
    }
}
