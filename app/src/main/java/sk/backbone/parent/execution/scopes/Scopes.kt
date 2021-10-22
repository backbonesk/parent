package sk.backbone.parent.execution.scopes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class Scopes {
    private var uiJob = SupervisorJob()
    val ui: CoroutineScope get() = CoroutineScope(Dispatchers.Main + uiJob)

    private var ioJob = SupervisorJob()
    val io: CoroutineScope get() = CoroutineScope(Dispatchers.IO + ioJob)

    private var defaultJob = SupervisorJob()
    val default: CoroutineScope get() = CoroutineScope(Dispatchers.Default + defaultJob)

    fun cancel(){
        defaultJob.cancel()
        uiJob.cancel()
        ioJob.cancel()
    }
}

