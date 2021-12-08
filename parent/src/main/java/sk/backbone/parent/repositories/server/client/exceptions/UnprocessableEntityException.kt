package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class UnprocessableEntityException(volleyError: VolleyError) : ParentHttpException(volleyError)