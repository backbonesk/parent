package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class MisdirectedRequestException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
