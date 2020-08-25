package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class ValidationException(volleyError: VolleyError) : ParentHttpException(volleyError)