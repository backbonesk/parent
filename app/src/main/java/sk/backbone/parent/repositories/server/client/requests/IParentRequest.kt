package sk.backbone.parent.repositories.server.client.requests

import android.net.Uri
import androidx.core.net.toUri
import com.google.gson.ExclusionStrategy
import sk.backbone.parent.utils.getUrl
import java.net.URLEncoder
import kotlin.coroutines.Continuation

interface IParentRequest<ReturnType, RequestBodyType> {
    val continuation: Continuation<ReturnType?>
    val requestMethod: Int
    val schema: String
    val serverAddress: String
    val apiVersion: String
    val endpoint: String
    val queryParameters: List<Pair<String, String?>>? get() = null
    val body: Any? get() = null
    val formData: Map<String, String?>? get() = null
    val parseSuccessResponse: (RequestBodyType?) -> ReturnType?
    val bodyExclusionStrategy: ExclusionStrategy? get() = null
    val additionalHeadersProvider: ((JsonArrayHttpRequest<*>) -> Map<String, String>?)
    val uri: Uri get() = getUrl(schema, serverAddress, apiVersion, endpoint, queryParameters).toUri()

    val requestQueryParametersEncoded: String? get() {
        return queryParameters?.joinToString("&") { parameter ->
            "${parameter.first}=${URLEncoder.encode(parameter.second, "utf-8")}"
        }
    }
}