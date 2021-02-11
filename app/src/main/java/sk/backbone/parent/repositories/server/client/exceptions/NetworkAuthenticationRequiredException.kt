package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class NetworkAuthenticationRequiredException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
