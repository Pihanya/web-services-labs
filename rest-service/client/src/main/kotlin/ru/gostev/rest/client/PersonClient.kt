package ru.gostev.rest.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.Charsets
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import ru.gostev.rest.api.kotlin.extension.asServiceException
import ru.gostev.rest.api.model.ErrorResponse
import ru.gostev.rest.api.model.Person
import ru.gostev.rest.api.model.PersonCreationRequest
import ru.gostev.rest.api.model.PersonFilter
import ru.gostev.rest.api.model.PersonUpdateRequest

private fun isValidatedStatusCode(status: HttpStatusCode): Boolean = when {
    status.isSuccess() -> false
    (status == HttpStatusCode.NotFound) -> false
    else -> true
}

private suspend fun HttpResponse.getErrorResponseOrNull(): ErrorResponse? =
    try {
        body()
    } catch (ex: Exception) {
        null
    }

class PersonClient {

    private val client: HttpClient = HttpClient(CIO) {
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(
                        username = "login",
                        password = "password",
                    )
                }
            }
        }
        defaultRequest {
            with(url) {
                protocol = URLProtocol.HTTP
                host = "0.0.0.0"
                port = 8080
                path("rest", "api", "v1", "persons")
            }
        }
        Charsets {
            register(Charsets.UTF_8)
        }
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                }
            )
        }

        HttpResponseValidator {
            validateResponse block@{ response ->
                if (!isValidatedStatusCode(response.status)) return@block

                response.getErrorResponseOrNull()
                    ?.asServiceException()
                    ?.let { throw it }
            }
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }

    fun create(request: PersonCreationRequest): Person = runBlocking {
        client.post {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    fun update(
        id: Long,
        request: PersonUpdateRequest,
    ): Person = runBlocking {
        client.put {
            url.appendPathSegments("persons", id.toString())
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    fun findByFilter(filter: PersonFilter): List<Person> = runBlocking {
        val response = client.post {
            url.appendPathSegments("persons", "find-by-filter")
            contentType(ContentType.Application.Json)
            setBody(filter)
        }
        if (response.status == HttpStatusCode.NotFound) {
            emptyList()
        } else {
            response.body()
        }
    }

    fun findById(id: Long): Person = runBlocking {
        client.get { url.appendPathSegments("persons", id.toString()) }
            .body()
    }

    fun deleteById(id: Long): Boolean = runBlocking {
        val response = client.delete { url.appendPathSegments("persons", id.toString()) }
        when (response.status) {
            HttpStatusCode.OK -> true
            HttpStatusCode.NotFound -> false
            else -> error("Unknown response status: ${response.status}")
        }
    }
}
