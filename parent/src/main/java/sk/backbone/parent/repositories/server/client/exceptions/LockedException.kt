package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class LockedException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
