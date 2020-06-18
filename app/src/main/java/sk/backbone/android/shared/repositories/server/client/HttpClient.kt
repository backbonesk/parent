package sk.backbone.android.shared.repositories.server.client

import android.content.Context
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

class HttpClient(private val context: Context) {
    suspend fun <Type>executeRequest(requestFactory: (Continuation<Type>) -> HttpRequest<Type>): Type? {
        return suspendCoroutine { continuation ->
            ClientRequestQueue.getInstance(context).addToRequestQueue(requestFactory(continuation))
        }
    }
}