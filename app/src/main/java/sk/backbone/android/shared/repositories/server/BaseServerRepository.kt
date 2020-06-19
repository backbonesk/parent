package sk.backbone.android.shared.repositories.server

import android.content.Context
import sk.backbone.android.shared.repositories.server.client.HttpClient
import sk.backbone.android.shared.repositories.server.client.HttpRequest
import sk.backbone.android.shared.repositories.server.client.ITokensProvider
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
}