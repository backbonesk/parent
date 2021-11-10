package sk.backbone.parent.repositories.server.client.requests

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
import sk.backbone.parent.utils.getUrl
import sk.backbone.parent.utils.toJsonString
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class JsonObjectHttpRequest<Type>(
    override val continuation: Continuation<Type?>,
    override val requestMethod: Int,
    override val schema: String,
    override val serverAddress: String,
    override val apiVersion: String,
    override val endpoint: String,
    override val queryParameters: List<Pair<String, String?>>?,
    final override val body: Any?,
    parseSuccessResponse: (JSONObject?) -> Type?,
    final override val bodyExclusionStrategy: ExclusionStrategy? = null,
    override val additionalHeadersProvider: ((IParentRequest<*, *>) -> Map<String, String>?)
) : JsonRequest<JSONObject>(
    requestMethod,
    getUrl(schema, serverAddress, apiVersion, endpoint, queryParameters),
    body?.toJsonString(bodyExclusionStrategy),
    onSuccess(continuation, parseSuccessResponse),
    onError(continuation)
),  IParentRequest<Type, JSONObject>{

    init {
        Log.i(LOGS_TAG, "Request Method: $method")
        Log.i(LOGS_TAG, "Request Url: $url")
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
            additionalHeadersProvider(this@JsonObjectHttpRequest)?.let { putAll(it) }
        }.also {
            Log.i(LOGS_TAG, it.toString())
        }
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject>? {
        return try {
            val jsonString = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers, response.getContentTypeCharset(PROTOCOL_CHARSET))))

            Log.e(LOGS_TAG, "Status:${response.statusCode}")
            Log.e(LOGS_TAG, "Response body:${jsonString}")

            if (jsonString.isEmpty()) {
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
                try {
                    val response = parseSuccessResponse(it)
                    continuation.resume(response)
                } catch (throwable: Throwable){
                    continuation.resumeWithException(throwable)
                }
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