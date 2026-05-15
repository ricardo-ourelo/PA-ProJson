package projjson.model

/**
 * Classe base de todos os elementos JSON.
 *
 * Representa qualquer estrutura JSON:
 * - objetos
 * - arrays
 * - valores primitivos
 *
 * Utilizada como base do padrão Composite.
 */
sealed class JsonValue {

    /**
     * Percorre a árvore JSON usando o padrão Visitor.
     *
     * @param visitor função executada para cada nó visitado
     */
    abstract fun accept(visitor: (JsonValue) -> Unit)
}