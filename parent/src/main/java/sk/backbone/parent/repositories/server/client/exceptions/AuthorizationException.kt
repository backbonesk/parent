package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class AuthorizationException(volleyError: VolleyError) : ParentHttpException(volleyError)