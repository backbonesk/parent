package sk.backbone.parent.execution

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class Scopes {
    private var uiJob = SupervisorJob()
    val ui: CoroutineScope get() = CoroutineScope(Dispatchers.Main + uiJob)

    private var ioJob = SupervisorJob()
    val io: CoroutineScope get() = CoroutineScope(Dispatchers.IO + ioJob)

    private var defaultJob = SupervisorJob()
    val default: CoroutineScope get() = CoroutineScope(Dispatchers.Default + defaultJob)

    fun quitJobs(){
        defaultJob.cancel()
        uiJob.cancel()
        ioJob.cancel()
    }

    fun stopJobs(){
        defaultJob.cancel()
        uiJob.cancel()
        ioJob.cancel()

        defaultJob  = SupervisorJob()
        uiJob = SupervisorJob()
        ioJob = SupervisorJob()
    }
}