package projjson.model

/**
 * Representa um objeto JSON.
 *
 * Exemplo:
 *
 * {
 *   "name": "Ana",
 *   "age": 25
 * }
 *
 * Guarda propriedades no formato:
 *
 * chave -> JsonValue
 */
class JsonObject(
    private val properties: MutableMap<String, JsonValue> = mutableMapOf()
) : JsonValue() {

    /**
     * Adiciona ou altera propriedade.
     *
     * Se o valor já for JsonValue,
     * é usado diretamente.
     *
     * Caso contrário,
     * é convertido para JsonPrimitive.
     */
    fun set(name: String, value: Any?) {

        // JSON não permite chave vazia
        require(name.isNotBlank()) {
            "Property name cannot be blank"
        }

        properties[name] = when (value) {
            is JsonValue -> value
            else -> JsonPrimitive(value)
        }
    }

    /**
     * Obtém valor de uma propriedade.
     */
    fun get(name: String): JsonValue? {
        return properties[name]
    }

    /**
     * Remove propriedade.
     */
    fun remove(name: String) {
        properties.remove(name)
    }

    /**
     * Remove todas as propriedades.
     */
    fun clear() {
        properties.clear()
    }

    /**
     * Verifica se objeto está vazio.
     */
    fun isEmpty(): Boolean {
        return properties.isEmpty()
    }

    /**
     * Número de propriedades.
     */
    fun size(): Int {
        return properties.size
    }

    /**
     * Verifica se contém chave.
     */
    fun containsKey(name: String): Boolean {
        return properties.containsKey(name)
    }

    /**
     * Devolve propriedades em modo read-only.
     */
    fun properties(): Map<String, JsonValue> = properties

    /**
     * Percorre recursivamente
     * toda a árvore JSON.
     *
     * Usa o padrão Visitor.
     */
    override fun accept(visitor: (JsonValue) -> Unit) {
        // Visita objeto atual
        visitor(this)
        // Visita propriedades internas
        properties.values.forEach {
            it.accept(visitor)
        }
    }

    /**
     * Converte objeto para texto JSON.
     */
    override fun toString(): String {

        return properties.entries.joinToString(
            prefix = "{",
            postfix = "}"
        ) { (key, value) ->

            "\"$key\": ${value.toString()}"
        }
    }
}