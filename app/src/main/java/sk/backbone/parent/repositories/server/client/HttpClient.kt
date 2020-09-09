package sk.backbone.parent.repositories.server.client

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.BaseHttpStack
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

class HttpClient(private val context: Context, private val baseHttpStack: BaseHttpStack? = null) {
    suspend fun <Type>executeRequest(requestFactory: (Continuation<Type>) -> Request<*>): Type? {
        return suspendCoroutine { continuation ->
            ClientRequestQueue.getInstance(context, baseHttpStack).addToRequestQueue(requestFactory(continuation))
        }
    }
}