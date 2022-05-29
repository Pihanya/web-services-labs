package ru.gostev.rest.api.kotlin.extension

import ru.gostev.core.ErrorCode
import ru.gostev.core.ErrorCodeRepository
import ru.gostev.core.exception.ServiceException
import ru.gostev.rest.api.model.ErrorModel
import ru.gostev.rest.api.model.ErrorResponse

fun ServiceException.asErrorResponse(): ErrorResponse =
    ErrorResponse(
        error = ErrorModel(
            code = this.code.code,
            message = this.message ?: "null",
        )
    )

fun ErrorResponse.asServiceException(): ServiceException {
    val errorCode: ErrorCode = ErrorCodeRepository.findByCode(error.code)
        ?: error("Error code [${error.code}] was not found")
    val message: String? = error.message.takeIf { it != "null" }

    return ServiceException(message, errorCode)
        .also { it.stackTrace = emptyArray() }
}
