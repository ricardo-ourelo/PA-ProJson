package projjson.model

/**
 * Representa um array JSON.
 */
class JsonArray(
    private val elements: MutableList<JsonValue> = mutableListOf()
) : JsonValue() {

    /**
     * Adiciona elemento ao array.
     */
    fun add(value: Any?) {
        elements.add(
            when (value) {
                is JsonValue -> value
                else -> JsonPrimitive(value)
            }
        )
    }

    /**
     * Obtém elemento pelo índice.
     *
     * Garante que o índice é válido.
     */
    fun get(index: Int): JsonValue {

        require(index in elements.indices) {
            "Invalid index"
        }

        return elements[index]
    }

    /**
     * Substitui elemento do array.
     */
    fun set(index: Int, value: Any?) {
        require(index in elements.indices) {
            "Invalid index"
        }
        elements[index] =
            when (value) {
                is JsonValue -> value
                else -> JsonPrimitive(value)
            }
    }

    /**
     * Remove elemento do array.
     */
    fun remove(index: Int) {
        require(index in elements.indices) {
            "Invalid index"
        }
        elements.removeAt(index)
    }

    /**
     * Remove todos os elementos.
     */
    fun clear() {
        elements.clear()
    }

    /**
     * Verifica se o array está vazio.
     */
    fun isEmpty(): Boolean {
        return elements.isEmpty()
    }

    /**
     * Número de elementos do array.
     */
    fun size(): Int {
        return elements.size
    }

    /**
     * Verifica se contém elemento.
     */
    fun contains(value: JsonValue): Boolean {
        return elements.contains(value)
    }

    /**
     * Devolve lista de elementos.
     */
    fun elements(): List<JsonValue> = elements

    /**
     * Percorre recursivamente
     * todos os elementos JSON.
     */
    override fun accept(visitor: (JsonValue) -> Unit) {

        // Visita array atual
        visitor(this)

        // Visita elementos internos
        elements.forEach {
            it.accept(visitor)
        }
    }

    /**
     * Converte para texto JSON.
     */
    override fun toString(): String {

        return elements.joinToString(
            prefix = "[",
            postfix = "]"
        ) {
            it.toString()
        }
    }
}