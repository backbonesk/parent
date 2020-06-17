package sk.backbone.android.shared.utils

import com.google.gson.ExclusionStrategy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

fun Any?.toJsonString(exclusionStrategy: ExclusionStrategy? = null): String {
    val builder = GsonBuilder()

    if(exclusionStrategy != null){
        builder.setExclusionStrategies(exclusionStrategy)
    }

    return builder.setDateFormat(iso8061Format).create().toJson(this)
}

inline fun <reified T>String.jsonToObject(exclusionStrategy: ExclusionStrategy? = null) : T {
    val builder = GsonBuilder()

    if(exclusionStrategy != null){
        builder.setExclusionStrategies(exclusionStrategy)
    }

    return builder.setDateFormat(iso8061Format).create().fromJson(this, object: TypeToken<T>() {}.type)
}