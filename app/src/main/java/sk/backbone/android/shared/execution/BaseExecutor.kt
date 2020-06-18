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
    protected val rootView: ViewGroup = executorParams.rootView
    protected val scopes: Scopes = executorParams.scopes
    protected val context: Context = executorParams.context

    var showProgressDialog: Boolean = false
    var ioOperation: (suspend () -> T)? = null
    var uiOperationOnSuccess: ((T?) -> Unit)? = null
    var uiOperationOnFailure: (() -> Unit)? = null
    var uiOperationOnFinished: (() -> Unit)? = null
    open var repeatUntilSuccess: Boolean = true
    open var repeatInfinitely = false
    var retryIntervalMillisecond: Long = 5000
    var maxRepeats: Int = 5
    var wasSuccessful = false
    var currentRepeatIndex = 0
    var firstRun = true
    var isFinished = false
    var uiNotificationOnError: (() -> Unit)? = null
    abstract val logToFirebase: Boolean

    abstract val dialogProvider: IExecutorDialogProvider

    fun isExecuting(): Boolean {
        return (!wasSuccessful && ((repeatUntilSuccess && repeatInfinitely) || (repeatUntilSuccess && (currentRepeatIndex < maxRepeats)) || firstRun)) && !isFinished
    }

    final fun execute() {
        var loadingDialog: AlertDialog? = null

        if(showProgressDialog){
            loadingDialog = dialogProvider.showLoadingDialog(context)
        }

        scopes.default.launch {
            while (isExecuting()) {
                firstRun = false
                currentRepeatIndex++

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

                    if(logToFirebase){
                        FirebaseCrashlytics.getInstance().recordException(throwable)
                    }

                    withContext(scopes.ui.coroutineContext){
                        uiOperationOnFailure?.invoke()
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
                            if(!handleUnknownException(throwable)){
                                throw throwable
                            }
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
                    uiOperationOnFinished?.invoke()
                }
            }

            isFinished = true
        }
    }

    protected open fun handleAuthorizationException(exception: AuthorizationException){
        repeatUntilSuccess = false
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exception.getDescription(context))
        }
    }

    protected open fun handleCommunicationException(exception: CommunicationException){
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exception.getDescription(context))
        }
    }

    protected open fun handleForbiddenException(exception: ForbiddenException){
        repeatUntilSuccess = false
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exception.getDescription(context))
        }
    }

    protected open fun handlePaymentException(exception: PaymentException){
        repeatUntilSuccess = false
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exception.getDescription(context))
        }
    }

    protected open fun handleServerException(exception: ServerException){
        repeatUntilSuccess = false
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exception.getDescription(context))
        }
    }

    protected open fun handleValidationException(exception: ValidationException){
        repeatUntilSuccess = false
        uiNotificationOnError = {
            dialogProvider.showErrorDialog(context, exception.getDescription(context))
        }
    }

    open fun handleUnknownException(throwable: Throwable): Boolean {
        repeatUntilSuccess = false
        return false
    }
}