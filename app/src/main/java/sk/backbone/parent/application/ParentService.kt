package sk.backbone.parent.application

import android.app.Service
import sk.backbone.parent.execution.IExecutioner
import sk.backbone.parent.execution.Scopes

abstract class ParentService : Service(), IExecutioner {
    override var scopes: Scopes = Scopes()

    override fun onDestroy() {
        scopes.cancel()
        super.onDestroy()
    }
}