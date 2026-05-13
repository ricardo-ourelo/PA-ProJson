package projjson.core

import projjson.model.JsonObject
import projjson.model.JsonValue
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import java.util.IdentityHashMap
import projjson.annotations.*
import projjson.model.JsonArray
import projjson.model.JsonPrimitive
import projjson.plugins.JsonSerializer
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.createInstance
/**
 * Conversor de objetos Kotlin para JSON.
 *
 * Usa Reflection para converter
 * data classes em JsonObject.
 */
class ProJson {

    /**
     * Guarda referências de objetos já serializados.
     *
     * Mapeia:
     *
     * objeto real -> id
     *
     * Usa referência real de memória
     * em vez de equals/hashCode.
     *
     * Necessário para:
     * - $id
     * - $ref
     * - ciclos
     * - objetos compartilhados
     */
    private val references =
        IdentityHashMap<Any, String>()

    /**
     * Próximo identificador disponível.
     */
    private var nextId = 1

    /**
     * Gera identificadores únicos
     * usados em $id e $ref.
     */
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
    fun toJson(
        obj: Any?,
        useReference: Boolean = false
    ): JsonValue {

        return convert(
            obj,
            useReference
        )
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

        return declaredMemberProperties
            .firstOrNull {
                it.name == parameter.name
            }
            ?: error(
                "Property '${parameter.name}' not found"
            )
    }



    /**
     * Converte data class para JsonObject.
     */
    private fun objectToJson(
        obj: Any,
        useReference: Boolean = false
    ): JsonObject {

        val json = JsonObject()

        val clazz = obj::class

        // ---------------- REFERENCES ----------------

        // Só criar ID quando referências estiverem ativas
        if (useReference) {

            val id = generateId()

            references[obj] = id

            json.set("\$id", id)
        }

        // ---------------- TYPE ----------------

        json.set(
            "\$type",
            clazz.simpleName
        )

        // ---------------- PROPERTIES ----------------

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

                // Nome customizado
                val jsonName =
                    property.annotations
                        .filterIsInstance<JsonProperty>()
                        .firstOrNull()
                        ?.name
                        ?: property.name

                // Verificar @Reference
                val propertyUsesReference =
                    property.annotations.any {
                        it is Reference
                    }

                // Conversão recursiva
                json.set(
                    jsonName,
                    convert(
                        value,
                        propertyUsesReference
                    )
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
     * Serializa usando suporte completo
     * para referências e ciclos.
     */
    fun toJsonGraph(obj: Any?): JsonValue {

        return toJson(
            obj,
            true
        )
    }


    fun toJsonGraphString(
        obj: Any?
    ): String {

        return toJsonGraph(obj)
            .toString()
    }

    /**
     * Converte estruturas iteráveis
     * para JsonArray.
     */
    private fun iterableToJson(
        iterable: Iterable<*>,
        useReference: Boolean = false
    ): JsonArray {

        val array = JsonArray()

        iterable.forEach {

            // Conversão recursiva
            array.add(convert(it,useReference))
        }

        return array
    }

    /**
     * Converte valores Kotlin para JsonValue.
     *
     * Pipeline principal da serialização.
     *
     * Responsável por:
     * - primitives
     * - collections
     * - arrays
     * - maps
     * - plugins (@JsonString)
     * - references ($id / $ref)
     * - objetos complexos
     */
    private fun convert(
        value: Any?,
        useReference: Boolean = false
    ): JsonValue {

        // ---------------- NULL ----------------

        if (value == null) {
            return JsonPrimitive(null)
        }

        // ---------------- JSON VALUES ----------------

        if (value is JsonValue) {
            return value
        }

        // ---------------- REFERENCES ----------------

        // Só verifica referências quando ativado
        if (useReference) {

            references[value]?.let { id ->

                val ref = JsonObject()

                ref.set("\$ref", id)

                return ref
            }
        }

        // ---------------- RESTANTE SERIALIZAÇÃO ----------------

        return when (value) {

            // ---------------- PRIMITIVES ----------------

            is String,
            is Number,
            is Boolean ->

                JsonPrimitive(value)

            // ---------------- COLLECTIONS ----------------

            is Collection<*> ->

                iterableToJson(
                    value,
                    useReference
                )

            // ---------------- ARRAYS ----------------

            is Array<*> ->

                iterableToJson(
                    value.asList(),
                    useReference
                )

            // ---------------- PRIMITIVE ARRAYS ----------------

            is IntArray ->

                iterableToJson(
                    value.toList(),
                    useReference
                )

            is DoubleArray ->

                iterableToJson(
                    value.toList(),
                    useReference
                )

            is FloatArray ->

                iterableToJson(
                    value.toList(),
                    useReference
                )

            is LongArray ->

                iterableToJson(
                    value.toList(),
                    useReference
                )

            is BooleanArray ->

                iterableToJson(
                    value.toList(),
                    useReference
                )

            // ---------------- MAPS ----------------

            is Map<*, *> -> {

                val obj = JsonObject()

                value.forEach { (key, v) ->

                    require(key is String) {
                        "Map keys must be strings"
                    }

                    obj.set(
                        key,
                        convert(
                            v,
                            useReference
                        )
                    )
                }

                obj
            }

            // ---------------- COMPLEX OBJECTS ----------------

            else -> {

                // JsonString plugin
                // ---------------- PLUGINS ----------------

                // Verifica se classe possui
                // annotation @JsonString
                val annotation =
                    value::class.findAnnotation<JsonString>()

                // Plugin encontrado
                if (annotation != null) {

                    val serializer =
                        annotation.serializer
                            .createInstance()

                    val result =
                        (serializer as JsonSerializer<Any>)
                            .serialize(value)

                    return JsonPrimitive(result)
                }

                // Serialização Reflection normal
                objectToJson(
                    value,
                    useReference
                )
            }
        }
    }

}