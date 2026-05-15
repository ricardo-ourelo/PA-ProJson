package projjson.model

/**
 * Representa um objeto JSON.
 *
 * Um JsonObject armazena pares:
 *
 * chave -> JsonValue
 *
 * Implementa o padrão Composite,
 * permitindo árvores JSON hierárquicas.
 */
class JsonObject(
    private val properties: MutableMap<String, JsonValue> = mutableMapOf()
) : JsonValue() {

    /**
     * Adiciona ou altera uma propriedade JSON.
     *
     * O valor é automaticamente convertido
     * para JsonValue.
     *
     * @param name nome da propriedade
     * @param value valor associado
     */
    fun set(name: String, value: Any?) {

        require(name.isNotBlank()) {
            "Property name cannot be blank"
        }

        properties[name] = wrap(value)
    }

    /**
     * Obtém valor da propriedade.
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
     * Devolve todas as propriedades
     * em modo read-only.
     *
     * @return mapa de propriedades JSON
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