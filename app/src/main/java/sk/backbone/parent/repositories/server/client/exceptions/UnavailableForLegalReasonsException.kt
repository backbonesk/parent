package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class UnavailableForLegalReasonsException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
