package sk.backbone.parent.execution

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sk.backbone.parent.execution.scopes.Scopes
import sk.backbone.parent.repositories.server.client.exceptions.CommunicationException
import sk.backbone.parent.repositories.server.client.exceptions.IExceptionDescriptionProvider

abstract class ParentExecutor<T>(executorParams: ExecutorParams) {
    abstract val logToFirebase: Boolean
    abstract val dialogProvider: IExecutorDialogProvider
    abstract val exceptionDescriptionProvider: IExceptionDescriptionProvider

    open var retryEnabled = false
    open var retryInfinitely = false

    var notifyUiOnError = true
    var showProgressDialog: Boolean = false
    var ioOperation: (suspend () -> T)? = null

    var defaultOperationOnSuccess: (suspend (T?) -> Unit)? = null
    var defaultOperationOnUnsuccessfulAttempt: (suspend (Throwable) -> Unit)? = null
    var defaultOperationOnFailure: (suspend (Throwable) -> Unit)? = null
    var defaultOperationOnFinished: (suspend () -> Unit)? = null

    var uiOperationOnSuccess: ((T?) -> Unit)? = null
    var uiOperationOnUnsuccessfulAttempt: ((Throwable) -> Unit)? = null
    var uiOperationOnFailure: ((Throwable) -> Unit)? = null
    var uiOperationOnFinished: (() -> Unit)? = null

    var errorDialogDefaultOperation: (() -> Unit)? = null

    var retryIntervalMillisecond: Long = 0
    var repeatInterval: Long = 0
    var startDelay: Long = 0
    var maxRetries: Int = 3

    protected var uiNotificationOnError: (() -> Unit)? = null
    private var recentJob: Job? = null

    var wasSuccessful = false
    var currentRepeatCount = 0
    var firstRun = true
    var isFinished = false
    var lastError: Throwable? = null
    var wasCanceled: Boolean = false
    var isLoopingInfinitely = false
    private var isRunning = false

    val isExecuting get() = isRunning && shouldContinue()

    protected val rootView: ViewGroup? = executorParams.rootView
    protected val scopes: Scopes = executorParams.scopes
    protected val context: Context = executorParams.context

    private fun shouldContinue(): Boolean {
        return ((!wasSuccessful && ( (retryEnabled && retryInfinitely) ||
                                    (retryEnabled && (currentRepeatCount < maxRetries)) ||
                                     firstRun))
                && !isFinished) || (isLoopingInfinitely && !wasCanceled && !isFinished)
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
        isRunning = true

        var loadingDialog: AlertDialog? = null

        if(showProgressDialog){
            loadingDialog = dialogProvider.showLoadingDialog(context)
        }

        cancel()

        recentJob = scopes.default.launch {
            delay(startDelay)

            while (shouldContinue()) {
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

                    if(isLoopingInfinitely){
                        delay(repeatInterval)
                    } else {
                        break
                    }
                }
                catch (canceled: ParentCancellationException){
                    wasCanceled = true
                }
                catch (throwable: Throwable) {
                    Log.e("ExecutionFailed", this.javaClass.name, throwable)

                    val exception = mapException(throwable)

                    if(logToFirebase){
                        FirebaseCrashlytics.getInstance().recordException(exception)
                    }

                    lastError = exception

                    if(!handleExceptionMiddleware(exception)){
                        retryEnabled = retryEnabled && exception is CommunicationException
                        uiNotificationOnError = {
                            dialogProvider.showDialog(context, exceptionDescriptionProvider.getDescription(context, exception), neutralButton = dialogProvider.getDefaultNeutralButton(context, errorDialogDefaultOperation))
                        }
                    }

                    withContext(scopes.default.coroutineContext){
                        defaultOperationOnUnsuccessfulAttempt?.invoke(exception)
                    }

                    withContext(scopes.ui.coroutineContext){
                        if(currentRepeatCount == 1 && notifyUiOnError){
                            uiNotificationOnError?.invoke()
                        }
                        uiOperationOnUnsuccessfulAttempt?.invoke(exception)
                    }

                    if(retryEnabled || isLoopingInfinitely) {
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

            isRunning = false
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

    /***
     * Override this and preprocess exception for caller. You can cast or map exception to different one if you need.
     ***/
    protected open fun mapException(throwable: Throwable): Throwable{
        return throwable
    }
}