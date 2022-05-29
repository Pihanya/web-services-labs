package ru.gostev.core.dao

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import ru.gostev.core.model.jpa.Person

interface PersonDao {

    fun save(entity: Person): Person

    fun findById(id: Long): Person?

    fun findByCondition(
        params: Map<String, Any>,
        conditions: (CriteriaBuilder, CriteriaQuery<Person>, Root<Person>) -> Predicate,
    ): List<Person>

    fun remove(id: Long): Boolean
}
