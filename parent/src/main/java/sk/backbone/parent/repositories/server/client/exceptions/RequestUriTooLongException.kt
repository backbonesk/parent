package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class RequestUriTooLongException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
