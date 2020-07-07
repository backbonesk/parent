package sk.backbone.android.shared.execution

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sk.backbone.android.shared.repositories.server.client.exceptions.*

abstract class BaseExecutor<T>(executorParams: ExecutorParams) {
    abstract val logToFirebase: Boolean
    abstract val dialogProvider: IExecutorDialogProvider
    abstract val exceptionDescriptionProvider: IExceptionDescriptionProvider

    open var repeatUntilSuccess: Boolean = true
    open var repeatInfinitely = false

    var showProgressDialog: Boolean = false
    var ioOperation: (suspend () -> T)? = null
    var uiOperationOnSuccess: ((T?) -> Unit)? = null
    var uiOperationOnUnsuccessfulAttempt: (() -> Unit)? = null
    var uiOperationOnFailure: (() -> Unit)? = null
    var uiOperationOnFinished: (() -> Unit)? = null
    var retryIntervalMillisecond: Long = 5000
    var maxRepeats: Int = 5
    var wasSuccessful = false
    var currentRepeatCount = 0
    var firstRun = true
    var isFinished = false
    var uiNotificationOnError: (() -> Unit)? = null

    protected val rootView: ViewGroup = executorParams.rootView
    protected val scopes: Scopes = executorParams.scopes
    protected val context: Context = executorParams.context

    fun isExecuting(): Boolean {
        return (!wasSuccessful && ((repeatUntilSuccess && repeatInfinitely) || (repeatUntilSuccess && (currentRepeatCount < maxRepeats)) || firstRun)) && !isFinished
    }

    final fun execute() {
        var loadingDialog: AlertDialog? = null

        if(showProgressDialog){
            loadingDialog = dialogProvider.showLoadingDialog(context)
        }

        scopes.default.launch {
            while (isExecuting()) {
                firstRun = false
                currentRepeatCount++

                try {
                    val ioOperationResult = withContext(scopes.io.coroutineContext){
                        ioOperation?.invoke()
                    }

                    scopes.ui.launch{
                        loadingDialog?.dismiss()
                        uiOperationOnSuccess?.invoke(ioOperationResult)
                        uiOperationOnFinished?.invoke()
                    }

                    repeatUntilSuccess = false
                    wasSuccessful = true

                    return@launch
                }
                catch (throwable: Throwable) {
                    Log.e("ExecutionFailed", this.javaClass.name, throwable)

                    if(currentRepeatCount == 3){
                        uiNotificationOnError?.invoke()
                    }

                    if(logToFirebase){
                        FirebaseCrashlytics.getInstance().recordException(throwable)
                    }

                    withContext(scopes.ui.coroutineContext){
                        uiOperationOnUnsuccessfulAttempt?.invoke()
                    }

                    when(throwable) {
                        is AuthorizationException -> {
                            handleAuthorizationException(throwable)
                        }
                        is CommunicationException -> {
                            handleCommunicationException(throwable)
                        }
                        is ForbiddenException -> {
                            handleForbiddenException(throwable)
                        }
                        is ServerException -> {
                            handleServerException(throwable)
                        }
                        is ValidationException -> {
                            handleValidationException(throwable)
                        }
                        else -> {
                            handleUnknownException(throwable)
                        }
                    }

                    if(repeatUntilSuccess) {
                        delay(retryIntervalMillisecond)
                    }
                }
            }
            if(!wasSuccessful){
                withContext(scopes.ui.coroutineContext){
                    loadingDialog?.dismiss()
                    uiNotificationOnError?.invoke()
                    uiOperationOnFailure?.invoke()
                    uiOperationOnFinished?.invoke()
                }
            }

            isFinished = true
        }
    }

    protected open fun handleAuthorizationException(exception: AuthorizationException){
        repeatUntilSuccess = false
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exceptionDescriptionProvider.getDescription(context, exception))
        }
    }

    protected open fun handleCommunicationException(exception: CommunicationException){
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exceptionDescriptionProvider.getDescription(context, exception))
        }
    }

    protected open fun handleForbiddenException(exception: ForbiddenException){
        repeatUntilSuccess = false
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exceptionDescriptionProvider.getDescription(context, exception))
        }
    }

    protected open fun handlePaymentException(exception: PaymentException){
        repeatUntilSuccess = false
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exceptionDescriptionProvider.getDescription(context, exception))
        }
    }

    protected open fun handleServerException(exception: ServerException){
        repeatUntilSuccess = false
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exceptionDescriptionProvider.getDescription(context, exception))
        }
    }

    protected open fun handleValidationException(exception: ValidationException){
        repeatUntilSuccess = false
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exceptionDescriptionProvider.getDescription(context, exception))
        }
    }

    open fun handleUnknownException(throwable: Throwable) {
        repeatUntilSuccess = false
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exceptionDescriptionProvider.getDescription(context, throwable))
        }
    }
}