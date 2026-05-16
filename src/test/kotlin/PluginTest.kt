package projjson

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import projjson.annotations.JsonString
import projjson.core.ProJson
import projjson.plugins.JsonSerializer


@JsonString(TextSerializer::class)
data class Text(
    val value: String
)

class TextSerializer
    : JsonSerializer<Text> {

    override fun serialize(
        value: Text
    ): String {

        return "TEXT: ${value.value}"
    }
}





class PluginTest {

    private val proJson = ProJson()

    /**
     * Classe Date para testes.
     */
    @JsonString(DateAsText::class)
    data class Date(
        val day: Int,
        val month: Int,
        val year: Int
    )

    /**
     * Plugin que serializa Date como texto.
     */
    class DateAsText : JsonSerializer<Date> {

        override fun serialize(
            value: Date
        ): String {

            return "${value.day}/" +
                    "${value.month}/" +
                    "${value.year}"
        }
    }

    /**
     * Classe Money.
     */
    @JsonString(MoneySerializer::class)
    data class Money(
        val amount: Double,
        val currency: String
    )

    /**
     * Plugin para Money.
     */
    class MoneySerializer
        : JsonSerializer<Money> {

        override fun serialize(
            value: Money
        ): String {

            return "${value.amount} " +
                    value.currency
        }
    }

    /**
     * Testa plugin DateAsText.
     */
    @Test
    fun testDatePlugin() {

        val json =
            proJson.toJson(
                Date(30, 2, 2026)
            )

        assertEquals(
            "\"30/2/2026\"",
            json.toString()
        )
    }

    /**
     * Testa serialização direta para String.
     */
    @Test
    fun testDatePluginToJsonString() {

        val json =
            proJson.toJsonString(
                Date(1, 1, 2025)
            )

        assertEquals(
            "\"1/1/2025\"",
            json
        )
    }

    /**
     * Testa plugin MoneySerializer.
     */
    @Test
    fun testMoneyPlugin() {

        val json =
            proJson.toJson(

                Money(
                    99.99,
                    "EUR"
                )
            )

        assertEquals(
            "\"99.99 EUR\"",
            json.toString()
        )
    }

    /**
     * Testa se plugin substitui
     * serialização Reflection padrão.
     */
    @Test
    fun testPluginOverridesReflection() {

        val json =
            proJson.toJson(
                Date(10, 5, 2024)
            )

        val result =
            json.toString()

        assertFalse(
            result.contains("\$type")
        )

        assertFalse(
            result.contains("day")
        )

        assertFalse(
            result.contains("month")
        )

        assertFalse(
            result.contains("year")
        )
    }

    /**
     * Testa múltiplas execuções do plugin.
     */
    @Test
    fun testMultiplePluginExecutions() {

        val d1 =
            proJson.toJsonString(
                Date(1, 1, 2024)
            )

        val d2 =
            proJson.toJsonString(
                Date(2, 2, 2025)
            )

        assertNotEquals(
            d1,
            d2
        )
    }

    /**
     * Testa plugin com caracteres especiais.
     */
    @Test
    fun testPluginSpecialCharacters() {

        val json =
            proJson.toJson(
                Text("Olá")
            )

        assertEquals(
            "\"TEXT: Olá\"",
            json.toString()
        )
    }

    /**
     * Testa plugin com espaços.
     */
    @Test
    fun testPluginWithSpaces() {

        val json =
            proJson.toJson(

                Money(
                    10.0,
                    "USD"
                )
            )

        assertTrue(
            json.toString()
                .contains("USD")
        )
    }
}