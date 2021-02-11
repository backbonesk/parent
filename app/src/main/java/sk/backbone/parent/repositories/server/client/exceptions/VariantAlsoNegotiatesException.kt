package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class VariantAlsoNegotiatesException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
