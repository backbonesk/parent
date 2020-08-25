package sk.backbone.parent.repositories.server

import android.content.Context
import com.android.volley.Request
import org.json.JSONObject
import sk.backbone.parent.repositories.server.client.*
import sk.backbone.parent.utils.jsonToObject
import kotlin.coroutines.Continuation

abstract class ParentServerRepository<TokensWrapperType>(val context: Context, val tokensProvider: ITokensProvider<TokensWrapperType>){
    val client = HttpClient(context)

    abstract val additionalHeadersProvider: (JsonHttpRequest<*>) -> Map<String, String>

    fun getTokens() = tokensProvider.getLocalTokens()

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

    protected inline fun <reified HttpResponseWrapperType, reified Type>parseResponseFromWrapper(value: String?): Type? where HttpResponseWrapperType : IHttpResponseWrapper<Type> {
        val response: HttpResponseWrapperType? = value?.jsonToObject()
        return response?.getResult()
    }

    protected inline fun <reified Type>parseResponse(value: String?): Type? {
        return value?.jsonToObject()
    }
}