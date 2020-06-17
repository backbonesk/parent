package sk.backbone.android.shared.repositories.server.client.exceptions

import com.android.volley.VolleyError

class PaymentException(volleyError: VolleyError, errorParser: IExceptionsErrorParser) : BaseHttpException(volleyError, errorParser)

