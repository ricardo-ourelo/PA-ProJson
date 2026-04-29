package projjson.model

/**
 * Converte valores Kotlin para JsonValue.
 *
 * Usado internamente por:
 * - JsonObject
 * - JsonArray
 *
 * Garante que apenas valores JSON válidos
 * sejam armazenados.
 */
internal fun wrap(value: Any?): JsonValue {

    return when (value) {

        // Valor já convertido
        is JsonValue -> value

        // Collections Kotlin
        is Collection<*> ->
            iterableToJson(value)

        // Arrays Kotlin
        is Array<*> ->
            iterableToJson(value.asList())

        // Arrays primitivos Kotlin
        is IntArray ->
            iterableToJson(value.toList())

        is DoubleArray ->
            iterableToJson(value.toList())

        is FloatArray ->
            iterableToJson(value.toList())

        is LongArray ->
            iterableToJson(value.toList())

        is BooleanArray ->
            iterableToJson(value.toList())

        // Maps -> JsonObject
        is Map<*, *> -> {

            val obj = JsonObject()

            value.forEach { (key, v) ->

                require(key is String) {
                    "Map keys must be strings"
                }

                obj.set(key, v)
            }

            obj
        }

        // Primitivos JSON válidos
        null,
        is String,
        is Number,
        is Boolean -> JsonPrimitive(value)

        // Objetos não suportados
        else -> {

            throw IllegalArgumentException(
                "Unsupported JSON value"
            )
        }
    }
}

/**
 * Converte estruturas iteráveis para JsonArray.
 *
 * Cada elemento é convertido
 * recursivamente para JsonValue.
 */
private fun iterableToJson(
    iterable: Iterable<*>
): JsonArray {

    val array = JsonArray()

    iterable.forEach {
        // Conversão recursiva
        array.add(it)
    }

    return array
}