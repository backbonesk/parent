package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class ClientClosedRequestException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
