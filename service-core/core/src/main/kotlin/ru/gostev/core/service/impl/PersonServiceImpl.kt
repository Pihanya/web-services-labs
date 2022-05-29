package ru.gostev.core.service.impl

import jakarta.inject.Inject
import jakarta.inject.Singleton
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import java.time.LocalDate
import mu.KLogger
import mu.toKLogger
import org.slf4j.LoggerFactory
import ru.gostev.core.ErrorCodes
import ru.gostev.core.dao.PersonDao
import ru.gostev.core.exception.ServiceException
import ru.gostev.core.model.dto.PersonDto
import ru.gostev.core.model.dto.PersonFilter
import ru.gostev.core.model.dto.PersonSaveDto
import ru.gostev.core.model.jpa.Person
import ru.gostev.core.service.PersonService

@Singleton
class PersonServiceImpl @Inject constructor(
    private val personDao: PersonDao,
) : PersonService {

    private val logger: KLogger = LoggerFactory.getLogger(this::class.java).toKLogger()

    override fun save(person: PersonSaveDto): PersonDto {
        logger.entry(person)

        if (person.firstName.isBlank()) {
            throw ServiceException(ErrorCodes.Persons002FirstNameIsBlank).also(logger::throwing)
        }
        if (person.secondName.isBlank()) {
            throw ServiceException(ErrorCodes.Persons003SecondNameIsBlank).also(logger::throwing)
        }
        if (person.birthPlace.isBlank()) {
            throw ServiceException(ErrorCodes.Persons004BirthPlaceIsBlank).also(logger::throwing)
        }
        if (person.birthDate < MIN_BIRTH_DATE) {
            throw ServiceException(ErrorCodes.Persons005BirthDateIsIncorrect).also(logger::throwing)
        }

        val entity = Person().apply {
            this.id = person.id
            this.firstName = person.firstName
            this.secondName = person.secondName
            this.birthPlace = person.birthPlace
            this.birthDate = person.birthDate
        }
        return withDao { save(entity) }
            .let(::toPersonDto)
            .also {
                logger.info { "Created [$it]" }
                logger.exit(it)
            }
    }

    override fun findById(id: Long): PersonDto {
        logger.entry(id)
        val entity: Person = withDao { findById(id) } ?: run {
            throw ServiceException(ErrorCodes.Persons006EntityNotFound)
                .also(logger::throwing)
        }
        return entity.let(::toPersonDto).also(logger::exit)
    }

    override fun findByFilter(filter: PersonFilter): List<PersonDto> {
        logger.entry(filter)
        val params = toParamsMap(filter)
        val entityList = withDao { findByCondition(params, createPredicateBuilder(params)) }
        return entityList.map(::toPersonDto)
            .also(logger::exit)
    }

    override fun remove(id: Long): Boolean = withDao { remove(id) }
        .also { removed ->
            if (removed) {
                logger.info { "Removed Person(id=$id)" }
            }
        }

    private fun createPredicateBuilder(
        params: Map<String, Any>,
    ): (CriteriaBuilder, CriteriaQuery<Person>, Root<Person>) -> Predicate = { cb, cq, root ->
        val predicates = buildList<Predicate> {
            if (Params.FIRST_NAME_LIKE in params) add(
                cb.like(
                    cb.lower(root[Person.FIRST_NAME]),
                    cb.parameter(String::class.java, Params.FIRST_NAME_LIKE)
                )
            )
            if (Params.SECOND_NAME_LIKE in params) add(
                cb.like(
                    cb.lower(root[Person.SECOND_NAME]),
                    cb.parameter(String::class.java, Params.SECOND_NAME_LIKE)
                )
            )
            if (Params.BIRTH_PLACE_LIKE in params) add(
                cb.like(
                    cb.lower(root[Person.BIRTH_PLACE]),
                    cb.parameter(String::class.java, Params.BIRTH_PLACE_LIKE)
                )
            )
            if (Params.BIRTH_DATE_FROM in params) add(
                cb.greaterThanOrEqualTo(
                    root[Person.BIRTH_DATE],
                    cb.parameter(LocalDate::class.java, Params.BIRTH_DATE_FROM)
                )
            )
            if (Params.BIRTH_DATE_TO in params) add(
                cb.lessThanOrEqualTo(
                    root[Person.BIRTH_DATE],
                    cb.parameter(LocalDate::class.java, Params.BIRTH_DATE_TO)
                )
            )
        }
        cb.and(*predicates.toTypedArray())
    }

    private fun toPersonDto(entity: Person): PersonDto = PersonDto(
        id = entity.id!!,
        firstName = entity.firstName!!,
        secondName = entity.secondName!!,
        birthPlace = entity.birthPlace!!,
        birthDate = entity.birthDate!!,
    )

    private fun toParamsMap(filter: PersonFilter): Map<String, Any> = buildMap {
        filter.firstName?.let { put(Params.FIRST_NAME_LIKE, '%' + it.lowercase() + '%') }
        filter.secondName?.let { put(Params.SECOND_NAME_LIKE, '%' + it.lowercase() + '%') }
        filter.birthPlace?.let { put(Params.BIRTH_PLACE_LIKE, '%' + it.lowercase() + '%') }
        filter.birthDateFrom?.let { put(Params.BIRTH_DATE_FROM, it) }
        filter.birthDateTo?.let { put(Params.BIRTH_DATE_TO, it) }
    }

    private fun <T> withDao(closure: PersonDao.() -> T): T =
        try {
            closure(personDao)
        } catch (ex: Exception) {
            throw ServiceException(ErrorCodes.Persons001UnknownError, ex)
        }

    private companion object {

        private val MIN_BIRTH_DATE: LocalDate = LocalDate.of(1900, 1, 1)

        private object Params {

            const val FIRST_NAME_LIKE: String = "firstNameLike"

            const val SECOND_NAME_LIKE: String = "secondNameLike"

            const val BIRTH_PLACE_LIKE: String = "birthPlaceLike"

            const val BIRTH_DATE_FROM: String = "birthDateFrom"

            const val BIRTH_DATE_TO: String = "birthDateTo"
        }
    }
}
