package sk.backbone.android.shared.repositories.server.client

import android.net.Uri
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
import sk.backbone.android.shared.repositories.server.client.exceptions.BaseHttpException
import sk.backbone.android.shared.utils.notNullValuesOnly
import sk.backbone.android.shared.utils.toJsonString
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class HttpRequest<Type>(
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
    val additionalHeaders: Map<String, String?> = mapOf()
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
        //TODO: Add console logging
        retryPolicy = DefaultRetryPolicy(
            60000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    override fun getHeaders(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            putAll(super.getHeaders())
            putAll(additionalHeaders.notNullValuesOnly())
        }
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject>? {
        return try {
            val jsonString = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET)))

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
        private fun getUri(schema: String, serverAddress: String, apiVersion: String, endpoint: String, queryParameters: Map<String, String?>?): Uri{
            return Uri.Builder().scheme(schema).encodedAuthority(serverAddress).appendEncodedPath(apiVersion).appendEncodedPath(endpoint).apply {
                queryParameters?.let {
                    for (key in it.notNullValuesOnly().keys){
                        this.appendQueryParameter(key, queryParameters[key])
                    }
                }
            }.build()
        }

        private fun <T>onSuccess(continuation: Continuation<T?>, parseSuccessResponse: (JSONObject?) -> T?): Response.Listener<JSONObject?>{
            return Response.Listener {
                Log.i("HttpResponseBody", it.toString())
                val response = parseSuccessResponse(it)
                continuation.resume(response)
            }
        }

        private fun onError(continuation: Continuation<*>): Response.ErrorListener{
            return Response.ErrorListener {
                Log.i("HttpResponseBody", BaseHttpException.getResponseBody(it).toString())
                val exception = BaseHttpException.parseException(it)
                continuation.resumeWithException(exception)
            }
        }
    }
}