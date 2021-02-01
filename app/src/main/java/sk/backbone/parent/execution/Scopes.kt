package sk.backbone.parent.execution

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

class Scopes {
    private val uiJob = SupervisorJob()
    val ui: CoroutineScope get() = CoroutineScope(Dispatchers.Main + uiJob)

    private val ioJob = SupervisorJob()
    val io: CoroutineScope get() = CoroutineScope(Dispatchers.IO + ioJob)

    private val defaultJob = SupervisorJob()
    val default: CoroutineScope get() = CoroutineScope(Dispatchers.Default + defaultJob)

    fun cancelJobs(){
        uiJob.cancelChildren()
        ioJob.cancelChildren()
        defaultJob.cancelChildren()
    }
}