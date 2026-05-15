package projjson.plugins

/**
 * Interface base para plugins
 * de serialização customizada.
 *
 * Permite definir como objetos
 * específicos devem ser convertidos
 * para JSON.
 */
interface JsonSerializer<T> {

    /**
     * Converte um objeto para representação textual JSON.
     *
     * @param value objeto a serializar
     *
     * @return representação textual
     */
    fun serialize(value: T): String
}