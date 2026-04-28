package projjson.model

/**
 * Classe base de todos os elementos JSON.
 *
 * JsonValue representa qualquer valor JSON:
 * - objeto
 * - array
 * - primitivo
 */
sealed class JsonValue {

    /**
     * Permite percorrer a árvore JSON.
     */
    abstract fun accept(visitor: (JsonValue) -> Unit)
}