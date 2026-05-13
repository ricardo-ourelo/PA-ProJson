package projjson.annotations

import kotlin.reflect.KClass

/**
 * Define serializer customizado
 * para uma classe.
 */
@Target(AnnotationTarget.CLASS)
annotation class JsonString(

    val serializer:
    KClass<out projjson.plugins.JsonSerializer<*>>
)