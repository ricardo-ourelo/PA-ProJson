package projjson.core

import projjson.model.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

class ProJson {

    fun toJson(obj: Any?): JsonValue {
        if (obj == null) return JsonPrimitive(null)

        return when (obj) {
            is String, is Number, is Boolean -> JsonPrimitive(obj)

            is Collection<*> -> {
                val array = JsonArray()
                obj.forEach { array.add(toJson(it)) }
                array
            }

            is Map<*, *> -> {
                val json = JsonObject()
                obj.forEach { (key, value) ->
                    require(key is String) { "Map keys must be strings" }
                    json.set(key, toJson(value))
                }
                json
            }

            else -> objectToJson(obj)
        }
    }

    fun KClass<*>.matchProperty(parameter: KParameter) : KProperty<*> {
        require(isData)
        return declaredMemberProperties.first { it.name == (parameter.name) }
    }

    private fun objectToJson(obj: Any): JsonObject {
        val json = JsonObject()
        val clazz = obj::class

        json.set("\$type", JsonPrimitive(clazz.simpleName))

          clazz.primaryConstructor?.parameters?.forEach { param ->
              val prop = clazz.matchProperty(param)
              val value = prop.call(obj)
              json.set(prop.name, toJson(value)) // recursivo
          }

        return json
    }

    fun toJsonString(obj: Any?): String {
        return toJson(obj).toString()
    }
}