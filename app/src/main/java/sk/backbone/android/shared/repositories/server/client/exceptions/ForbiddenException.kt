package sk.backbone.android.shared.repositories.server.client.exceptions

import com.android.volley.VolleyError

class ForbiddenException(volleyError: VolleyError, errorParser: IExceptionDescriptionProvider) : BaseHttpException(volleyError, errorParser)