package com.weather.weatherapp.utils

import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.JsonConvertException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException

abstract class BaseDataSource {

    suspend inline fun <reified T> getResult(call: () -> HttpResponse): RestClientResult<T> {
        val result: HttpResponse?
        return try {
            result = call()
            val successCodes = listOf(HttpStatusCode.OK, HttpStatusCode.Created, HttpStatusCode.Accepted)
            if (successCodes.contains(result.status)) {
                val data: T = result.body()
                RestClientResult.success(data)
            }
            else {
                val errorJson = result.bodyAsText()

                val message = Regex("\"message\"\\s*:\\s*\"(.*?)\"")
                    .find(errorJson)
                    ?.groupValues?.get(1)
                    ?: NetworkErrorMessages.SOME_ERROR_OCCURRED

                RestClientResult.error(
                    errorMessage = message.ifBlank { NetworkErrorMessages.SOME_ERROR_OCCURRED },
                    errorCode = result.status.value,
                )
            }
        } catch (e: ClientRequestException) {
            val errorBody =
                try {
                    e.response.body<ErrorResponse>()
                } catch (ex: Exception) {
                    null
                }
            return when (val statusCode = e.response.status.value) {

                NetworkErrorCodes.BAD_REQUEST -> {

                    RestClientResult.error(
                        errorMessage = errorBody?.message ?: NetworkErrorMessages.BAD_REQUEST,
                        errorCode = statusCode,
                    )
                }
                NetworkErrorCodes.NOT_FOUND -> {
                    RestClientResult.error(
                        errorMessage = errorBody?.message ?: NetworkErrorMessages.BAD_REQUEST,
                        errorCode = statusCode,
                    )
                }

                else -> {
                    RestClientResult.error(
                        errorMessage = e.message,
                        errorCode = NetworkErrorCodes.UNKNOWN_ERROR_OCCURRED,
                    )
                }
            }
        } catch (e: ServerResponseException) {
            val statusCode = e.response.status.value
            RestClientResult.error(
                errorMessage = NetworkErrorMessages.APP_UNDER_MAINTENANCE,
                errorCode = statusCode,
            )
        } catch (e: IOException) {
            RestClientResult.error(
                errorMessage = e.message.toString(),
                errorCode = NetworkErrorCodes.INTERNET_NOT_WORKING,
            )
        }  catch (e: SocketTimeoutException) {
            RestClientResult.error(
                errorMessage =  e.message.toString(),
                errorCode = NetworkErrorCodes.INTERNET_NOT_WORKING,
            )
        } catch (e: SerializationException) {
            RestClientResult.error(
                errorMessage =  e.message.toString(),
                errorCode = NetworkErrorCodes.DATA_SERIALIZATION_ERROR,
            )
        } catch (e: JsonConvertException) {
            RestClientResult.error(
                errorMessage =  e.message.toString(),
                errorCode = NetworkErrorCodes.DATA_SERIALIZATION_ERROR,
            )
        } catch (e: CancellationException) {
            RestClientResult.error(
                errorMessage =
                    "", // This is a special case in which we don't want to show any error
                errorCode = NetworkErrorCodes.NETWORK_CALL_CANCELLED,
            )
        } catch (e: Exception) {
            RestClientResult.error(
                errorMessage = e.message ?: NetworkErrorMessages.SOME_ERROR_OCCURRED,
                errorCode = NetworkErrorCodes.UNKNOWN_ERROR_OCCURRED,
            )
        }
    }
}
