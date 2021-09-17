package sk.backbone.parent.application

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import sk.backbone.parent.execution.Scopes
import sk.backbone.parent.models.IModelsProvider

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
abstract class ParentFcmService<ModelsProvider: IModelsProvider> : FirebaseMessagingService(), IModelsProvider {
    val scopes = Scopes()
    abstract val modelsProvider: ModelsProvider
    override val context: Context get() = this

    override fun onDestroy() {
        scopes.cancel()
        super.onDestroy()
    }
}