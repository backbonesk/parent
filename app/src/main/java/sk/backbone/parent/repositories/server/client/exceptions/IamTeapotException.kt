package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class IamTeapotException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
