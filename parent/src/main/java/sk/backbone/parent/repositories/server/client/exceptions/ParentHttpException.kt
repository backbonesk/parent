package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError
import sk.backbone.parent.utils.jsonToObject
import java.nio.charset.Charset

abstract class ParentHttpException(private val volleyError: VolleyError) : Exception(volleyError) {
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

        fun parseException(volleyError: VolleyError): ParentHttpException {
            return when(volleyError.networkResponse?.statusCode){
                400 -> BadRequestException(volleyError)
                401 -> AuthorizationException(volleyError)
                402 -> PaymentException(volleyError)
                403 -> ForbiddenException(volleyError)
                404 -> NotFoundException(volleyError)
                405 -> MethodNotAllowedException(volleyError)
                406 -> NotAcceptableException(volleyError)
                408 -> RequestTimeoutException(volleyError)
                409 -> ConflictException(volleyError)
                410 -> GoneException(volleyError)
                411 -> LengthRequiredException(volleyError)
                412 -> PreconditionFailedException(volleyError)
                413 -> PayloadTooLargeException(volleyError)
                414 -> RequestUriTooLongException(volleyError)
                415 -> UnsupportedMediaTypeException(volleyError)
                416 -> RequestRangeNotSatisfiableException(volleyError)
                417 -> ExpectationFailedException(volleyError)
                418 -> IamTeapotException(volleyError)
                420 -> EnhanceYourCalmException(volleyError)
                421 -> MisdirectedRequestException(volleyError)
                422 -> UnprocessableEntityException(volleyError)
                423 -> LockedException(volleyError)
                424 -> FailedDependencyException(volleyError)
                425 -> TooEarlyException(volleyError)
                426 -> UpgradeRequiredException(volleyError)
                429 -> TooManyRequestsException(volleyError)
                431 -> RequestHeaderFieldsTooLargeException(volleyError)
                444 -> NoResponseException(volleyError)
                450 -> BlockedByWindowsParentalControlsException(volleyError)
                451 -> UnavailableForLegalReasonsException(volleyError)
                499 -> ClientClosedRequestException(volleyError)
                500 -> InternalServerErrorException(volleyError)
                501 -> NotImplementedException(volleyError)
                502 -> BadGatewayException(volleyError)
                503 -> ServiceUnavailableException(volleyError)
                504 -> GatewayTimeoutException(volleyError)
                506 -> VariantAlsoNegotiatesException(volleyError)
                507 -> InsufficientStorageException(volleyError)
                508 -> LoopDetectedException(volleyError)
                509 -> BandwidthLimitExceededException(volleyError)
                510 -> NotExtendedException(volleyError)
                511 -> NetworkAuthenticationRequiredException(volleyError)
                599 -> NetworkConnectTimeoutErrorException(volleyError)
                else -> CommunicationException(volleyError)
            }
        }
    }
}