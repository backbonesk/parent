package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class ServiceUnavailableException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
