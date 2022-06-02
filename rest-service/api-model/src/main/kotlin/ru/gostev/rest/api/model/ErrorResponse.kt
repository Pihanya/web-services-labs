package ru.gostev.rest.api.model

import kotlinx.serialization.Serializable

/**
 * See [Microsoft API Guidlines. Error condition responses](https://github.com/microsoft/api-guidelines/blob/vNext/Guidelines.md#7102-error-condition-responses)
 *
 * ```
 * {
 *   "error": {
 *     "code": "BadArgument",
 *     "message": "Previous passwords may not be reused",
 *     "target": "password",
 *     "innererror": {
 *       "code": "PasswordError",
 *       "innererror": {
 *         "code": "PasswordDoesNotMeetPolicy",
 *         "minLength": "6",
 *         "maxLength": "64",
 *         "characterTypes": ["lowerCase","upperCase","number","symbol"],
 *         "minDistinctCharacterTypes": "2",
 *         "innererror": {
 *           "code": "PasswordReuseNotAllowed"
 *         }
 *       }
 *     }
 *   }
 * }
 * ```
 *
 * ```
 * {
 *   "error": {
 *     "code": "BadArgument",
 *     "message": "Multiple errors in ContactInfo data",
 *     "target": "ContactInfo",
 *     "details": [
 *       {
 *         "code": "NullValue",
 *         "target": "PhoneNumber",
 *         "message": "Phone number must not be null"
 *       },
 *       {
 *         "code": "NullValue",
 *         "target": "LastName",
 *         "message": "Last name must not be null"
 *       },
 *       {
 *         "code": "MalformedValue",
 *         "target": "Address",
 *         "message": "Address is not valid"
 *       }
 *     ]
 *   }
 * }
 * ```
 */
@Serializable
data class ErrorResponse(

    /**
     * The error object.
     */
    val error: ErrorModel,
)
