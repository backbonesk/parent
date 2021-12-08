package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class BandwidthLimitExceededException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
