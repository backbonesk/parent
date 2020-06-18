package sk.backbone.android.shared.repositories.server.client

import android.net.Uri
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import org.json.JSONException
import org.json.JSONObject
import sk.backbone.android.shared.repositories.server.client.exceptions.BaseHttpException
import sk.backbone.android.shared.repositories.server.client.exceptions.IExceptionDescriptionProvider
import sk.backbone.android.shared.utils.notNullValuesOnly
import sk.backbone.android.shared.utils.toJsonString
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

abstract class BaseHttpRequest<Type>(
    val continuation: Continuation<Type?>,
    val parseSuccessResponse: (JSONObject?) -> Type?,
    val requestParameters: RequestParameters<Type>
) : JsonRequest<JSONObject>(
    requestParameters.requestMethod,
    getUri(requestParameters.schema, requestParameters.serverAddress, requestParameters.apiVersion, requestParameters.endpoint, requestParameters.queryParameters).toString(),
    requestParameters.body?.toJsonString(requestParameters.bodyExclusionStrategy),
    onSuccess(continuation, parseSuccessResponse),
    onError(requestParameters.errorParser, continuation)){

    val uri by lazy {
        return@lazy getUri(requestParameters.schema, requestParameters.serverAddress, requestParameters.apiVersion, requestParameters.endpoint, requestParameters.queryParameters)
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
            putAll(requestParameters.additionalHeaders.notNullValuesOnly())
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

        private fun onError(errorParser: IExceptionDescriptionProvider, continuation: Continuation<*>): Response.ErrorListener{
            return Response.ErrorListener {
                Log.i("HttpResponseBody", BaseHttpException.getResponseBody(it).toString())
                val exception = BaseHttpException.parseException(it, errorParser)
                continuation.resumeWithException(exception)
            }
        }
    }
}