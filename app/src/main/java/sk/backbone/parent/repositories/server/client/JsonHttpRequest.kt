package sk.backbone.parent.repositories.server.client

import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import com.google.gson.ExclusionStrategy
import org.json.JSONException
import org.json.JSONObject
import sk.backbone.parent.repositories.server.client.exceptions.ParentHttpException
import sk.backbone.parent.utils.getContentTypeCharset
import sk.backbone.parent.utils.getUri
import sk.backbone.parent.utils.toJsonString
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class JsonHttpRequest<Type>(
    val continuation: Continuation<Type?>,
    val requestMethod: Int,
    val schema: String,
    val serverAddress: String,
    val apiVersion: String,
    val endpoint: String,
    val queryParameters: Map<String, String?>?,
    val body: Any?,
    val parseSuccessResponse: (JSONObject?) -> Type?,
    val bodyExclusionStrategy: ExclusionStrategy? = null,
    val additionalHeadersProvider: ((JsonHttpRequest<*>) -> Map<String, String>?)
) : JsonRequest<JSONObject>(
    requestMethod,
    getUri(schema, serverAddress, apiVersion, endpoint, queryParameters).toString(),
    body?.toJsonString(bodyExclusionStrategy),
    onSuccess(continuation, parseSuccessResponse),
    onError(continuation)){

    val uri by lazy {
        return@lazy getUri(schema, serverAddress, apiVersion, endpoint, queryParameters)
    }

    init {
        Log.i(LOGS_TAG, "Request Method: $method")
        Log.i(LOGS_TAG, "Request Url: $uri")
        Log.i(LOGS_TAG, "Request body:\n${body.toJsonString(bodyExclusionStrategy)}")
        retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    override fun getHeaders(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            putAll(super.getHeaders())
            additionalHeadersProvider(this@JsonHttpRequest)?.let { putAll(it) }
        }
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject>? {
        return try {
            val jsonString = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers, response.getContentTypeCharset(PROTOCOL_CHARSET))))

            if (jsonString.isEmpty() && response.statusCode == 204) {
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response))
            }

            Response.success(JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response))
        }
        catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        }
        catch (je: JSONException) {
            Response.error(ParseError(je))
        }
    }

    companion object {
        private const val LOGS_TAG = "JsonHttpRequest"

        private fun <T>onSuccess(continuation: Continuation<T?>, parseSuccessResponse: (JSONObject?) -> T?): Response.Listener<JSONObject?>{
            return Response.Listener {
                Log.i(LOGS_TAG, it.toString())
                val response = parseSuccessResponse(it)
                continuation.resume(response)
            }
        }

        private fun onError(continuation: Continuation<*>): Response.ErrorListener{
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