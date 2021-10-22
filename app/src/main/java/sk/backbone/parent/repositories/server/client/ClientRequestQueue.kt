package sk.backbone.parent.repositories.server.client

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BaseHttpStack
import com.android.volley.toolbox.Volley

class ClientRequestQueue(private val context: Context, baseHttpStack: BaseHttpStack? = null) {
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext, baseHttpStack)
    }

    fun addToRequestQueue(request: Request<*>) {
        requestQueue.add(request)
    }
}