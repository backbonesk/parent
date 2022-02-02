package sk.backbone.parent.repositories.server.client.requests

import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import sk.backbone.parent.repositories.server.client.exceptions.ParentHttpException
import sk.backbone.parent.utils.getUrl
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


open class ByteArrayHttpRequest(
    override val continuation: Continuation<ByteArray?>,
    override val schema: String,
    override val serverAddress: String,
    override val apiVersion: String,
    override val endpoint: String,
    override val queryParameters: List<Pair<String, String?>>?,
) : Request<ByteArray>(REQUEST_METHOD, getUrl(schema, serverAddress, apiVersion, endpoint, queryParameters), onError(continuation)),  IParentRequest<ByteArray, ByteArray>{

    override val requestMethod: Int = REQUEST_METHOD

    init {
        retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<ByteArray>? {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: ByteArray?) {
        continuation.resume(response)
    }

    companion object {
        private const val REQUEST_METHOD = Method.GET

        private fun onError(continuation: Continuation<*>): Response.ErrorListener {
            return Response.ErrorListener {
                val exception = ParentHttpException.parseException(it)
                continuation.resumeWithException(exception)
            }
        }
    }
}