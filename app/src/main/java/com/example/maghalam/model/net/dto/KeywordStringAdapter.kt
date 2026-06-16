package com.example.maghalam.model.net.dto

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class KeywordStringAdapter : JsonDeserializer<String>, JsonSerializer<String> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): String {
        return when {
            json == null || json is JsonNull -> ""
            json.isJsonArray -> json.asJsonArray
                .mapNotNull { element -> element.takeIf { !it.isJsonNull }?.asString?.trim() }
                .filter { it.isNotEmpty() }
                .joinToString(", ")
            json.isJsonPrimitive -> json.asString.orEmpty()
            else -> ""
        }
    }

    override fun serialize(
        src: String?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.orEmpty())
    }
}
