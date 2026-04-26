package projjson.model

class JsonObject(
    private val properties: MutableMap<String, JsonValue> = mutableMapOf()
) : JsonValue() {

    fun set(name: String, value: Any?) {
        require(name.isNotBlank())
        properties[name] = JsonPrimitive(value)
    }

    fun get(name: String): JsonValue? {
        return properties[name]
    }

    fun remove(name: String) {
        properties.remove(name)
    }

    //Ver todas propriedades (read-only)
    fun getProperties(): Map<String, JsonValue> = properties

    override fun accept(visitor: (JsonValue) -> Unit) {
        visitor(this)
        properties.values.forEach { it.accept(visitor) }
    }

    override fun toString(): String {
        return properties.entries.joinToString(
            prefix = "{", postfix = "}"
        ) { (k, v) -> "\"$k\": ${v.toString()}" }
    }
}