package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class TooEarlyException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
