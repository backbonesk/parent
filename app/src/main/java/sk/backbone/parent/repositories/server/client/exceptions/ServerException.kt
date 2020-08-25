package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class ServerException(volleyError: VolleyError) : ParentHttpException(volleyError)