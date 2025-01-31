package com.example.market.data

import androidx.compose.ui.geometry.isEmpty
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import com.google.gson.JsonObject
import com.google.gson.JsonParseException

class ProgressDeserializer : JsonDeserializer<String?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): String? {
        if (json == null || json.isJsonNull) return null

        // If "progress" is a JSON primitive, return its string value
        if (json.isJsonPrimitive) {
            return json.asString
        }
        // If "progress" is an object (like {}), return "0" or "0.0"
        if (json.isJsonObject) {
            return "0.0"
        }
        return null
    }
}