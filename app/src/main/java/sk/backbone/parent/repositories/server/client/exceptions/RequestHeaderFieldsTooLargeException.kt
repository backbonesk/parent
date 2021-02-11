package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class RequestHeaderFieldsTooLargeException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
