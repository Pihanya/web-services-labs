package ru.gostev.core.dao.impl

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityTransaction
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaDelete
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import ru.gostev.core.dao.PersonDao
import ru.gostev.core.model.jpa.Person

class StandalonePersonDao constructor(
    private val entityManager: EntityManager,
) : PersonDao {

    override fun save(entity: Person): Person = inTransaction { entityManager.merge(entity) }

    override fun findById(id: Long): Person? = inTransaction { entityManager.find(Person::class.java, id) }

    override fun findByCondition(
        params: Map<String, Any>,
        conditions: (CriteriaBuilder, CriteriaQuery<Person>, Root<Person>) -> Predicate,
    ): List<Person> = inTransaction {
        val cb: CriteriaBuilder = entityManager.criteriaBuilder
        val cq: CriteriaQuery<Person> = cb.createQuery(Person::class.java)
        val root: Root<Person> = cq.from(Person::class.java)
        cq.where(conditions(cb, cq, root))

        val query: TypedQuery<Person> = entityManager.createQuery(cq)
        params.forEach { (key, value) -> query.setParameter(key, value) }

        return@inTransaction query.resultList
    }

    override fun remove(id: Long): Boolean = inTransaction {
        val cb = entityManager.criteriaBuilder
        val cd: CriteriaDelete<Person> = cb.createCriteriaDelete(Person::class.java)
        val root: Root<Person> = cd.from(Person::class.java)
        cd.where(
            cb.equal(
                root.get<Long>(Person.ID),
                cb.parameter(Long::class.java, Person.ID)
            )
        )

        val updated: Int = entityManager
            .createQuery(cd)
            .setParameter(Person.ID, id)
            .executeUpdate()

        return@inTransaction updated > 0
    }

    private inline fun <T> inTransaction(crossinline closure: () -> T): T {
        val transaction: EntityTransaction = entityManager.transaction

        transaction.begin()
        val result: T = try {
            closure()
        } catch (ex: Exception) {
            transaction.rollback()
            throw ex
        }
        transaction.commit()

        return result
    }
}
