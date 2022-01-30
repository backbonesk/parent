package sk.backbone.parent.utils

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Any?.toJsonString(exclusionStrategy: ExclusionStrategy? = null): String {
    val builder = GsonBuilder()

    if(exclusionStrategy != null){
        builder.setExclusionStrategies(exclusionStrategy)
    }

    return builder.serializeNulls().setDateFormat(iso8601Format).registerTypeAdapter(Date::class.java, GsonUtcDateAdapter()).create().toJson(this)
}

inline fun <reified T>String.jsonToObject(exclusionStrategy: ExclusionStrategy? = null) : T {
    val builder = GsonBuilder()

    if(exclusionStrategy != null){
        builder.setExclusionStrategies(exclusionStrategy)
    }

    return builder.serializeNulls().create().fromJson(this, object: TypeToken<T>() {}.type)
}

class GsonUtcDateAdapter : JsonSerializer<Date?>, JsonDeserializer<Date?> {
    private val dateFormat: DateFormat

    init {
        dateFormat = SimpleDateFormat(iso8601FormatUtcStrict, Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(dateFormat.format(src!!))
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date? {
        return try {
            dateFormat.parse(json!!.asString)
        } catch (e: ParseException) {
            throw JsonParseException(e)
        }
    }
}