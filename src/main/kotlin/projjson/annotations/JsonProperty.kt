package projjson.annotations

/**
 * Permite alterar o nome da propriedade no JSON.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonProperty(
    val name: String
)