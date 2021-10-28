package sk.backbone.parent.execution

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.*
import sk.backbone.parent.execution.scopes.Scopes
import sk.backbone.parent.repositories.server.client.exceptions.*

abstract class ParentExecutor<T>(executorParams: ExecutorParams) {
    abstract val logToFirebase: Boolean
    abstract val dialogProvider: IExecutorDialogProvider
    abstract val exceptionDescriptionProvider: IExceptionDescriptionProvider

    open var retryEnabled = true
    open var retryInfinitely = false

    var notifyUiOnError = true
    var showProgressDialog: Boolean = false
    var ioOperation: (suspend () -> T)? = null

    var defaultOperationOnSuccess: ((T?) -> Unit)? = null
    var defaultOperationOnUnsuccessfulAttempt: ((Throwable) -> Unit)? = null
    var defaultOperationOnFailure: ((Throwable) -> Unit)? = null
    var defaultOperationOnFinished: (() -> Unit)? = null

    var uiOperationOnSuccess: ((T?) -> Unit)? = null
    var uiOperationOnUnsuccessfulAttempt: ((Throwable) -> Unit)? = null
    var uiOperationOnFailure: ((Throwable) -> Unit)? = null
    var uiOperationOnFinished: (() -> Unit)? = null

    var retryIntervalMillisecond: Long = 5000
    var repeatInterval: Long = 0
    var startDelay: Long = 0
    var maxRetries: Int = 5

    protected var uiNotificationOnError: (() -> Unit)? = null
    private var recentJob: Job? = null
    var wasSuccessful = false
    var currentRepeatCount = 0
    var firstRun = true
    var isFinished = false
    var lastError: Throwable? = null
    var wasCanceled: Boolean = false
    var isLoopingInfinitely = false

    protected val rootView: ViewGroup? = executorParams.rootView
    protected val scopes: Scopes = executorParams.scopes
    protected val context: Context = executorParams.context

    fun isExecuting(): Boolean {
        return ((!wasSuccessful && ( (retryEnabled && retryInfinitely) ||
                                    (retryEnabled && (currentRepeatCount < maxRetries)) ||
                                     firstRun))
                && !isFinished) || (isLoopingInfinitely && !wasCanceled)
    }

    final fun execute() {
        uiNotificationOnError = null
        recentJob = null
        wasSuccessful = false
        currentRepeatCount = 0
        firstRun = true
        isFinished = false
        lastError = null
        wasCanceled = false

        var loadingDialog: AlertDialog? = null

        if(showProgressDialog){
            loadingDialog = dialogProvider.showLoadingDialog(context)
        }

        cancel()

        recentJob = scopes.default.launch {
            delay(startDelay)

            while (isExecuting()) {
                firstRun = false
                currentRepeatCount++

                try {
                    val ioOperationResult = withContext(scopes.io.coroutineContext){
                        ioOperation?.invoke()
                    }

                    withContext(scopes.default.coroutineContext){
                        defaultOperationOnSuccess?.invoke(ioOperationResult)
                    }

                    withContext(scopes.ui.coroutineContext){
                        loadingDialog?.dismiss()
                        uiOperationOnSuccess?.invoke(ioOperationResult)
                        uiOperationOnFinished?.invoke()
                    }

                    retryEnabled = false
                    wasSuccessful = true

                    if(retryInfinitely){
                        delay(repeatInterval)
                    }

                    return@launch
                }
                catch (canceled: ParentCancellationException){
                    Log.e("ExecutionFailed", this.javaClass.name, canceled)
                    wasCanceled = true
                }
                catch (throwable: Throwable) {
                    Log.e("ExecutionFailed", this.javaClass.name, throwable)

                    if(logToFirebase){
                        FirebaseCrashlytics.getInstance().recordException(throwable)
                    }

                    lastError = throwable

                    if(!handleExceptionMiddleware(throwable)){
                        retryEnabled = retryEnabled && throwable is CommunicationException
                        uiNotificationOnError = {
                            dialogProvider.showErrorDialog(context, exceptionDescriptionProvider.getDescription(context, throwable))
                        }
                    }

                    withContext(scopes.default.coroutineContext){
                        defaultOperationOnUnsuccessfulAttempt?.invoke(throwable)
                    }

                    withContext(scopes.ui.coroutineContext){
                        if(currentRepeatCount == 1 && notifyUiOnError){
                            uiNotificationOnError?.invoke()
                        }
                        uiOperationOnUnsuccessfulAttempt?.invoke(throwable)
                    }

                    if(retryEnabled) {
                        delay(retryIntervalMillisecond)
                    }
                }
            }
            if(!wasSuccessful && !wasCanceled){

                withContext(scopes.ui.coroutineContext){
                    loadingDialog?.dismiss()
                    lastError?.let { uiOperationOnFailure?.invoke(it) }
                    uiOperationOnFinished?.invoke()
                }

                withContext(scopes.default.coroutineContext){
                    lastError?.let { defaultOperationOnFailure?.invoke(it) }
                    defaultOperationOnFinished?.invoke()
                }
            }

            isFinished = true
        }
    }

    fun cancel(){
        recentJob?.cancel(ParentCancellationException())
    }

    /***
     * Override this and return true to change default behavior.
     ***/
    protected open fun handleExceptionMiddleware(throwable: Throwable): Boolean {
        return false
    }
}