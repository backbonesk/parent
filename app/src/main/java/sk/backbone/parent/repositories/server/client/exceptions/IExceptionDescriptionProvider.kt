package sk.backbone.parent.repositories.server.client.exceptions

import android.content.Context

interface IExceptionDescriptionProvider {
    fun parseBadRequestException(context: Context, exception: BadRequestException, responseBody: String?, statusCode: Int?): String?
    fun parseAuthorizationException(context: Context, exception: AuthorizationException, responseBody: String?, statusCode: Int?): String?
    fun parsePaymentException(context: Context, exception: PaymentException, responseBody: String?, statusCode: Int?): String?
    fun parseForbiddenException(context: Context, exception: ForbiddenException, responseBody: String?, statusCode: Int?): String?
    fun parseNotFoundException(context: Context, exception: NotFoundException, responseBody: String?, statusCode: Int?): String?
    fun parseConflictException(context: Context, exception: ConflictException, responseBody: String?, statusCode: Int?): String?
    fun parseValidationException(context: Context, exception: ValidationException, responseBody: String?, statusCode: Int?): String?
    fun parseServerException(context: Context, exception: ServerException, responseBody: String?, statusCode: Int?): String?
    fun parseCommunicationException(context: Context, exception: CommunicationException, responseBody: String?, statusCode: Int?): String?
    fun getDefaultErrorMessage(context: Context, exception: Throwable): String

    fun getDescription(context: Context, throwable: Throwable): String {
        return when(throwable){
            is BadRequestException -> {
                parseBadRequestException(context, throwable, throwable.responseBody, throwable.statusCode)
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
            is NotFoundException -> {
                parseNotFoundException(context, throwable, throwable.responseBody, throwable.statusCode)
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
