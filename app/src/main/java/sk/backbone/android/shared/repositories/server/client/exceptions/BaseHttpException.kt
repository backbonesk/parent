package sk.backbone.android.shared.repositories.server.client.exceptions

import android.content.Context
import com.android.volley.VolleyError
import java.nio.charset.Charset

abstract class BaseHttpException(private val volleyError: VolleyError, private val errorParser: IExceptionDescriptionProvider) : Exception(volleyError) {
    private val responseBody by lazy {
        return@lazy getResponseBody(volleyError)
    }

    private val statusCode by lazy {
        return@lazy volleyError.networkResponse?.statusCode
    }

    open fun getDescription(context: Context): String {
        return errorParser.getDescription(context, this, responseBody, statusCode)
    }

    companion object {
        fun getResponseBody(volleyError: VolleyError): String? {
            return volleyError.networkResponse?.data?.let { return@let String(it, Charset.forName("utf-8")) }
        }

        fun parseException(volleyError: VolleyError, parser: IExceptionDescriptionProvider): Throwable {
            return when(volleyError.networkResponse?.statusCode){
                401 -> AuthorizationException(volleyError, parser)
                402 -> PaymentException(volleyError, parser)
                403 -> ForbiddenException(volleyError, parser)
                422 -> ValidationException(volleyError, parser)
                500 -> ServerException(volleyError, parser)
                else -> CommunicationException(volleyError, parser)
            }
        }
    }
}