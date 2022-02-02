package sk.backbone.parent.repositories.server.client.requests

import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import sk.backbone.parent.repositories.server.client.exceptions.ParentHttpException
import sk.backbone.parent.utils.getContentTypeCharset
import sk.backbone.parent.utils.getUrl
import sk.backbone.parent.utils.notNullValuesOnly
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class UrlHttpRequest<Type>(
    override val continuation: Continuation<Type?>,
    override val requestMethod: Int,
    override val schema: String,
    override val serverAddress: String,
    override val apiVersion: String,
    override val endpoint: String,
    final override val formData: Map<String, String?>?,
    parseSuccessResponse: (String?) -> Type?
) : StringRequest(
    requestMethod,
    getUrl(schema, serverAddress, apiVersion, endpoint, null),
    onSuccess(continuation, parseSuccessResponse),
    onError(continuation)
), IParentRequest<Type, String>{

    init {
        retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    override fun getParams(): MutableMap<String, String> {
        val params = mutableMapOf<String, String>()
        super.getParams()?.let { params.putAll(it) }
        formData?.notNullValuesOnly()?.let { params.putAll(it) }
        return params
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<String>? {
        return try {
            val responseString = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers, response.getContentTypeCharset(paramsEncoding))))

            if (responseString.isEmpty() && response.statusCode == 204) {
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response))
            }

            Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response))
        }
        catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        }
        catch (je: JSONException) {
            Response.error(ParseError(je))
        }
    }

    companion object {
        private fun <T>onSuccess(continuation: Continuation<T?>, parseSuccessResponse: (String?) -> T?): Response.Listener<String?>{
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