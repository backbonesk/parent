package sk.backbone.android.shared.repositories.server.client.exceptions

import com.android.volley.VolleyError
import sk.backbone.android.shared.utils.jsonToObject
import java.nio.charset.Charset

abstract class BaseHttpException(private val volleyError: VolleyError) : Exception(volleyError) {
    val responseBody by lazy {
        return@lazy getResponseBody(volleyError)
    }

    val statusCode by lazy {
        return@lazy volleyError.networkResponse?.statusCode
    }

    inline fun <reified Type>getErrors(): Type? {
        return responseBody?.jsonToObject()
    }

    companion object {
        fun getResponseBody(volleyError: VolleyError): String? {
            return volleyError.networkResponse?.data?.let { return@let String(it, Charset.forName("utf-8")) }
        }

        fun parseException(volleyError: VolleyError): Throwable {
            return when(volleyError.networkResponse?.statusCode){
                400 -> BadRequestException(volleyError)
                401 -> AuthorizationException(volleyError)
                402 -> PaymentException(volleyError)
                403 -> ForbiddenException(volleyError)
                409 -> ConflictException(volleyError)
                422 -> ValidationException(volleyError)
                500 -> ServerException(volleyError)
                else -> CommunicationException(volleyError)
            }
        }
    }
}