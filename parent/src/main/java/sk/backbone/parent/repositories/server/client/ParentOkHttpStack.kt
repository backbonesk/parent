package sk.backbone.parent.repositories.server.client

import com.android.volley.AuthFailureError
import com.android.volley.Header
import com.android.volley.Request
import com.android.volley.toolbox.BaseHttpStack
import com.android.volley.toolbox.HttpResponse
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request.Builder
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import sk.backbone.parent.utils.notNullValuesOnly
import java.io.IOException
import java.io.InputStream

abstract class ParentOkHttpStack : BaseHttpStack() {

    abstract val okHttpClient: OkHttpClient

    @Throws(IOException::class, AuthFailureError::class)
    override fun executeRequest(request: Request<*>, additionalHeaders: Map<String, String>): HttpResponse {
        val okHttpClient = okHttpClient.newBuilder().build()

        val okHttpRequestBuilder = Builder()
        okHttpRequestBuilder.url(request.url)

        request.headers.notNullValuesOnly().forEach { header -> okHttpRequestBuilder.addHeader(header.key, header.value) }
        additionalHeaders.notNullValuesOnly().forEach { header -> okHttpRequestBuilder.addHeader(header.key, header.value) }

        setConnectionParametersForRequest(okHttpRequestBuilder, request)

        val okHttpRequest = okHttpRequestBuilder.build()
        val okHttpCall = okHttpClient.newCall(okHttpRequest)
        val okHttpResponse = okHttpCall.execute()

        val code = okHttpResponse.code
        val body = okHttpResponse.body
        val content: InputStream? = body?.byteStream()
        val contentLength = body?.contentLength()?.toInt() ?: 0
        val responseHeaders: List<Header> = mapHeaders(okHttpResponse.headers)

        return HttpResponse(code, responseHeaders, contentLength, content)
    }

    private fun mapHeaders(responseHeaders: Headers): List<Header> {
        return responseHeaders.map { header -> Header(header.first, header.second) }
    }

    companion object {
        @Throws(AuthFailureError::class)
        private fun setConnectionParametersForRequest(builder: Builder, request: Request<*>) {
            val requestBody = createRequestBody(request)

            when (request.method) {
                Request.Method.DEPRECATED_GET_OR_POST -> {
                    // Ensure backwards compatibility.  Volley assumes a request with a null body is a GET.
                    if (request.body != null) {
                        builder.post(requestBody)
                    }
                }

                Request.Method.GET -> builder.get()
                Request.Method.DELETE -> builder.delete(requestBody)
                Request.Method.POST -> builder.post(requestBody)
                Request.Method.PUT -> builder.put(requestBody)
                Request.Method.HEAD -> builder.head()
                Request.Method.OPTIONS -> builder.method("OPTIONS", null)
                Request.Method.TRACE -> builder.method("TRACE", null)
                Request.Method.PATCH -> builder.patch(requestBody)
                else -> throw IllegalStateException("Unknown method type.")
            }
        }

        @Throws(AuthFailureError::class)
        private fun createRequestBody(request: Request<*>): RequestBody {
            return (request.body ?: ByteArray(0)).toRequestBody(request.bodyContentType.toMediaType())
        }
    }
}