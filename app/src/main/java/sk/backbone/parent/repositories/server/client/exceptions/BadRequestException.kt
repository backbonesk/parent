package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class BadRequestException(volleyError: VolleyError) : ParentHttpException(volleyError)
