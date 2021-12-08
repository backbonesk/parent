package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class LengthRequiredException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
