package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.NetworkError
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import sk.backbone.parent.execution.ParentException
import sk.backbone.parent.utils.jsonToObject
import java.nio.charset.Charset

abstract class ParentHttpException @JvmOverloads constructor(volleyError: VolleyError? = null) : ParentException(volleyError) {
    val responseBody = getResponseBody(volleyError)
    val statusCode = volleyError?.networkResponse?.statusCode

    inline fun <reified Type>getErrors(): Type? {
        return responseBody?.jsonToObject()
    }

    companion object {
        fun getResponseBody(volleyError: VolleyError?): String? {
            return volleyError?.networkResponse?.data?.let { return@let String(it, Charset.forName("utf-8")) }
        }

        fun parseException(volleyError: VolleyError): ParentHttpException {
            return when {
                volleyError.networkResponse?.statusCode == 400 -> BadRequestException(volleyError)
                volleyError.networkResponse?.statusCode == 401 -> AuthorizationException(volleyError)
                volleyError.networkResponse?.statusCode == 402 -> PaymentException(volleyError)
                volleyError.networkResponse?.statusCode == 403 -> ForbiddenException(volleyError)
                volleyError.networkResponse?.statusCode == 404 -> NotFoundException(volleyError)
                volleyError.networkResponse?.statusCode == 405 -> MethodNotAllowedException(volleyError)
                volleyError.networkResponse?.statusCode == 406 -> NotAcceptableException(volleyError)
                volleyError.networkResponse?.statusCode == 408 -> RequestTimeoutException(volleyError)
                volleyError.networkResponse?.statusCode == 409 -> ConflictException(volleyError)
                volleyError.networkResponse?.statusCode == 410 -> GoneException(volleyError)
                volleyError.networkResponse?.statusCode == 411 -> LengthRequiredException(volleyError)
                volleyError.networkResponse?.statusCode == 412 -> PreconditionFailedException(volleyError)
                volleyError.networkResponse?.statusCode == 413 -> PayloadTooLargeException(volleyError)
                volleyError.networkResponse?.statusCode == 414 -> RequestUriTooLongException(volleyError)
                volleyError.networkResponse?.statusCode == 415 -> UnsupportedMediaTypeException(volleyError)
                volleyError.networkResponse?.statusCode == 416 -> RequestRangeNotSatisfiableException(volleyError)
                volleyError.networkResponse?.statusCode == 417 -> ExpectationFailedException(volleyError)
                volleyError.networkResponse?.statusCode == 418 -> IamTeapotException(volleyError)
                volleyError.networkResponse?.statusCode == 420 -> EnhanceYourCalmException(volleyError)
                volleyError.networkResponse?.statusCode == 421 -> MisdirectedRequestException(volleyError)
                volleyError.networkResponse?.statusCode == 422 -> UnprocessableEntityException(volleyError)
                volleyError.networkResponse?.statusCode == 423 -> LockedException(volleyError)
                volleyError.networkResponse?.statusCode == 424 -> FailedDependencyException(volleyError)
                volleyError.networkResponse?.statusCode == 425 -> TooEarlyException(volleyError)
                volleyError.networkResponse?.statusCode == 426 -> UpgradeRequiredException(volleyError)
                volleyError.networkResponse?.statusCode == 429 -> TooManyRequestsException(volleyError)
                volleyError.networkResponse?.statusCode == 431 -> RequestHeaderFieldsTooLargeException(volleyError)
                volleyError.networkResponse?.statusCode == 444 -> NoResponseException(volleyError)
                volleyError.networkResponse?.statusCode == 450 -> BlockedByWindowsParentalControlsException(volleyError)
                volleyError.networkResponse?.statusCode == 451 -> UnavailableForLegalReasonsException(volleyError)
                volleyError.networkResponse?.statusCode == 499 -> ClientClosedRequestException(volleyError)
                volleyError.networkResponse?.statusCode == 500 -> InternalServerErrorException(volleyError)
                volleyError.networkResponse?.statusCode == 501 -> NotImplementedException(volleyError)
                volleyError.networkResponse?.statusCode == 502 -> BadGatewayException(volleyError)
                volleyError.networkResponse?.statusCode == 503 -> ServiceUnavailableException(volleyError)
                volleyError.networkResponse?.statusCode == 504 -> GatewayTimeoutException(volleyError)
                volleyError.networkResponse?.statusCode == 506 -> VariantAlsoNegotiatesException(volleyError)
                volleyError.networkResponse?.statusCode == 507 -> InsufficientStorageException(volleyError)
                volleyError.networkResponse?.statusCode == 508 -> LoopDetectedException(volleyError)
                volleyError.networkResponse?.statusCode == 509 -> BandwidthLimitExceededException(volleyError)
                volleyError.networkResponse?.statusCode == 510 -> NotExtendedException(volleyError)
                volleyError.networkResponse?.statusCode == 511 -> NetworkAuthenticationRequiredException(volleyError)
                volleyError.networkResponse?.statusCode == 599 -> NetworkConnectTimeoutErrorException(volleyError)
                volleyError is NetworkError -> NetworkException(volleyError)
                volleyError is TimeoutError -> NetworkTimeoutException(volleyError)
                else -> CommunicationException(volleyError)
            }
        }
    }
}