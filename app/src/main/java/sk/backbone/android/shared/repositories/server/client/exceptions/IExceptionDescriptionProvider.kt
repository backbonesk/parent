package sk.backbone.android.shared.repositories.server.client.exceptions

import android.content.Context

interface IExceptionDescriptionProvider {
    fun parseBadRequestException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun parseAuthorizationException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun parsePaymentException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun parseForbiddenException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun parseConflictException(context: Context, throwable: ConflictException, responseBody: String?, statusCode: Int?): String?
    fun parseValidationException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun parseServerException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun parseCommunicationException(context: Context, baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String?
    fun getDefaultErrorMessage(context: Context, throwable: Throwable): String

    fun getDescription(context: Context, throwable: Throwable): String {
        return when(throwable){
            is BadRequestException -> {
                parseAuthorizationException(context, throwable, throwable.responseBody, throwable.statusCode)
            }
            is AuthorizationException -> {
                parseAuthorizationException(context, throwable, throwable.responseBody, throwable.statusCode)
            }
            is PaymentException -> {
                parsePaymentException(context, throwable, throwable.responseBody, throwable.statusCode)
            }
            is ForbiddenException -> {
                parseForbiddenException(context, throwable, throwable.responseBody, throwable.statusCode)
            }
            is ConflictException -> {
                parseConflictException(context, throwable, throwable.responseBody, throwable.statusCode)
            }
            is ValidationException -> {
                parseValidationException(context, throwable, throwable.responseBody, throwable.statusCode)
            }
            is ServerException -> {
                parseServerException(context, throwable, throwable.responseBody, throwable.statusCode)
            }
            is CommunicationException -> {
                parseCommunicationException(context, throwable, throwable.responseBody, throwable.statusCode)
            }
            else -> {
                getDefaultErrorMessage(context, throwable)
            }
        } ?: getDefaultErrorMessage(context, throwable)
    }
}
