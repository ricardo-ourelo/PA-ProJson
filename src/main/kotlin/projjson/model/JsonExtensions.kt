package projjson.model

/**
 * Filtra elementos da árvore JSON.
 */
fun JsonValue.filter(
    predicate: (JsonValue) -> Boolean
): List<JsonValue> {

    val result = mutableListOf<JsonValue>()
    accept {
        if (predicate(it)) {
            result.add(it)
        }
    }

    return result
}

/**
 * Procura primeiro elemento
 * que satisfaz a condição.
 */
fun JsonValue.find(
    predicate: (JsonValue) -> Boolean
): JsonValue? {

    var result: JsonValue? = null
    accept {
        if (result == null && predicate(it)) {
            result = it
        }
    }

    return result
}

/**
 * Conta elementos da árvore JSON.
 */
fun JsonValue.count(
    predicate: (JsonValue) -> Boolean
): Int {

    var count = 0
    accept {
        if (predicate(it)) {
            count++
        }
    }

    return count
}

/**
 * Transforma elementos da árvore JSON.
 */
fun <T> JsonValue.map(
    transform: (JsonValue) -> T
): List<T> {

    val result = mutableListOf<T>()
    accept {
        result.add(transform(it))
    }

    return result
}