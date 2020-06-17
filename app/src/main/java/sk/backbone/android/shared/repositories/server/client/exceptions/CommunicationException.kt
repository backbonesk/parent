package sk.backbone.android.shared.repositories.server.client.exceptions

import com.android.volley.VolleyError

class CommunicationException(volleyError: VolleyError, errorParser: IExceptionsErrorParser) : BaseHttpException(volleyError, errorParser)