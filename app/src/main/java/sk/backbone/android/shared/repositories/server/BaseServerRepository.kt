package sk.backbone.android.shared.repositories.server

import android.content.Context
import org.json.JSONObject
import sk.backbone.android.shared.repositories.server.client.HttpClient
import sk.backbone.android.shared.repositories.server.client.HttpRequest
import sk.backbone.android.shared.repositories.server.client.IHttpResponseWrapper
import sk.backbone.android.shared.repositories.server.client.ITokensProvider
import sk.backbone.android.shared.utils.jsonToObject
import kotlin.coroutines.Continuation

abstract class BaseServerRepository<TokensWrapper>(val context: Context, val tokensProvider: ITokensProvider<TokensWrapper>){
    val client = HttpClient(context)

    abstract val additionalHeadersProvider: (HttpRequest<*>) -> Map<String, String>

    fun getTokens() = tokensProvider.getLocalTokens()

    suspend inline fun <reified Type>executeRequest(crossinline requestFactoryMethod: (Continuation<Type?>) -> HttpRequest<Type?>): Type? {
        return client.executeRequest {
            requestFactoryMethod(it)
        }
    }

    protected inline fun <reified HttpResponseWrapper, reified Type>parseResponseFromWrapper(value: JSONObject?): Type? where HttpResponseWrapper : IHttpResponseWrapper<Type> {
        val response: HttpResponseWrapper? = value?.toString()?.jsonToObject()
        return response?.getResult()
    }

    protected inline fun <reified Type>parseResponse(value: JSONObject?): Type? {
        return value?.toString()?.jsonToObject()
    }
}