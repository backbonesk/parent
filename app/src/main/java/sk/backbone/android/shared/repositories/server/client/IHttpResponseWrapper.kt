package sk.backbone.android.shared.repositories.server.client

interface IHttpResponseWrapper<T> {
    fun getResult(): T?
}