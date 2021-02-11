package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class NotImplementedException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
