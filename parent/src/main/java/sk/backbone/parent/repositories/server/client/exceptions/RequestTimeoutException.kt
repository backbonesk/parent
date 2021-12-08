package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class RequestTimeoutException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
