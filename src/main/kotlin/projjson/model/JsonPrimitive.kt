package projjson.model

/**
 * Representa um valor primitivo JSON.
 *
 * Valores suportados:
 * - String
 * - Number
 * - Boolean
 * - null
 *
 * JsonPrimitive é um nó leaf
 * no padrão Composite.
 */
class JsonPrimitive(val value: Any?) : JsonValue() {

    init {

        // Tipos válidos JSON
        require(
            value == null ||
                    value is String ||
                    value is Number ||
                    value is Boolean
        ) {
            "Invalid JSON primitive type"
        }

        // JSON não suporta NaN nem Infinity
        require(
            value !is Double || value.isFinite()
        ) {
            "Invalid JSON number"
        }

        require(
            value !is Float || value.isFinite()
        ) {
            "Invalid JSON number"
        }
    }

    /**
     * Visita o elemento atual.
     *
     * Como JsonPrimitive não possui filhos,
     * apenas visita o próprio nó.
     */
    override fun accept(visitor: (JsonValue) -> Unit) {
        visitor(this)
    }

    /**
     * Converte valor para texto JSON.
     */
    override fun toString(): String {

        return when (value) {
            // Representação JSON de null
            null -> "null"
            // Strings precisam de escape
            is String -> "\"${escape(value)}\""
            // Number / Boolean
            else -> value.toString()
        }
    }

    /**
     * Escapa caracteres especiais
     * válidos em JSON.
     *
     * Suporta:
     * - aspas
     * - backslashes
     * - newlines
     * - tabs
     * - carriage returns
     *
     * @param str texto original
     *
     * @return texto escapado
     */
    private fun escape(str: String): String {
        return str
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
            .replace("\r", "\\r")
    }
}