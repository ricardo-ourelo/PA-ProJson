//package org.example

import projjson.core.ProJson
import projjson.model.*

data class Person(val name: String, val age: Int)

fun main() {
    val p = Person("Ana \"teste\" \\ caminho", 25)

    val jsonn = ProJson().toJsonString(p)

    println(jsonn)


    val map = mapOf("x" to 10, "y" to 20)

    val json1 = ProJson().toJsonString(map)

    println(json1)

    val complex = mapOf(
        "name" to "Ana",
        "nums" to listOf(1,2,3)
    )

    val json2 = ProJson().toJsonString(complex)

    println(json2)


    val json = JsonObject()

    json.set("name", "Ana")
    json.set("age", 25)

    val array = JsonArray()
    array.add(1)
    array.add(2)

    json.set("numbers", array)

// alterar
    json.set("age", 30)

// remover
    json.remove("name")

    println(json)

     // ---------------- FILTER ----------------

    // Buscar todos os JsonPrimitive
    val primitives = json.filter {
        it is JsonPrimitive
    }

    println("\nPrimitives:")
    println(primitives)

    // Buscar todos os JsonArray
    val arrays = json.filter {
        it is JsonArray
    }

    println("\narrays:")
    println(arrays)

    // ---------------- FIND ----------------

    // Procurar primeiro JsonObject
    val firstObject = json.find {
        it is JsonObject
    }

    println("\nFirst object:")
    println(firstObject)

    // ---------------- COUNT ----------------

    // Contar primitivos
    val primitiveCount = json.count {
        it is JsonPrimitive
    }

    println("\nPrimitive count:")
    println(primitiveCount)

    // ---------------- MAP ----------------

    // Transformar elementos em nomes das classes
    val classNames = json.map {
        it::class.simpleName
    }

    println(classNames)

}