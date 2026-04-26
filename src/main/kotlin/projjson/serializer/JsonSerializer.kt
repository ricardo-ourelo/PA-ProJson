package projjson.serializer

import projjson.model.*

class JsonSerializer {

    fun serialize(value: JsonValue): String {
        return when (value) {
            is JsonPrimitive -> format(value.value)

            is JsonObject -> value.getProperties().entries.joinToString(
                prefix = "{", postfix = "}"
            ) { (k, v) -> "\"$k\": ${serialize(v)}" }

            is JsonArray -> value.getElements().joinToString(
                prefix = "[", postfix = "]"
            ) { serialize(it) }
        }
    }

    private fun format(value: Any?): String = when (value) {
        null -> "null"
        is String -> "\"$value\""
        else -> value.toString()
    }
}