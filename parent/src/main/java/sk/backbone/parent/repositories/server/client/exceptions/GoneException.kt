package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class GoneException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
