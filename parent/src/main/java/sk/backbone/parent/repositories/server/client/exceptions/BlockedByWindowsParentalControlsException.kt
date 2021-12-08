package sk.backbone.parent.repositories.server.client.exceptions

import com.android.volley.VolleyError

class BlockedByWindowsParentalControlsException(volleyError: VolleyError) : ParentHttpException(volleyError) {

}
