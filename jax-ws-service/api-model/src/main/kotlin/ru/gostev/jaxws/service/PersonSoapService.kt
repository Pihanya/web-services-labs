package ru.gostev.jaxws.service

import jakarta.jws.WebMethod
import jakarta.jws.WebParam
import java.util.Date
import ru.gostev.core.exception.ServiceException
import ru.gostev.jaxws.model.dto.PersonSoapDto

interface PersonSoapService {

    @WebMethod(operationName = "uploadBinaryData")
    fun uploadBinaryData(content: ByteArray)

    @Throws(ServiceException::class)
    @WebMethod(operationName = "create")
    fun create(
        @WebParam(name = "firstName") firstName: String,
        @WebParam(name = "secondName") secondName: String,
        @WebParam(name = "birthPlace") birthPlace: String,
        @WebParam(name = "birthDate") birthDate: Date,
    ): PersonSoapDto

    @Throws(ServiceException::class)
    @WebMethod(operationName = "findById")
    fun findById(
        @WebParam(name = "id") id: Long,
    ): PersonSoapDto

    @Throws(ServiceException::class)
    @WebMethod(operationName = "findByFilter")
    fun findByFilter(
        @WebParam(name = "firstName") firstName: String? = null,
        @WebParam(name = "secondName") secondName: String? = null,
        @WebParam(name = "birthPlace") birthPlace: String? = null,
        @WebParam(name = "birthDateFrom") birthDateFrom: Date? = null,
        @WebParam(name = "birthDateTo") birthDateTo: Date? = null,
    ): List<PersonSoapDto>

    @Throws(ServiceException::class)
    @WebMethod(operationName = "update")
    fun update(
        @WebParam(name = "id") id: Long,
        @WebParam(name = "firstName") firstName: String,
        @WebParam(name = "secondName") secondName: String,
        @WebParam(name = "birthPlace") birthPlace: String,
        @WebParam(name = "birthDate") birthDate: Date,
    ): PersonSoapDto

    @Throws(ServiceException::class)
    @WebMethod(operationName = "deleteById")
    fun deleteById(
        @WebParam(name = "id") id: Long,
    ): Boolean
}
