package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class RequestRangeNotSatisfiableException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
