package projjson

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import projjson.model.JsonArray
import projjson.model.JsonObject
import projjson.model.JsonPrimitive

class JsonApiTest {

    /**
     * Testa set/get em JsonObject.
     */
    @Test
    fun testJsonObjectSetGet() {

        val obj =
            JsonObject()

        obj.set(
            "name",
            JsonPrimitive("Ana")
        )

        assertNotNull(
            obj["name"]
        )

        assertEquals(
            "\"Ana\"",
            obj["name"].toString()
        )
    }

    /**
     * Testa overwrite de propriedade.
     */
    @Test
    fun testJsonObjectOverwrite() {

        val obj =
            JsonObject()

        obj.set(
            "name",
            JsonPrimitive("Ana")
        )

        obj.set(
            "name",
            JsonPrimitive("Maria")
        )

        assertEquals(
            "\"Maria\"",
            obj["name"].toString()
        )
    }

    /**
     * Testa propriedade inexistente.
     */
    @Test
    fun testJsonObjectMissingKey() {

        val obj =
            JsonObject()

        assertNull(
            obj["missing"]
        )
    }

    /**
     * Testa múltiplas propriedades.
     */
    @Test
    fun testJsonObjectMultipleProperties() {

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
     * Testa add em JsonArray.
     */
    @Test
    fun testJsonArrayAdd() {

        val array =
            JsonArray()

        array.add(
            JsonPrimitive(1)
        )

        array.add(
            JsonPrimitive(2)
        )

        assertEquals(
            2,
            array.size()
        )

        assertEquals(
            "1",
            array[0].toString()
        )

        assertEquals(
            "2",
            array[1].toString()
        )
    }

    /**
     * Testa acesso inválido em JsonArray.
     */
    @Test
    fun testJsonArrayInvalidIndex() {

        val array =
            JsonArray()

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            array[0]
        }
    }

    /**
     * Testa array vazio.
     */
    @Test
    fun testEmptyJsonArray() {

        val array =
            JsonArray()

        assertEquals(
            0,
            array.size()
        )

        assertEquals(
            "[]",
            array.toString()
        )
    }

    /**
     * Testa objeto vazio.
     */
    @Test
    fun testEmptyJsonObject() {

        val obj =
            JsonObject()

        assertEquals(
            "{}",
            obj.toString()
        )
    }

    /**
     * Testa toString de JsonObject.
     */
    @Test
    fun testJsonObjectToString() {

        val obj =
            JsonObject()

        obj.set(
            "name",
            JsonPrimitive("Ana")
        )

        val result =
            obj.toString()

        assertTrue(
            result.contains(
                "\"name\": \"Ana\""
            )
        )
    }

    /**
     * Testa toString de JsonArray.
     */
    @Test
    fun testJsonArrayToString() {

        val array =
            JsonArray()

        array.add(JsonPrimitive(1))
        array.add(JsonPrimitive(2))

        assertEquals(
            "[1, 2]",
            array.toString()
        )
    }

    /**
     * Testa JsonPrimitive String.
     */
    @Test
    fun testJsonPrimitiveString() {

        val primitive =
            JsonPrimitive("Ana")

        assertEquals(
            "\"Ana\"",
            primitive.toString()
        )
    }

    /**
     * Testa JsonPrimitive Number.
     */
    @Test
    fun testJsonPrimitiveNumber() {

        val primitive =
            JsonPrimitive(25)

        assertEquals(
            "25",
            primitive.toString()
        )
    }

    /**
     * Testa JsonPrimitive Boolean.
     */
    @Test
    fun testJsonPrimitiveBoolean() {

        val primitive =
            JsonPrimitive(true)

        assertEquals(
            "true",
            primitive.toString()
        )
    }

    /**
     * Testa JsonPrimitive null.
     */
    @Test
    fun testJsonPrimitiveNull() {

        val primitive =
            JsonPrimitive(null)

        assertEquals(
            "null",
            primitive.toString()
        )
    }

    /**
     * Testa nested structures.
     */
    @Test
    fun testNestedJsonStructures() {

        val obj =
            JsonObject()

        val array =
            JsonArray()

        array.add(
            JsonPrimitive("A")
        )

        array.add(
            JsonPrimitive("B")
        )

        obj.set(
            "letters",
            array
        )

        val result =
            obj.toString()

        assertTrue(
            result.contains(
                "\"letters\""
            )
        )

        assertTrue(
            result.contains(
                "[\"A\", \"B\"]"
            )
        )
    }

    /**
     * Testa preservação de ordem.
     */
    @Test
    fun testPropertyOrder() {

        val obj =
            JsonObject()

        obj.set(
            "a",
            JsonPrimitive(1)
        )

        obj.set(
            "b",
            JsonPrimitive(2)
        )

        val result =
            obj.toString()

        val indexA =
            result.indexOf("\"a\"")

        val indexB =
            result.indexOf("\"b\"")

        assertTrue(
            indexA < indexB
        )
    }
}