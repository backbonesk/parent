package sk.backbone.parent.repositories.server.client

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class ClientRequestQueue(private val context: Context) {
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun addToRequestQueue(request: Request<*>) {
        requestQueue.add(request)
    }

    companion object {
        @Volatile
        private var INSTANCE: ClientRequestQueue? = null

        fun getInstance(context: Context) = synchronized(this) {
            INSTANCE ?: ClientRequestQueue(context).also { INSTANCE = it }
        }
    }
}