package projjson.annotations

/**
 * Ignora propriedade durante serialização JSON.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME) //a annotation continua disponível em runtime
annotation class JsonIgnore