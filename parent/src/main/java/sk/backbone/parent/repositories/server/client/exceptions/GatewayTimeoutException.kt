package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class GatewayTimeoutException @JvmOverloads constructor(volleyError: VolleyError? = null) : ParentHttpException(volleyError) {

}
