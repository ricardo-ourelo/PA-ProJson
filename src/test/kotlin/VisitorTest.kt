package projjson

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import projjson.core.ProJson
import projjson.model.JsonArray
import projjson.model.JsonObject
import projjson.model.JsonPrimitive

class VisitorTest {

    private val proJson = ProJson()

    /**
     * Classe simples para testes.
     */
    data class Person(
        val name: String,
        val age: Int
    )

    /**
     * Testa visita de JsonPrimitive.
     */
    @Test
    fun testVisitPrimitive() {

        val json =
            JsonPrimitive("Ana")

        var count = 0

        json.accept {
            count++
        }

        assertEquals(
            1,
            count
        )
    }

    /**
     * Testa visita de JsonArray.
     */
    @Test
    fun testVisitArray() {

        val json =
            proJson.toJson(
                listOf(1, 2, 3)
            )

        var count = 0

        json.accept {
            count++
        }

        /**
         * JsonArray + 3 JsonPrimitive
         */
        assertEquals(
            4,
            count
        )
    }

    /**
     * Testa visita de JsonObject.
     */
    @Test
    fun testVisitObject() {

        val json =
            proJson.toJson(
                Person("Ana", 25)
            )

        var count = 0

        json.accept {
            count++
        }

        /**
         * JsonObject
         * + $type
         * + name
         * + age
         */
        assertEquals(
            4,
            count
        )
    }

    /**
     * Testa visita de objeto aninhado.
     */
    @Test
    fun testVisitNestedObject() {

        data class Address(
            val city: String
        )

        data class User(
            val name: String,
            val address: Address
        )

        val json =
            proJson.toJson(

                User(
                    "Ana",
                    Address("Lisboa")
                )
            )

        var count = 0

        json.accept {
            count++
        }

        /**
         * Root User object
         * + $type User
         * + name
         * + Address object
         * + $type Address
         * + city
         */
        assertEquals(
            6,
            count
        )
    }

    /**
     * Testa ordem de visita.
     */
    @Test
    fun testVisitOrder() {

        val json =
            proJson.toJson(
                listOf(1, 2)
            )

        val visited =
            mutableListOf<String>()

        json.accept {

            visited.add(
                it::class.simpleName!!
            )
        }

        assertEquals(
            "JsonArray",
            visited[0]
        )

        assertEquals(
            "JsonPrimitive",
            visited[1]
        )

        assertEquals(
            "JsonPrimitive",
            visited[2]
        )
    }

    /**
     * Testa visitor em JsonObject manual.
     */
    @Test
    fun testManualJsonObjectVisit() {

        val obj =
            JsonObject()

        obj.set(
            "name",
            JsonPrimitive("Ana")
        )

        obj.set(
            "age",
            JsonPrimitive(25)
        )

        var count = 0

        obj.accept {
            count++
        }

        /**
         * JsonObject
         * + name primitive
         * + age primitive
         */
        assertEquals(
            3,
            count
        )
    }

    /**
     * Testa visitor em JsonArray manual.
     */
    @Test
    fun testManualJsonArrayVisit() {

        val array =
            JsonArray()

        array.add(JsonPrimitive(1))
        array.add(JsonPrimitive(2))

        var count = 0

        array.accept {
            count++
        }

        /**
         * JsonArray
         * + primitive 1
         * + primitive 2
         */
        assertEquals(
            3,
            count
        )
    }

    /**
     * Testa visitor em árvore complexa.
     */
    @Test
    fun testComplexTreeVisit() {

        val json =
            proJson.toJson(

                mapOf(
                    "users" to listOf(
                        Person("Ana", 20),
                        Person("Joao", 30)
                    )
                )
            )

        var count = 0

        json.accept {
            count++
        }

        /**
         * Estrutura mínima esperada:
         * Root JsonObject
         * users JsonArray
         * 2 Person JsonObject
         * propriedades primitivas
         */
        assertTrue(
            count >= 9
        )
    }

    /**
     * Testa se visitor percorre
     * todos os nós.
     */
    @Test
    fun testVisitorTraversal() {

        val json =
            proJson.toJson(
                listOf(
                    "Ana",
                    25,
                    true
                )
            )

        val visited =
            mutableListOf<String>()

        json.accept {

            visited.add(
                it.toString()
            )
        }

        assertTrue(
            visited.contains("\"Ana\"")
        )

        assertTrue(
            visited.contains("25")
        )

        assertTrue(
            visited.contains("true")
        )
    }
}