package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class BadGatewayException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
