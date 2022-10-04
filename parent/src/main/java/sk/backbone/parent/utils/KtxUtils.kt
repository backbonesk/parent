package sk.backbone.parent.utils

import kotlinx.coroutines.CancellableContinuation

@SinceKotlin("1.3")
inline fun <reified T> CancellableContinuation<T>?.resumeWithExceptionIfActive(exception: Throwable) {
    if(this?.isActive == true){
        resumeWith(Result.failure(exception))
    }
}

@SinceKotlin("1.3")
inline fun <reified T> CancellableContinuation<T>?.resumeIfActive(value: T) {
    if(this?.isActive == true){
        resumeWith(Result.success(value))
    }
}
