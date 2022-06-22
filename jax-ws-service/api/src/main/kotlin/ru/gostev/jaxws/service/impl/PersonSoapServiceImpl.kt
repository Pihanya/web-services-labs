package ru.gostev.jaxws.service.impl

import jakarta.inject.Inject
import jakarta.jws.WebService
import jakarta.xml.soap.SOAPFactory
import jakarta.xml.soap.SOAPFault
import jakarta.xml.ws.soap.SOAPFaultException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date
import javax.xml.namespace.QName
import ru.gostev.core.exception.ServiceException
import ru.gostev.core.model.dto.PersonDto
import ru.gostev.core.model.dto.PersonFilter
import ru.gostev.core.model.dto.PersonSaveDto
import ru.gostev.core.service.PersonService
import ru.gostev.jaxws.model.dto.PersonSoapDto
import ru.gostev.jaxws.service.PersonSoapService

@WebService(name = "PersonService", serviceName = "PersonService")
class PersonSoapServiceImpl @Inject constructor(
    private val personService: PersonService,
) : PersonSoapService {

    override fun uploadBinaryData(content: ByteArray) {
        println(content)
    }

    override fun create(
        firstName: String,
        secondName: String,
        birthPlace: String,
        birthDate: Date,
    ): PersonSoapDto = interceptServiceException {
        personService.save(
            PersonSaveDto(
                id = null,
                firstName = firstName,
                secondName = secondName,
                birthPlace = birthPlace,
                birthDate = toLocalDate(birthDate),
            )
        ).let(::toPersonSoapDto)
    }

    override fun findById(id: Long): PersonSoapDto = interceptServiceException {
        personService.findById(id).let(::toPersonSoapDto)
    }

    override fun findByFilter(
        firstName: String?,
        secondName: String?,
        birthPlace: String?,
        birthDateFrom: Date?,
        birthDateTo: Date?,
    ): List<PersonSoapDto> = interceptServiceException {
        personService.findByFilter(
            PersonFilter(
                firstName = firstName,
                secondName = secondName,
                birthPlace = birthPlace,
                birthDateFrom = birthDateFrom?.let(::toLocalDate),
                birthDateTo = birthDateTo?.let(::toLocalDate),
            )
        ).map(::toPersonSoapDto)
    }

    override fun update(
        id: Long,
        firstName: String,
        secondName: String,
        birthPlace: String,
        birthDate: Date,
    ): PersonSoapDto = interceptServiceException {
        personService.save(
            PersonSaveDto(
                id = id,
                firstName = firstName,
                secondName = secondName,
                birthPlace = birthPlace,
                birthDate = toLocalDate(birthDate),
            )
        ).let(::toPersonSoapDto)
    }

    override fun deleteById(id: Long): Boolean = interceptServiceException {
        personService.remove(id)
    }

    private fun toPersonSoapDto(entity: PersonDto): PersonSoapDto = PersonSoapDto().apply {
        this.id = entity.id
        this.firstName = entity.firstName
        this.secondName = entity.secondName
        this.birthPlace = entity.birthPlace
        this.birthDate = entity.birthDate
    }

    private fun toLocalDate(date: Date): LocalDate = LocalDateTime
        .ofInstant(date.toInstant(), ZoneOffset.UTC)
        .toLocalDate()

    private fun <T> interceptServiceException(fn: () -> T): T =
        try {
            fn()
        } catch (ex: ServiceException) {
            val soapFactory: SOAPFactory = SOAPFactory.newInstance()
            val soapFault: SOAPFault = soapFactory.createFault(
                ServiceException::class.qualifiedName + "::" + ex.code.code,
                QName("http://schemas.xmlsoap.org/soap/envelope/", "Client")
            )
            throw SOAPFaultException(soapFault)
        }
}
