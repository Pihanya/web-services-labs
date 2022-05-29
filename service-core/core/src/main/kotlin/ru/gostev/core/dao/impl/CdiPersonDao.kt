package ru.gostev.core.dao.impl

import jakarta.inject.Singleton
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaDelete
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import ru.gostev.core.dao.PersonDao
import ru.gostev.core.model.jpa.Person

@Singleton
class CdiPersonDao : PersonDao {

    @PersistenceContext(unitName = "ru.gostev.web")
    lateinit var entityManager: EntityManager

    @Transactional
    override fun save(entity: Person): Person = entityManager.merge(entity)

    @Transactional
    override fun findById(id: Long): Person? = entityManager.find(Person::class.java, id)

    @Transactional
    override fun findByCondition(
        params: Map<String, Any>,
        conditions: (CriteriaBuilder, CriteriaQuery<Person>, Root<Person>) -> Predicate,
    ): List<Person> {
        val cb: CriteriaBuilder = entityManager.criteriaBuilder
        val cq: CriteriaQuery<Person> = cb.createQuery(Person::class.java)
        val root: Root<Person> = cq.from(Person::class.java)
        cq.where(conditions(cb, cq, root))

        val query: TypedQuery<Person> = entityManager.createQuery(cq)
        params.forEach { (key, value) -> query.setParameter(key, value) }

        return query.resultList
    }

    @Transactional
    override fun remove(id: Long): Boolean {
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

        return updated > 0
    }
}
