package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class NotExtendedException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
