package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class CommunicationException(volleyError: VolleyError) : ParentHttpException(volleyError)