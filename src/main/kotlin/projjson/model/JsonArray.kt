package projjson.model

class JsonArray(
    private val elements: MutableList<JsonValue> = mutableListOf()
) : JsonValue() {

    fun add(value: Any?) {
        elements.add(JsonPrimitive(value))
    }

    fun get(index: Int): JsonValue {
        return elements[index]
    }

    fun set(index: Int, value: Any?) {
        elements[index] = JsonPrimitive(value)
    }

    fun remove(index: Int) {
        elements.removeAt(index)
    }

    fun getElements(): List<JsonValue> = elements

    override fun accept(visitor: (JsonValue) -> Unit) {
        visitor(this)
        elements.forEach { it.accept(visitor) }
    }

    override fun toString(): String {
        return elements.joinToString(
            prefix = "[", postfix = "]"
        ) { it.toString() }
    }
}