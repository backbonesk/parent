package sk.backbone.parent.repositories.server.client

interface IHttpResponseWrapper<T> {
    fun getResult(): T?
}