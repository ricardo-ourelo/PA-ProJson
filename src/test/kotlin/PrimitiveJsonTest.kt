package projjson

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import projjson.core.ProJson
import projjson.model.JsonPrimitive

class PrimitiveJsonTest {

    private val proJson = ProJson()

    /**
     * Testa serialização de String.
     */
    @Test
    fun testStringPrimitive() {

        val json =
            proJson.toJson("Ana")

        assertInstanceOf(
            JsonPrimitive::class.java,
            json
        )

        assertEquals(
            "\"Ana\"",
            json.toString()
        )
    }

    /**
     * Testa serialização de Int.
     */
    @Test
    fun testIntPrimitive() {

        val json =
            proJson.toJson(25)

        assertInstanceOf(
            JsonPrimitive::class.java,
            json
        )

        assertEquals(
            "25",
            json.toString()
        )
    }

    /**
     * Testa serialização de Double.
     */
    @Test
    fun testDoublePrimitive() {

        val json =
            proJson.toJson(10.5)

        assertInstanceOf(
            JsonPrimitive::class.java,
            json
        )

        assertEquals(
            "10.5",
            json.toString()
        )
    }

    /**
     * Testa serialização de Boolean true.
     */
    @Test
    fun testBooleanTrue() {

        val json =
            proJson.toJson(true)

        assertInstanceOf(
            JsonPrimitive::class.java,
            json
        )

        assertEquals(
            "true",
            json.toString()
        )
    }

    /**
     * Testa serialização de Boolean false.
     */
    @Test
    fun testBooleanFalse() {

        val json =
            proJson.toJson(false)

        assertInstanceOf(
            JsonPrimitive::class.java,
            json
        )

        assertEquals(
            "false",
            json.toString()
        )
    }

    /**
     * Testa serialização de null.
     */
    @Test
    fun testNullPrimitive() {

        val json =
            proJson.toJson(null)

        assertInstanceOf(
            JsonPrimitive::class.java,
            json
        )

        assertEquals(
            "null",
            json.toString()
        )
    }

    /**
     * Testa escape de aspas.
     */
    @Test
    fun testEscapeQuotes() {

        val json =
            proJson.toJson(
                "Ana \"Maria\""
            )

        assertEquals(
            "\"Ana \\\"Maria\\\"\"",
            json.toString()
        )
    }

    /**
     * Testa escape de newline.
     */
    @Test
    fun testEscapeNewLine() {

        val json =
            proJson.toJson(
                "Ana\nMaria"
            )

        assertEquals(
            "\"Ana\\nMaria\"",
            json.toString()
        )
    }

    /**
     * Testa rejeição de NaN.
     */
    @Test
    fun testNaNValidation() {

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            proJson.toJson(Double.NaN)
        }
    }

    /**
     * Testa rejeição de infinito.
     */
    @Test
    fun testInfinityValidation() {

        assertThrows(
            IllegalArgumentException::class.java
        ) {

            proJson.toJson(
                Double.POSITIVE_INFINITY
            )
        }
    }
}