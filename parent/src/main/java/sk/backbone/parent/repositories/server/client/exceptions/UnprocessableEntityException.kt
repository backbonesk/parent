package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class UnprocessableEntityException @JvmOverloads constructor(volleyError: VolleyError? = null) : ParentHttpException(volleyError){

}