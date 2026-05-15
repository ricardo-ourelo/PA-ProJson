package projjson.model

/**
 * Extensions utilitárias para
 * navegação e manipulação
 * de árvores JSON.
 */

/**
 * Filtra elementos da árvore JSON
 * que satisfazem uma condição.
 *
 * @param predicate condição de filtragem
 *
 * @return lista de elementos encontrados
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
 * Procura o primeiro elemento
 * da árvore JSON que satisfaz
 * uma condição.
 *
 * @param predicate condição de procura
 *
 * @return primeiro elemento encontrado
 * ou null caso não exista
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
 * Conta elementos da árvore JSON
 * que satisfazem uma condição.
 *
 * @param predicate condição de contagem
 *
 * @return número de elementos encontrados
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
 * Transforma elementos da árvore JSON
 * para outro tipo de valor.
 *
 * @param transform transformação aplicada
 * a cada elemento
 *
 * @return lista transformada
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