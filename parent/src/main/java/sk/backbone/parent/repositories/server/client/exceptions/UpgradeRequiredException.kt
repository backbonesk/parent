package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class UpgradeRequiredException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
