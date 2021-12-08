package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class PayloadTooLargeException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
