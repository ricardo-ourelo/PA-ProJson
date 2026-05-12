package projjson.core

import projjson.model.JsonObject
import projjson.model.JsonValue
import projjson.model.wrap
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import java.util.IdentityHashMap
import projjson.annotations.*

/**
 * Conversor de objetos Kotlin para JSON.
 *
 * Usa Reflection para converter
 * data classes em JsonObject.
 */
class ProJson {

    /**
     * Mapa de referências
     * Guarda objeto real -> id
     *
     * Usa identidade de memória para detetar referências repetidas e ciclos.
     */
    private val references =
        IdentityHashMap<Any, String>()

    /**
     * Gera IDs únicos para objetos serializados
     * usados em $id e $ref.
     */
    private var nextId = 1

    private fun generateId(): String {
        return (nextId++).toString()
    }

    /**
     * Verifica se o valor é um objeto Kotlin complexo.
     */
    private fun isComplexObject(obj: Any?): Boolean {

        return obj != null &&
                obj !is String &&
                obj !is Number &&
                obj !is Boolean &&
                obj !is Collection<*> &&
                obj !is Map<*, *> &&
                obj !is JsonValue &&
                !obj.javaClass.isArray
    }

    /**
     * Converte objeto Kotlin para JsonValue.
     */
    fun toJson(obj: Any?): JsonValue {

        // Objetos complexos Kotlin
        if (isComplexObject(obj)) {

            // Verifica se objeto já apareceu antes
            if (references.containsKey(obj)) {

                val ref = JsonObject()

                ref.set(
                    "\$ref",
                    references[obj]
                )

                return ref
            }
            //serializa normalmente
            return objectToJson(obj!!) //(obj!!) -> tem a certeza que o objeto não é nulo
        }

        // Conversão padrão
        return convert(obj)
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

        // Gerar identificador único
        val id = generateId()

        // Guardar referência do objeto
        references[obj] = id

        // Guardar identificador no Json
        json.set("\$id", id)

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

                // Ignorar propriedade
                if (
                    property.annotations.any {
                        it is JsonIgnore
                    }
                ) {
                    return@forEach
                }

                val value =
                    property.call(obj)

                val jsonName =
                    property.annotations
                        .filterIsInstance<JsonProperty>()
                        .firstOrNull()
                        ?.name
                        ?: property.name

                // Conversão recursiva
                json.set(
                    jsonName,
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

    /**
     * Converte valores Kotlin para JsonValue.
     *
     * Permite serialização recursiva de    :
     * - collections
     * - maps
     * - objetos complexos
     * mantendo referências.
     */
    private fun convert(value: Any?): JsonValue {

        return when (value) {

            is JsonValue ->
                value

            is Collection<*> -> {

                val array = projjson.model.JsonArray()

                value.forEach {
                    array.add(convert(it))
                }

                array
            }

            is Map<*, *> -> {

                val obj = JsonObject()

                value.forEach { (key, v) ->

                    require(key is String)

                    obj.set(key, convert(v))
                }

                obj
            }

            else -> {

                if (isComplexObject(value)) {
                    toJson(value)
                } else {
                    wrap(value)
                }
            }
        }
    }

}