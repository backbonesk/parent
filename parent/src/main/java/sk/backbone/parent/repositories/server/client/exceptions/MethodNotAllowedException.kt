package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class MethodNotAllowedException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
