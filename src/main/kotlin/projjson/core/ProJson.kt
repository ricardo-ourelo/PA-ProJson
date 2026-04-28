package projjson.core

import projjson.model.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Classe responsável por converter objetos Kotlin para JSON.
 * Usa Reflection para descobrir propriedades automaticamente.
 */
class ProJson {
    /**
     * Converte qualquer objeto Kotlin para JsonValue.
     */
    fun toJson(obj: Any?): JsonValue {
        // Representação JSON de null
        if (obj == null) return JsonPrimitive(null)
        return when (obj) {
            // Tipos primitivos JSON
            is String,
            is Number,
            is Boolean -> JsonPrimitive(obj)
            // List, Set, etc.
            is Collection<*> -> {
                val array = JsonArray()
                // Conversão recursiva dos elementos
                obj.forEach {
                    array.add(toJson(it))
                }
                array
            }
            // Map<String, Any>
            is Map<*, *> -> {
                val json = JsonObject()
                obj.forEach { (key, value) ->
                    // JSON só aceita String como chave
                    require(key is String) {
                        "Map keys must be strings"
                    }
                    // Conversão recursiva
                    json.set(key, toJson(value))
                }
                json
            }
            // Objetos Kotlin normais
            else -> objectToJson(obj)
        }
    }

    /**
     * Associa um parâmetro do construtor
     * à propriedade correspondente.
     * Permite manter a ordem correta no JSON.
     */
    fun KClass<*>.matchProperty(parameter: KParameter): KProperty<*> {
        require(isData)
        return declaredMemberProperties.first {
            it.name == parameter.name
        }
    }

    /**
     * Converte um objeto Kotlin para JsonObject.
     */
    private fun objectToJson(obj: Any): JsonObject {

        val json = JsonObject()
        // Classe do objeto
        val clazz = obj::class
        // Guardar o tipo original
        json.set("\$type", JsonPrimitive(clazz.simpleName))
        // Percorrer parâmetros do construtor
        clazz.primaryConstructor?.parameters?.forEach { param ->
            // Encontrar propriedade correspondente
            val prop = clazz.matchProperty(param)
            // Obter valor da propriedade
            val value = prop.call(obj)
            // Conversão recursiva
            json.set(prop.name, toJson(value))
        }

        return json
    }
    /**
     * Converte diretamente para texto JSON.
     */
    fun toJsonString(obj: Any?): String {
        return toJson(obj).toString()
    }
}