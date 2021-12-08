package sk.backbone.parent.repositories.server.client.requests

import android.util.Log
import com.android.volley.*
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
    override val additionalHeadersProvider: ((IParentRequest<*, *>) -> Map<String, String>?)
) : Request<ByteArray>(REQUEST_METHOD, getUrl(schema, serverAddress, apiVersion, endpoint, queryParameters), onError(continuation)),  IParentRequest<ByteArray, ByteArray>{

    override val requestMethod: Int = REQUEST_METHOD

    init {
        Log.i(LOGS_TAG, "Request Method: $method")
        Log.i(LOGS_TAG, "Request Url: $url")
        retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    override fun getHeaders(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            putAll(super.getHeaders())
            additionalHeadersProvider(this@ByteArrayHttpRequest)?.let { putAll(it) }
        }.also {
            Log.i(LOGS_TAG, it.toString())
        }
    }


    override fun parseNetworkResponse(response: NetworkResponse): Response<ByteArray>? {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: ByteArray?) {
        continuation.resume(response)
    }

    companion object {
        private const val LOGS_TAG = "ByteArrayHttpRequest"
        private const val REQUEST_METHOD = Method.GET

        private fun onError(continuation: Continuation<*>): Response.ErrorListener {
            return Response.ErrorListener {
                Log.e(LOGS_TAG, "Status:${it.networkResponse?.statusCode}")
                Log.e(LOGS_TAG, "Response body:${ParentHttpException.getResponseBody(it).toString()}")
                Log.e(LOGS_TAG, "Exception was thrown", it)
                val exception = ParentHttpException.parseException(it)
                continuation.resumeWithException(exception)
            }
        }
    }
}