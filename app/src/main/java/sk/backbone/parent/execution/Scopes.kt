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
        uiJob.cancel()
        ioJob.cancel()
        defaultJob.cancel()
    }

    fun stopJobs(){
        uiJob.cancel()
        ioJob.cancel()
        defaultJob.cancel()

        uiJob = SupervisorJob()
        ioJob = SupervisorJob()
        defaultJob  = SupervisorJob()
    }
}