package sk.backbone.parent.application.services

import androidx.lifecycle.LifecycleService
import sk.backbone.parent.execution.IExecutioner
import sk.backbone.parent.execution.scopes.ServiceScopes
import javax.inject.Inject

abstract class ParentLifecycleService : LifecycleService(), IExecutioner<ServiceScopes> {
    @Inject override lateinit var scopes: ServiceScopes

    override fun onDestroy() {
        scopes.cancel()
        super.onDestroy()
    }
}