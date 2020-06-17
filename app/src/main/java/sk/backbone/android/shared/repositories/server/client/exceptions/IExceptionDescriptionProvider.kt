package sk.backbone.android.shared.repositories.server.client.exceptions

import android.content.Context

interface IExceptionDescriptionProvider {
    fun parseAuthorizationException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun parseCommunicationException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun parseForbiddenException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun parsePaymentException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun parseServerException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun parseValidationException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun getDefaultErrorMessage(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String

    fun getDescription(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String {
        return when(baseHttpException){
            is AuthorizationException -> {
                parseAuthorizationException(context, baseHttpException, responseBody, statusCode)
            }
            is CommunicationException -> {
                parseCommunicationException(context, baseHttpException, responseBody, statusCode)
            }
            is ForbiddenException -> {
                parseForbiddenException(context, baseHttpException, responseBody, statusCode)
            }
            is PaymentException -> {
                parsePaymentException(context, baseHttpException, responseBody, statusCode)
            }
            is ServerException -> {
                parseServerException(context, baseHttpException, responseBody, statusCode)
            }
            is ValidationException -> {
                parseValidationException(context, baseHttpException, responseBody, statusCode)
            }
            else -> {
                getDefaultErrorMessage(context, baseHttpException, responseBody, statusCode)
            }
        } ?: getDefaultErrorMessage(context, baseHttpException, responseBody, statusCode)
    }
}
