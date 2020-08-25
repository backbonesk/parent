package sk.backbone.parent.repositories.server.client

import android.net.Uri
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import sk.backbone.parent.repositories.server.client.exceptions.ParentHttpException
import sk.backbone.parent.utils.getContentTypeCharset
import sk.backbone.parent.utils.notNullValuesOnly
import sk.backbone.parent.utils.toJsonString
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class UrlHttpRequest<Type>(
    val continuation: Continuation<Type?>,
    val requestMethod: Int,
    val schema: String,
    val serverAddress: String,
    val apiVersion: String,
    val endpoint: String,
    val data: Map<String, String?>?,
    val parseSuccessResponse: (String?) -> Type?,
    val additionalHeadersProvider: ((UrlHttpRequest<*>) -> Map<String, String>?)
) : StringRequest(
    requestMethod,
    getUri(schema, serverAddress, apiVersion, endpoint, null).toString(),
    onSuccess(continuation, parseSuccessResponse),
    onError(continuation)){

    val uri by lazy {
        return@lazy getUri(schema, serverAddress, apiVersion, endpoint, null)
    }

    init {
        Log.i(LOGS_TAG, "Request Method: $method")
        Log.i(LOGS_TAG, "Request Url: $uri")
        Log.i(LOGS_TAG, "Request Data:\n${data.toJsonString()}")
        retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    override fun getHeaders(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            putAll(super.getHeaders())
            additionalHeadersProvider(this@UrlHttpRequest)?.let { putAll(it) }
        }
    }

    override fun getParams(): MutableMap<String, String> {
        val params = mutableMapOf<String, String>()
        super.getParams()?.let { params.putAll(it) }
        data?.notNullValuesOnly()?.let { params.putAll(it) }
        return params
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<String>? {
        return try {
            val responseString =
                String(
                    response.data,
                    Charset.forName(HttpHeaderParser.parseCharset(
                        response.headers,
                        response.getContentTypeCharset(paramsEncoding))
                    )
                )

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
        private const val LOGS_TAG = "UriHttpRequest"

        private fun getUri(schema: String, serverAddress: String, apiVersion: String, endpoint: String, queryParameters: Map<String, String?>?): Uri{
            return Uri.Builder().scheme(schema).encodedAuthority(serverAddress).appendEncodedPath(apiVersion).appendEncodedPath(endpoint).apply {
                queryParameters?.let {
                    for (key in it.notNullValuesOnly().keys){
                        this.appendQueryParameter(key, queryParameters[key])
                    }
                }
            }.build()
        }

        private fun <T>onSuccess(continuation: Continuation<T?>, parseSuccessResponse: (String?) -> T?): Response.Listener<String?>{
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