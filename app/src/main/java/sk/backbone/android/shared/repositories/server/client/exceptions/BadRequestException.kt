package sk.backbone.android.shared.repositories.server.client.exceptions

import com.android.volley.VolleyError

class BadRequestException(volleyError: VolleyError) : BaseHttpException(volleyError)
