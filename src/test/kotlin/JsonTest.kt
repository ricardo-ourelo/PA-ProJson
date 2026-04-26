import projjson.core.ProJson
import projjson.model.JsonArray
import projjson.model.JsonObject
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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
            $$"""{"$type": "Person", "name": "Ana", "age": 25}""",
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
}