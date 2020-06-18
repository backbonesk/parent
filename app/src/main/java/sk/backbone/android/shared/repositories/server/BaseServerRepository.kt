package sk.backbone.android.shared.repositories.server

import android.content.Context
import sk.backbone.android.shared.repositories.server.client.BaseHttpRequest
import sk.backbone.android.shared.repositories.server.client.HttpClient
import sk.backbone.android.shared.repositories.server.client.ITokensProvider
import kotlin.coroutines.Continuation

abstract class BaseServerRepository<TokensWrapper>(val context: Context, val tokensProvider: ITokensProvider<TokensWrapper>){
    val client = HttpClient(context)

    fun getTokens() = tokensProvider.getLocalTokens()

    suspend inline fun <reified Type>executeRequest(crossinline requestFactoryMethod: (Continuation<Type?>) -> BaseHttpRequest<Type?>): Type?{
        return client.executeRequest {
            requestFactoryMethod(it)
        }
    }
}