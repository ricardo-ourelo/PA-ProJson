package projjson.model

/**
 * Representa um array JSON.
 *
 * Armazena múltiplos JsonValue
 * numa estrutura ordenada.
 *
 * Implementa o padrão Composite.
 */
class JsonArray(
    private val elements: MutableList<JsonValue> = mutableListOf()
) : JsonValue() {

    /**
     * Adiciona um elemento ao array.
     *
     * O valor é convertido automaticamente
     * para JsonValue.
     *
     * @param value elemento a adicionar
     */
    fun add(value: Any?) {
        elements.add(wrap(value))
    }

    /**
     * Obtém elemento pelo índice.
     *
     * Garante índice válido.
     */
    fun get(index: Int): JsonValue {

        require(index in elements.indices) {
            "Invalid index"
        }

        return elements[index]
    }

    /**
     * Substitui elemento do array.
     *
     * Garante índice válido.
     */
    fun set(index: Int, value: Any?) {

        require(index in elements.indices) {
            "Invalid index"
        }

        elements[index] = wrap(value)
    }

    /**
     * Remove elemento do array.
     *
     * Garante índice válido.
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
     * Número de elementos.
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
     * Devolve elementos em modo read-only.
     */
    fun elements(): List<JsonValue> = elements

    /**
     * Percorre recursivamente
     * toda a árvore JSON.
     *
     * Usa o padrão Visitor.
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
     * Converte array para texto JSON.
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