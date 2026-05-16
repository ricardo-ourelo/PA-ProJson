package projjson

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import projjson.core.ProJson
import projjson.model.JsonObject
import projjson.model.JsonPrimitive

class ObjectSerializationTest {

    private val proJson = ProJson()

    /**
     * Classe simples para testes.
     */
    data class Person(
        val name: String,
        val age: Int
    )

    /**
     * Classe aninhada.
     */
    data class Address(
        val city: String
    )

    data class User(
        val name: String,
        val address: Address
    )

    /**
     * Classe com null.
     */
    data class NullableUser(
        val name: String?,
        val age: Int?
    )

    /**
     * Testa serialização de objeto simples.
     */
    @Test
    fun testSimpleObject() {

        val json =
            proJson.toJson(
                Person("Ana", 25)
            )

        assertInstanceOf(
            JsonObject::class.java,
            json
        )

        val obj =
            json as JsonObject

        assertEquals(
            "\"Person\"",
            obj["\$type"].toString()
        )

        assertEquals(
            "\"Ana\"",
            obj["name"].toString()
        )

        assertEquals(
            "25",
            obj["age"].toString()
        )
    }

    /**
     * Testa serialização de objeto aninhado.
     */
    @Test
    fun testNestedObject() {

        val json =
            proJson.toJson(

                User(
                    "Ana",
                    Address("Lisboa")
                )
            )

        val obj =
            json as JsonObject

        assertEquals(
            "\"User\"",
            obj["\$type"].toString()
        )

        assertEquals(
            "\"Ana\"",
            obj["name"].toString()
        )

        val address =
            obj["address"] as JsonObject

        assertEquals(
            "\"Address\"",
            address["\$type"].toString()
        )

        assertEquals(
            "\"Lisboa\"",
            address["city"].toString()
        )
    }

    /**
     * Testa serialização de propriedades null.
     */
    @Test
    fun testObjectWithNulls() {

        val json =
            proJson.toJson(

                NullableUser(
                    null,
                    null
                )
            )

        val obj =
            json as JsonObject

        assertEquals(
            "null",
            obj["name"].toString()
        )

        assertEquals(
            "null",
            obj["age"].toString()
        )
    }

    /**
     * Testa tipos internos das propriedades.
     */
    @Test
    fun testObjectPropertyTypes() {

        val json =
            proJson.toJson(
                Person("Ana", 25)
            )

        val obj =
            json as JsonObject

        assertInstanceOf(
            JsonPrimitive::class.java,
            obj["name"]
        )

        assertInstanceOf(
            JsonPrimitive::class.java,
            obj["age"]
        )
    }

    /**
     * Testa saída JSON completa.
     */
    @Test
    fun testObjectToString() {

        val json =
            proJson.toJson(
                Person("Ana", 25)
            )

        val result =
            json.toString()

        print(result)

        assertTrue(
            result.contains("\"name\": \"Ana\"")
        )

        assertTrue(
            result.contains("\"age\": 25")
        )

        assertTrue(
            result.contains("\"\$type\": \"Person\"")
        )
    }

    /**
     * Testa serialização de objeto aninhado
     * como string JSON.
     */
    @Test
    fun testNestedObjectToString() {

        val json =
            proJson.toJson(

                User(
                    "Ana",
                    Address("Porto")
                )
            )

        val result =
            json.toString()

        assertTrue(
            result.contains("\"name\": \"Ana\"")
        )

        assertTrue(
            result.contains("\"city\": \"Porto\"")
        )

        assertTrue(
            result.contains("\"\$type\": \"Address\"")
        )
    }

    /**
     * Testa se ordem das propriedades
     * é mantida.
     */
    @Test
    fun testPropertyOrder() {

        val json =
            proJson.toJson(
                Person("Ana", 25)
            )

        val result =
            json.toString()

        val typeIndex =
            result.indexOf("\$type")

        val nameIndex =
            result.indexOf("name")

        val ageIndex =
            result.indexOf("age")

        assertTrue(
            typeIndex < nameIndex
        )

        assertTrue(
            nameIndex < ageIndex
        )
    }

    /**
     * Testa múltiplos objetos independentes.
     */
    @Test
    fun testMultipleObjects() {

        val p1 =
            proJson.toJson(
                Person("Ana", 20)
            )

        val p2 =
            proJson.toJson(
                Person("Joao", 30)
            )

        assertNotEquals(
            p1.toString(),
            p2.toString()
        )
    }

    /**
     * Testa serialização diretamente
     * para String JSON.
     */
    @Test
    fun testToJsonString() {

        val json =
            proJson.toJsonString(
                Person("Ana", 25)
            )

        assertTrue(
            json.contains("\"name\": \"Ana\"")
        )

        assertTrue(
            json.contains("\"age\": 25")
        )
    }
}