package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class UnsupportedMediaTypeException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
