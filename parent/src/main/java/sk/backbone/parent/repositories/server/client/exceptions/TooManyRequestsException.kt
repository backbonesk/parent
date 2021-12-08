package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class TooManyRequestsException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
