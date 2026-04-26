package projjson.model

class JsonPrimitive(val value: Any?) : JsonValue() {

    override fun accept(visitor: (JsonValue) -> Unit) {
        visitor(this)
    }

    override fun toString(): String {
        return when (value) {
            null -> "null"
            is String -> "\"${escape(value)}\""
            else -> value.toString()
        }
    }

    private fun escape(str: String): String {
        return str
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
    }
}
