import projjson.core.ProJson
import projjson.model.JsonArray
import projjson.model.JsonObject
import org.junit.jupiter.api.Test
import projjson.model.JsonPrimitive
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import projjson.model.*

class JsonTest {

    data class Person(val name: String, val age: Int)

    @Test
    fun testSimpleObject() {
        val p = Person("Ana", 25)

        val json = ProJson().toJsonString(p)

        assertEquals(
            """{"${'$'}type": "Person", "name": "Ana", "age": 25}""",
            json
        )
    }

    @Test
    fun testList(): Unit {
        val list = listOf(1, 2, 3)

        val json = ProJson().toJsonString(list)

        assertEquals("[1, 2, 3]", json)
    }


    @Test
    fun testMap() {
        val map = mapOf("x" to 10, "y" to 20)

        val json = ProJson().toJsonString(map)

        assertEquals("""{"x": 10, "y": 20}""", json)
    }

    @Test
    fun testNested() { //estrutura complexa
        val data = mapOf(
            "name" to "Ana",
            "nums" to listOf(1, 2)
        )

        val json = ProJson().toJsonString(data)

        assertEquals("""{"name": "Ana", "nums": [1, 2]}""", json)
    }


    //Teste escape de string
    @Test
    fun testStringEscape() {
        val text = "Ana \"teste\" \\ caminho"

        val json = ProJson().toJsonString(text)

        assertEquals("\"Ana \\\"teste\\\" \\\\ caminho\"", json)
    }


    //Teste API JsonObject (set/get/remove)
    @Test
    fun testJsonObjectManipulation() {
        val obj = JsonObject()

        obj.set("name", "Ana")
        obj.set("age", 25)

        assertEquals("\"Ana\"", obj.get("name").toString())

        obj.remove("name")

        assertEquals(null, obj.get("name"))
    }


    //Teste JsonArray
    @Test
    fun testJsonArrayManipulation() {
        val arr = JsonArray()

        arr.add(1)
        arr.add(2)

        arr.set(1, 5)

        assertEquals("[1, 5]", arr.toString())

        arr.remove(0)

        assertEquals("[5]", arr.toString())
    }


    //8. Teste ordem correta (constructor)
    @Test
    fun testPropertyOrder() {
        val p = Person("Ana", 25)

        val json = ProJson().toJsonString(p)

        assertEquals(
            """{"${'$'}type": "Person", "name": "Ana", "age": 25}""",
            json
        )
    }


    //Teste Visitor
    @Test
    fun testVisitorCount() {
        val json = ProJson().toJson(Person("Ana", 25))

        var count = 0

        json.accept { count++ }

        assertEquals(4, count) // Nó	Conta JsonObject	1 JsonPrimitive("Person")	1 JsonPrimitive("Ana")	1 JsonPrimitive(25)	1
    }


//tesste null

    @Test
    fun testNull() {
        val json = ProJson().toJsonString(null)

        assertEquals("null", json)
    }

    @Test
    fun testInvalidNaN() {

        assertFailsWith<IllegalArgumentException> {
            JsonPrimitive(Double.NaN)
        }
    }

    // Testar Filter

    @Test
    fun testFilterMultipleMatches() {
        val json = ProJson().toJson(listOf(1, 2, 3, 2))

        val result = json.filter {
            it is JsonPrimitive && it.value == 2
        }

        assertEquals(2, result.size)
    }

    @Test
    fun testFilterNoMatches() {
        val json = ProJson().toJson(listOf(1, 2, 3))

        val result = json.filter {
            it is JsonPrimitive && it.value == 10
        }

        assertEquals(0, result.size)
    }

    //Testar Find

    @Test
    fun testFindExisting() {
        val json = ProJson().toJson(listOf(1, 2, 3))

        val result = json.find {
            it is JsonPrimitive && it.value == 2
        }

        assertEquals("2", result.toString())
    }

    @Test
    fun testFindNotExisting() {
        val json = ProJson().toJson(listOf(1, 2, 3))

        val result = json.find {
            it is JsonPrimitive && it.value == 10
        }

        assertEquals(null, result)
    }

    //Testar Count

    @Test
    fun testCountPrimitives() {
        val json = ProJson().toJson(listOf(1, 2, 3))

        val count = json.count {
            it is JsonPrimitive
        }

        assertEquals(3, count)
    }

    @Test
    fun testCountObjects() {
        val json = ProJson().toJson(
            mapOf("a" to 1, "b" to 2)
        )

        val count = json.count {
            it is JsonObject
        }

        assertEquals(1, count)
    }

    //Testar Map

    @Test
    fun testMapValues() {
        val json = ProJson().toJson(listOf(1, 2, 3))

        val result = json.map {
            if (it is JsonPrimitive) it.value else null
        }

        assertEquals(listOf(1, 2, 3), result.filterNotNull())
    }

    @Test
    fun testMapTypes() {
        val json = ProJson().toJson(
            mapOf("a" to listOf(1, 2))
        )

        val result = json.map {
            when (it) {
                is JsonObject -> "object"
                is JsonArray -> "array"
                is JsonPrimitive -> "primitive"
            }
        }

        // Deve conter pelo menos um de cada
        assert(result.contains("object"))
        assert(result.contains("array"))
        assert(result.contains("primitive"))
    }

    // Testar Objetos Dentro De Json Manual

    @Test
    fun testObjectInsideObject() {
        val obj = JsonObject()
        val inner = JsonObject()

        inner.set("x", 10)
        obj.set("inner", inner)

        assertEquals("""{"inner": {"x": 10}}""", obj.toString())
    }

    // Testar Listas dentro de Json Manual

    @Test
    fun testArrayInsideObject() {
        val obj = JsonObject()
        val arr = JsonArray()

        arr.add(1)
        arr.add(2)

        obj.set("nums", arr)

        assertEquals("""{"nums": [1, 2]}""", obj.toString())
    }

    // Testar Map dentro de JsonObject

    @Test
    fun testMapInsideObject() {
        val obj = JsonObject()

        obj.set("data", mapOf("a" to 1))

        assertEquals("""{"data": {"a": 1}}""", obj.toString())
    }
}