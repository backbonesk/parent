package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class ExpectationFailedException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
