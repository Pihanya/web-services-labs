package ru.gostev.core.exception

import ru.gostev.core.ErrorCode
import ru.gostev.core.ErrorCodes

class ServiceException : Exception {

    @Suppress("MemberVisibilityCanBePrivate")
    val code: ErrorCode

    constructor(message: String?, code: ErrorCode, cause: Throwable?) : super(message, cause) {
        this.code = code
    }

    constructor(message: String?, code: ErrorCode) : super(message) {
        this.code = code
    }

    constructor(code: ErrorCode, cause: Throwable?) : super(cause) {
        this.code = code
    }

    constructor(code: ErrorCode) : this(code = code, cause = null)

    constructor(cause: Throwable) : this(code = ErrorCodes.Persons001UnknownError, cause)
}
