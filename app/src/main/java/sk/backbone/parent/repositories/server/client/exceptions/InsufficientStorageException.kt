package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class InsufficientStorageException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
