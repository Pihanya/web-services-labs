package ru.gostev.rest

import jakarta.enterprise.inject.spi.BeanManager
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlin.properties.Delegates
import ru.gostev.core.service.PersonService

@Singleton
class Beans @Inject constructor(
    beanManager: BeanManager,
    personService: PersonService,
) {

    init {
        BEAN_MANAGER = beanManager
        PERSON_SERVICE = personService
    }

    companion object {

        var BEAN_MANAGER: BeanManager by Delegates.notNull()
            private set

        var PERSON_SERVICE: PersonService by Delegates.notNull()
            private set
    }
}
