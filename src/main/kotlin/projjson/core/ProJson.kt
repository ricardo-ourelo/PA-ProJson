package projjson.core

import projjson.model.JsonObject
import projjson.model.JsonValue
import projjson.model.wrap
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Conversor de objetos Kotlin para JSON.
 *
 * Usa Reflection para converter
 * data classes em JsonObject.
 */
class ProJson {

    /**
     * Converte objeto Kotlin para JsonValue.
     */
    fun toJson(obj: Any?): JsonValue {

        // Objetos complexos Kotlin
        if (
            obj != null &&
            obj !is String &&
            obj !is Number &&
            obj !is Boolean &&
            obj !is Collection<*> &&
            obj !is Map<*, *> &&
            obj !is JsonValue &&
            !obj.javaClass.isArray
        ) {
            return objectToJson(obj)
        }

        // Conversão padrão
        return wrap(obj)
    }

    /**
     * Procura propriedade correspondente
     * ao parâmetro do construtor.
     *
     * Mantém ordem correta das propriedades.
     */
    private fun KClass<*>.matchProperty(
        parameter: KParameter
    ): KProperty<*> {

        require(isData) {
            "Only data classes are supported"
        }

        return declaredMemberProperties.first {
            it.name == parameter.name
        }
    }

    /**
     * Converte data class para JsonObject.
     */
    private fun objectToJson(obj: Any): JsonObject {

        val json = JsonObject()

        val clazz = obj::class

        // Nome da classe
        json.set(
            "\$type",
            clazz.simpleName
        )

        // Converter propriedades
        clazz.primaryConstructor
            ?.parameters
            ?.forEach { parameter ->

                val property =
                    clazz.matchProperty(parameter)

                val value =
                    property.call(obj)

                // Conversão recursiva
                json.set(
                    property.name,
                    toJson(value)
                )
            }

        return json
    }

    /**
     * Converte diretamente para String JSON.
     */
    fun toJsonString(obj: Any?): String {
        return toJson(obj).toString()
    }
}