package projjson.annotations

/**
 * Ignora uma propriedade durante
 * a serialização JSON.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME) //a annotation continua disponível em runtime
annotation class JsonIgnore