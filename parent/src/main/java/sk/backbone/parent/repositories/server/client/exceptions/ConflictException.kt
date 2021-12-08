package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class ConflictException(volleyError: VolleyError) : ParentHttpException(volleyError)