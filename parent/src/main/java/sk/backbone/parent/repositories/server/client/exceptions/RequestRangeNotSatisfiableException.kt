package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class RequestRangeNotSatisfiableException @JvmOverloads constructor(volleyError: VolleyError? = null) : ParentHttpException(volleyError) {

}
