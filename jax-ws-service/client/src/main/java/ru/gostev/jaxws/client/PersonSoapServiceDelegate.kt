package ru.gostev.jaxws.client

import com.sun.xml.ws.fault.ServerSOAPFaultException
import java.time.LocalDate
import java.util.Date
import ru.gostev.core.ErrorCodeRepository
import ru.gostev.core.exception.ServiceException
import ru.gostev.jaxws.service.PersonSoapService

private typealias JaxWsPersonSoapDto = ru.gostev.jaxws.client.PersonSoapDto
private typealias ModelPersonSoapDto = ru.gostev.jaxws.model.dto.PersonSoapDto

class PersonSoapServiceDelegate(
    private val personService: PersonService,
) : PersonSoapService {

    override fun create(
        firstName: String,
        secondName: String,
        birthPlace: String,
        birthDate: Date,
        authToken: String,
    ): ModelPersonSoapDto = interceptException {
        personService.create(
            firstName,
            secondName,
            birthPlace,
            birthDate.toGregorianCalendar(),
            authToken,
        ).let(::toModelDto)
    }

    override fun findById(id: Long): ModelPersonSoapDto = interceptException {
        personService.findById(id).let(::toModelDto)
    }

    override fun findByFilter(
        firstName: String?,
        secondName: String?,
        birthPlace: String?,
        birthDateFrom: Date?,
        birthDateTo: Date?,
    ): List<ModelPersonSoapDto> = interceptException {
        personService.findByFilter(
            firstName,
            secondName,
            birthPlace,
            birthDateFrom?.toGregorianCalendar(),
            birthDateTo?.toGregorianCalendar(),
        ).map(::toModelDto)
    }

    override fun update(
        id: Long,
        firstName: String,
        secondName: String,
        birthPlace: String,
        birthDate: Date,
        authToken: String,
    ): ModelPersonSoapDto = interceptException {
        personService.update(
            id,
            firstName,
            secondName,
            birthPlace,
            birthDate.toGregorianCalendar(),
            authToken,
        ).let(::toModelDto)
    }

    override fun deleteById(id: Long, authToken: String): Boolean = interceptException {
        personService.deleteById(id, authToken)
    }

    private fun toModelDto(entity: JaxWsPersonSoapDto): ModelPersonSoapDto = ModelPersonSoapDto().apply {
        this.id = entity.id
        this.firstName = entity.firstName
        this.secondName = entity.secondName
        this.birthPlace = entity.birthPlace
        this.birthDate = entity.birthDate.run { LocalDate.of(year, month, day) }
    }

    private fun <T> interceptException(fn: () -> T): T =
        try {
            fn()
        } catch (ex: ServerSOAPFaultException) {
            val faultString = ex.fault.faultString ?: throw ex

            val prefix = ServiceException::class.java.canonicalName + "::"
            if (!faultString.startsWith(prefix)) throw ex

            val errorCodeStr = faultString.removePrefix(prefix)
            val errorCode = ErrorCodeRepository.findByCode(errorCodeStr) ?: throw ex
            throw ServiceException(errorCode)
        }
}
