package sk.backbone.android.shared.repositories.server.client

import com.google.gson.ExclusionStrategy
import org.json.JSONObject
import sk.backbone.android.shared.repositories.server.client.exceptions.IExceptionDescriptionProvider
import kotlin.coroutines.Continuation

class RequestParameters<Type>(
    val continuation: Continuation<Type?>,
    val requestMethod: Int,
    val schema: String,
    val serverAddress: String,
    val apiVersion: String,
    val endpoint: String,
    val queryParameters: Map<String, String?>?,
    val body: Any?,
    val parseSuccessResponse: (JSONObject?) -> Type?,
    val errorParser: IExceptionDescriptionProvider,
    val bodyExclusionStrategy: ExclusionStrategy? = null,
    val additionalHeaders: Map<String, String?> = mapOf()
)