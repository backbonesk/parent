package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class NotAcceptableException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
