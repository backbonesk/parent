package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class NetworkConnectTimeoutErrorException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
