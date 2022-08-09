package sk.backbone.parent.application.services

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import sk.backbone.parent.execution.IExecutioner
import sk.backbone.parent.execution.scopes.ServiceScopes
import javax.inject.Inject

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
abstract class ParentFcmService : FirebaseMessagingService(), IExecutioner<ServiceScopes> {
    @Inject override lateinit var scopes: ServiceScopes

    override fun onDestroy() {
        scopes.cancel()
        super.onDestroy()
    }
}