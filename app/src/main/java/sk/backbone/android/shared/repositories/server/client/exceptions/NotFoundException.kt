package sk.backbone.android.shared.repositories.server.client.exceptions

import com.android.volley.VolleyError

class NotFoundException(volleyError: VolleyError) : BaseHttpException(volleyError)