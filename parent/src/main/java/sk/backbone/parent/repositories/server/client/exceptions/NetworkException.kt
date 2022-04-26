package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class NetworkException(volleyError: VolleyError) : ParentHttpException(volleyError)