package projjson

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import projjson.annotations.JsonIgnore
import projjson.annotations.JsonProperty
import projjson.annotations.Reference
import projjson.core.ProJson
import projjson.model.JsonObject

class AnnotationTest {

    private val proJson = ProJson()

    /**
     * Classe para testar @JsonIgnore.
     */
    data class UserIgnore(

        val name: String,

        @JsonIgnore
        val password: String
    )

    /**
     * Classe para testar @JsonProperty.
     */
    data class UserProperty(

        @JsonProperty("full_name")
        val name: String
    )

    /**
     * Classe para testar @Reference.
     */
    data class Task(

        val description: String,

        @Reference
        val dependency: Task?
    )

    /**
     * Classe para testar ciclos.
     */
    data class PersonRef(

        val name: String,

        @Reference
        var friend: PersonRef?
    )

    /**
     * Testa @JsonIgnore.
     */
    @Test
    fun testJsonIgnore() {

        val json =
            proJson.toJson(

                UserIgnore(
                    "Ana",
                    "123456"
                )
            )

        val obj =
            json as JsonObject

        assertNotNull(
            obj["name"]
        )

        assertNull(
            obj["password"]
        )

        val result =
            json.toString()

        assertTrue(
            result.contains("\"name\": \"Ana\"")
        )

        assertFalse(
            result.contains("password")
        )

        assertFalse(
            result.contains("123456")
        )
    }

    /**
     * Testa @JsonProperty.
     */
    @Test
    fun testJsonProperty() {

        val json =
            proJson.toJson(
                UserProperty("Ana")
            )

        val obj =
            json as JsonObject

        assertNotNull(
            obj["full_name"]
        )

        assertNull(
            obj["name"]
        )

        assertEquals(
            "\"Ana\"",
            obj["full_name"].toString()
        )

        val result =
            json.toString()

        assertTrue(
            result.contains("\"full_name\": \"Ana\"")
        )
    }

    /**
     * Testa @Reference.
     */
    @Test
    fun testReference() {

        val t1 =
            Task("T1", null)

        val t2 =
            Task("T2", t1)

        val json =
            proJson.toJsonString(t2)

        assertTrue(
            json.contains("\"\$id\"")
        )

        assertTrue(
            json.contains("\"description\": \"T1\"")
        )
    }

    /**
     * Testa referência cíclica.
     */
    @Test
    fun testCircularReference() {

        val ana =
            PersonRef("Ana", null)

        val joao =
            PersonRef("Joao", ana)

        ana.friend = joao

        val json =
            proJson.toJsonString(ana)

        assertTrue(
            json.contains("\"\$id\"")
        )

        assertTrue(
            json.contains("\"\$ref\"")
        )

        assertTrue(
            json.contains("\"name\": \"Ana\"")
        )

        assertTrue(
            json.contains("\"name\": \"Joao\"")
        )
    }

    /**
     * Testa múltiplas propriedades customizadas.
     */
    @Test
    fun testMultipleJsonProperties() {

        data class Employee(

            @JsonProperty("employee_name")
            val name: String,

            @JsonProperty("employee_age")
            val age: Int
        )

        val json =
            proJson.toJson(

                Employee(
                    "Carlos",
                    30
                )
            )

        val result =
            json.toString()

        assertTrue(
            result.contains(
                "\"employee_name\": \"Carlos\""
            )
        )

        assertTrue(
            result.contains(
                "\"employee_age\": 30"
            )
        )
    }

    /**
     * Testa combinação de annotations.
     */
    @Test
    fun testCombinedAnnotations() {

        data class Account(

            @JsonProperty("user_name")
            val username: String,

            @JsonIgnore
            val password: String
        )

        val json =
            proJson.toJson(

                Account(
                    "admin",
                    "123456"
                )
            )

        val result =
            json.toString()

        assertTrue(
            result.contains(
                "\"user_name\": \"admin\""
            )
        )

        assertFalse(
            result.contains("password")
        )
    }
}