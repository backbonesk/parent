package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class PaymentException(volleyError: VolleyError) : ParentHttpException(volleyError)

