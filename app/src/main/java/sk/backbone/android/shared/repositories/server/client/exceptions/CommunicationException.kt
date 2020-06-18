package sk.backbone.android.shared.repositories.server.client.exceptions

import com.android.volley.VolleyError

class CommunicationException(volleyError: VolleyError) : BaseHttpException(volleyError)