package sk.backbone.parent.application

import android.app.Service
import sk.backbone.parent.execution.IExecutioner
import sk.backbone.parent.execution.scopes.Scopes
import sk.backbone.parent.execution.scopes.ServiceScopes
import javax.inject.Inject

abstract class ParentService : Service(), IExecutioner<ServiceScopes> {
    @Inject override lateinit var scopes: ServiceScopes

    override fun onDestroy() {
        scopes.cancel()
        super.onDestroy()
    }
}