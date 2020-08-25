package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class ForbiddenException(volleyError: VolleyError) : ParentHttpException(volleyError)