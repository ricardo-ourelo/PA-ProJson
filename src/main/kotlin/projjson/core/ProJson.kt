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
 * Biblioteca principal responsável pela serialização
 * de objetos Kotlin para estruturas JSON.
 *
 * A conversão utiliza Reflection para:
 * - descobrir propriedades automaticamente
 * - suportar data classes
 * - aplicar annotations customizadas
 * - gerir referências e ciclos
 * - suportar plugins de serialização
 *
 * A serialização produz estruturas baseadas em:
 * - JsonObject
 * - JsonArray
 * - JsonPrimitive
 */
class ProJson {

    /**
     * Guarda referências de objetos já serializados.
     *
     * Mapeia:
     *
     * objeto real -> id
     *
     * Usa identidade de memória em vez de equals/hashCode.
     *
     * Isso é essencial para detectar corretamente:
     * - ciclos
     * - referências compartilhadas
     * - objetos distintos com mesmo conteúdo
     */
    private val references =
        IdentityHashMap<Any, String>()

    /**
     * Próximo identificador disponível.
     */
    private var nextId = 1

    /**
     * Gera identificadores únicos usados
     * no sistema de referências JSON.
     *
     * Os IDs são utilizados nos campos:
     * - $id
     * - $ref
     *
     * @return identificador único
     */
    private fun generateId(): String {
        return (nextId++).toString()
    }

    /**
     * Converte qualquer objeto Kotlin para JsonValue.
     *
     * Suporta:
     * - primitivas JSON
     * - collections
     * - arrays
     * - maps
     * - data classes
     * - referências
     * - plugins customizados
     *
     * @param obj objeto a serializar
     * @param useReference ativa rastreamento
     * de referências usando $id e $ref
     *
     * @return estrutura JSON equivalente
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
     * Obtém a propriedade correspondente ao parâmetro
     * do construtor primário.
     *
     * Isto garante que a serialização respeita
     * a ordem declarada no construtor da data class.
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
     * Converte uma data class Kotlin para JsonObject.
     *
     * As propriedades são obtidas automaticamente
     * através de Reflection.
     *
     * O método também processa:
     * - @JsonIgnore
     * - @JsonProperty
     * - @Reference
     *
     * @param obj objeto a serializar
     * @param useReference ativa suporte a referências
     *
     * @return JsonObject correspondente
     */
    private fun objectToJson(
        obj: Any,
        useReference: Boolean = false
    ): JsonObject {

        val json = JsonObject()
        val clazz = obj::class

        // ---------------- REFERENCES ----------------


        if (useReference) {
            /**
             * O objeto é registado antes da serialização
             * das propriedades para evitar recursão infinita
             * em estruturas cíclicas.
             */
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
        return toJson(obj, true).toString()
    }



    /**
     * Converte estruturas iteráveis para JsonArray.
     *
     * Cada elemento é convertido recursivamente
     * para JsonValue.
     *
     * @param iterable coleção a converter
     * @param useReference ativa suporte a referências
     *
     * @return array JSON equivalente
     */
    private fun iterableToJson(
        iterable: Iterable<*>,
        useReference: Boolean = false
    ): JsonArray {

        val array = JsonArray()

        iterable.forEach {

            // Conversão recursiva
            array.add(
                convert(
                    it,
                    useReference
                )
            )
        }

        return array
    }

    /**
     * Pipeline principal de serialização.
     *
     * Responsável por converter recursivamente
     * qualquer valor Kotlin para JsonValue.
     *
     * O processo suporta:
     * - null
     * - JsonValue
     * - referências
     * - primitivas
     * - collections
     * - arrays
     * - maps
     * - plugins customizados
     * - objetos Kotlin via Reflection
     *
     * @param value valor a converter
     * @param useReference ativa gestão de referências
     *
     * @return valor convertido para JsonValue
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

            is ShortArray ->
                iterableToJson(
                    value.toList(),
                    useReference
                )

            is ByteArray ->
                iterableToJson(
                    value.toList(),
                    useReference
                )

            is CharArray ->
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

                // ---------------- REFERENCE ----------------
                // Usa referência apenas quando
                // propriedade possui @Reference
                if (useReference) {
                    references[value]?.let { id ->
                        val ref = JsonObject()
                        ref.set("\$ref", id)
                        return ref
                    }
                }

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

                    //Mantém warning controlado localmente.
                    @Suppress("UNCHECKED_CAST")
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
}