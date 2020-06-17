package sk.backbone.android.shared.repositories.server.client.exceptions

interface IExceptionsErrorParser {
    fun getResponse(baseHttpException: BaseHttpException, responseBody: String?, statusCode: Int?): String
}
