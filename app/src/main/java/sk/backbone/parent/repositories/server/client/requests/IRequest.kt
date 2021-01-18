package sk.backbone.parent.repositories.server.client.requests

import android.net.Uri
import androidx.core.net.toUri
import com.google.gson.ExclusionStrategy
import sk.backbone.parent.utils.getUrl
import kotlin.coroutines.Continuation

interface IRequest<Type, RequestBodyType> {
    val continuation: Continuation<Type?>
    val requestMethod: Int
    val schema: String
    val serverAddress: String
    val apiVersion: String
    val endpoint: String
    val queryParameters: List<Pair<String, String?>>? get() = null
    val body: Any? get() = null
    val formData: Map<String, String?>? get() = null
    val parseSuccessResponse: (RequestBodyType?) -> Type?
    val bodyExclusionStrategy: ExclusionStrategy? get() = null
    val additionalHeadersProvider: ((JsonArrayHttpRequest<*>) -> Map<String, String>?)
    val uri: Uri get() = getUrl(schema, serverAddress, apiVersion, endpoint, queryParameters).toUri()
}