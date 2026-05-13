package projjson.plugins

/**
 * Plugin de serialização customizada.
 */
interface JsonSerializer<T> {

    /**
     * Converte objeto para String JSON.
     */
    fun serialize(value: T): String
}