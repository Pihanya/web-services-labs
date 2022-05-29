package ru.gostev.core.service

import ru.gostev.core.model.dto.PersonDto
import ru.gostev.core.model.dto.PersonFilter
import ru.gostev.core.model.dto.PersonSaveDto

interface PersonService {

    fun save(person: PersonSaveDto): PersonDto

    fun findById(id: Long): PersonDto

    fun findByFilter(filter: PersonFilter): List<PersonDto>

    fun remove(id: Long): Boolean
}
