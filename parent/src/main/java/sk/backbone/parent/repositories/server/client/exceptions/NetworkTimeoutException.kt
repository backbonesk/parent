package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class NetworkTimeoutException(volleyError: VolleyError) : ParentHttpException(volleyError)