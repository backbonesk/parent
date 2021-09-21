package sk.backbone.parent.utils

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun getSha256Digest(value: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val bytes =  digest.digest(value.toByteArray(StandardCharsets.UTF_8))
    var hash = ""

    for(byte in bytes){
        val part = String.format("%02X", byte)
        hash = "$hash$part"
    }

    return hash.lowercase(Locale.getDefault())
}

fun generateHmac256Signature(digest: String, secret: String): String {
    val algorithm = "HmacSHA256"
    val sha256Hmac = Mac.getInstance(algorithm)
    val secretKey = SecretKeySpec(secret.toByteArray(), algorithm)
    sha256Hmac.init(secretKey)
    val signatureBytes = sha256Hmac.doFinal(digest.toByteArray())

    var signature = ""

    for(byte in signatureBytes){
        val part = String.format("%02X", byte).lowercase()
        signature = "$signature$part"
    }

    return signature
}