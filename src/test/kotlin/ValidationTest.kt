package projjson

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import projjson.core.ProJson

class ValidationTest {

    private val proJson = ProJson()

    /**
     * Classe simples para testes.
     */
    data class Person(
        val name: String
    )

    /**
     * Testa rejeição de Double.NaN.
     */
    @Test
    fun testRejectNaN() {

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            proJson.toJson(
                Double.NaN
            )
        }
    }

    /**
     * Testa rejeição de Float.NaN.
     */
    @Test
    fun testRejectFloatNaN() {

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            proJson.toJson(
                Float.NaN
            )
        }
    }

    /**
     * Testa rejeição de infinito positivo.
     */
    @Test
    fun testRejectPositiveInfinity() {

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            proJson.toJson(
                Double.POSITIVE_INFINITY
            )
        }
    }

    /**
     * Testa rejeição de infinito negativo.
     */
    @Test
    fun testRejectNegativeInfinity() {

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            proJson.toJson(
                Double.NEGATIVE_INFINITY
            )
        }
    }

    /**
     * Testa rejeição de chave inválida em Map.
     */
    @Test
    fun testRejectInvalidMapKey() {

        val invalidMap =
            mapOf(
                1 to "Ana"
            )

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            proJson.toJson(
                invalidMap
            )
        }
    }

    /**
     * Testa índice inválido em JsonArray.
     */
    @Test
    fun testInvalidJsonArrayIndex() {

        val array =
            projjson.model.JsonArray()

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            array[0]
        }
    }

    /**
     * Testa acesso inválido em JsonObject.
     */
    @Test
    fun testMissingJsonObjectProperty() {

        val obj =
            projjson.model.JsonObject()

        assertNull(
            obj["missing"]
        )
    }

    /**
     * Testa serialização de null.
     */
    @Test
    fun testNullSerialization() {

        val json =
            proJson.toJson(null)

        assertEquals(
            "null",
            json.toString()
        )
    }

    /**
     * Testa serialização segura
     * de objeto vazio.
     */
    @Test
    fun testEmptyObjectSerialization() {

        data class Empty(
            val value: String? = null
        )

        val json =
            proJson.toJson(
                Empty()
            )

        assertTrue(
            json.toString()
                .contains("\"value\": null")
        )
    }

    /**
     * Testa lista vazia.
     */
    @Test
    fun testEmptyListSerialization() {

        val json =
            proJson.toJson(
                emptyList<String>()
            )

        assertEquals(
            "[]",
            json.toString()
        )
    }

    /**
     * Testa map vazio.
     */
    @Test
    fun testEmptyMapSerialization() {

        val json =
            proJson.toJson(
                emptyMap<String, String>()
            )

        assertEquals(
            "{}",
            json.toString()
        )
    }

    /**
     * Testa serialização de string vazia.
     */
    @Test
    fun testEmptyStringSerialization() {

        val json =
            proJson.toJson("")

        assertEquals(
            "\"\"",
            json.toString()
        )
    }

    /**
     * Testa valores booleanos.
     */
    @Test
    fun testBooleanSerialization() {

        val trueJson =
            proJson.toJson(true)

        val falseJson =
            proJson.toJson(false)

        assertEquals(
            "true",
            trueJson.toString()
        )

        assertEquals(
            "false",
            falseJson.toString()
        )
    }

    /**
     * Testa serialização de número negativo.
     */
    @Test
    fun testNegativeNumberSerialization() {

        val json =
            proJson.toJson(-100)

        assertEquals(
            "-100",
            json.toString()
        )
    }

    /**
     * Testa caracteres especiais.
     */
    @Test
    fun testEscapeCharacters() {

        val json =
            proJson.toJson(
                "Ana\nMaria"
            )

        assertTrue(
            json.toString()
                .contains("\\n")
        )
    }

    /**
     * Testa múltiplas serializações independentes.
     */
    @Test
    fun testIndependentSerializations() {

        val p1 =
            proJson.toJsonString(
                Person("Ana")
            )

        val p2 =
            proJson.toJsonString(
                Person("Joao")
            )

        assertNotEquals(
            p1,
            p2
        )
    }
}