package sk.backbone.parent.repositories.server

import com.android.volley.Request
import org.json.JSONArray
import org.json.JSONObject
import sk.backbone.parent.repositories.server.client.HttpClient
import sk.backbone.parent.repositories.server.client.IHttpResponseWrapper
import sk.backbone.parent.repositories.server.client.requests.JsonObjectHttpRequest
import sk.backbone.parent.utils.jsonToObject
import kotlin.coroutines.Continuation

abstract class ParentServerRepository {
    abstract var client: HttpClient

    abstract val additionalHeadersProvider: (JsonObjectHttpRequest<*>) -> Map<String, String>

    suspend inline fun <reified Type>executeRequest(crossinline requestFactoryMethod: (Continuation<Type?>) -> Request<*>): Type? {
        return client.executeRequest {
            requestFactoryMethod(it)
        }
    }

    protected inline fun <reified HttpResponseWrapperType, reified Type>parseResponseFromWrapper(value: JSONObject?): Type? where HttpResponseWrapperType : IHttpResponseWrapper<Type> {
        val response: HttpResponseWrapperType? = value?.toString()?.jsonToObject()
        return response?.getResult()
    }

    protected inline fun <reified Type>parseResponse(value: JSONObject?): Type? {
        return value?.toString()?.jsonToObject()
    }

    protected inline fun <reified HttpResponseWrapperType, reified Type>parseResponseFromWrapper(value: JSONArray?): Type? where HttpResponseWrapperType : IHttpResponseWrapper<Type> {
        val response: HttpResponseWrapperType? = value?.toString()?.jsonToObject()
        return response?.getResult()
    }

    protected inline fun <reified Type>parseResponse(value: JSONArray?): Type? {
        return value?.toString()?.jsonToObject()
    }

    protected inline fun <reified HttpResponseWrapperType, reified Type>parseResponseFromWrapper(value: String?): Type? where HttpResponseWrapperType : IHttpResponseWrapper<Type> {
        val response: HttpResponseWrapperType? = value?.jsonToObject()
        return response?.getResult()
    }

    protected inline fun <reified Type>parseResponse(value: String?): Type? {
        return value?.jsonToObject()
    }
}