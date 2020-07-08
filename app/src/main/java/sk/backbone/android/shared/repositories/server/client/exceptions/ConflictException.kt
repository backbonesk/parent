package sk.backbone.android.shared.repositories.server.client.exceptions

import com.android.volley.VolleyError

class ConflictException(volleyError: VolleyError) : BaseHttpException(volleyError)