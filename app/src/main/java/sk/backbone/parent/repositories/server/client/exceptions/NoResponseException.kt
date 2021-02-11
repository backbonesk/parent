package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class NoResponseException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
