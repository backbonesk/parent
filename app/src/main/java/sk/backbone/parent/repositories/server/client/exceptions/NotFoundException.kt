package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class NotFoundException(volleyError: VolleyError) : ParentHttpException(volleyError)