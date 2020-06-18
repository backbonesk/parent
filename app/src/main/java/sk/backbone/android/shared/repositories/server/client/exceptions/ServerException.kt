package sk.backbone.android.shared.repositories.server.client.exceptions

import com.android.volley.VolleyError

class ServerException(volleyError: VolleyError) : BaseHttpException(volleyError)