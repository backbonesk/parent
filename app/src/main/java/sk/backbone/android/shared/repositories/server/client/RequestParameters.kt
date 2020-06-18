package sk.backbone.android.shared.repositories.server.client

import com.google.gson.ExclusionStrategy
import sk.backbone.android.shared.repositories.server.client.exceptions.IExceptionDescriptionProvider

class RequestParameters<Type>(
    val requestMethod: Int,
    val schema: String,
    val serverAddress: String,
    val apiVersion: String,
    val endpoint: String,
    val queryParameters: Map<String, String?>?,
    val body: Any?,
    val errorParser: IExceptionDescriptionProvider,
    val bodyExclusionStrategy: ExclusionStrategy? = null,
    val additionalHeaders: Map<String, String?> = mapOf()
)