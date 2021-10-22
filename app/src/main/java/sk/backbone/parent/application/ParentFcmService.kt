package sk.backbone.parent.application

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import sk.backbone.parent.execution.IExecutioner
import sk.backbone.parent.execution.Scopes

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
abstract class ParentFcmService : FirebaseMessagingService(), IExecutioner {
    override var scopes: Scopes = Scopes()

    override fun onDestroy() {
        scopes.cancel()
        super.onDestroy()
    }
}