package sk.backbone.parent.repositories.server.client.requests

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
) : JsonRequest<JSONObject>(
    requestMethod,
    getUrl(schema, serverAddress, apiVersion, endpoint, queryParameters),
    body?.toJsonString(bodyExclusionStrategy),
    onSuccess(continuation, parseSuccessResponse),
    onError(continuation)
),  IParentRequest<Type, JSONObject>{

    init {
        retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject>? {
        return try {
            val jsonString = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers, response.getContentTypeCharset(PROTOCOL_CHARSET))))

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
                val exception = ParentHttpException.parseException(it)
                continuation.resumeWithException(exception)
            }
        }
    }
}