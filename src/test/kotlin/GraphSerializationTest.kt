package projjson

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import projjson.annotations.Reference
import projjson.core.ProJson

class GraphSerializationTest {

    private val proJson = ProJson()

    /**
     * Classe para testes de referências.
     */
    data class Task(

        val description: String,

        @Reference
        val dependency: Task?
    )

    /**
     * Classe para testes de ciclos.
     */
    data class PersonRef(

        val name: String,

        @Reference
        var friend: PersonRef?
    )

    /**
     * Testa geração de $id.
     */
    @Test
    fun testReferenceIdGeneration() {

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
     * Testa geração de $ref.
     */
    @Test
    fun testReferenceReuse() {

        val shared =
            Task("Shared", null)

        val t1 =
            Task("T1", shared)

        val t2 =
            Task("T2", shared)

        val json1 =
            proJson.toJsonString(t1)

        val json2 =
            proJson.toJsonString(t2)

        assertTrue(
            json1.contains("\"\$id\"")
        )

        assertTrue(
            json2.contains("\"\$id\"")
        )
    }

    /**
     * Testa referência circular.
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
     * Testa se ciclo não gera
     * recursão infinita.
     */
    @Test
    fun testCircularReferenceTerminates() {

        val ana =
            PersonRef("Ana", null)

        ana.friend = ana

        val json =
            proJson.toJsonString(ana)

        assertTrue(
            json.contains("\"\$ref\"")
        )
    }

    /**
     * Testa múltiplas referências
     * para mesmo objeto.
     */
    @Test
    fun testSharedReference() {

        val shared =
            Task("Database", null)

        val task =
            Task(
                "API",
                shared
            )

        val json =
            proJson.toJsonString(task)

        assertTrue(
            json.contains("\"\$id\"")
        )

        assertTrue(
            json.contains("Database")
        )
    }

    /**
     * Testa IDs diferentes.
     */
    @Test
    fun testDifferentIds() {

        val t1 =
            Task("T1", null)

        val t2 =
            Task("T2", t1)

        val json =
            proJson.toJsonString(t2)

        val firstId =
            "\"\$id\": \"1\""

        assertTrue(
            json.contains(firstId)
        )
    }

    /**
     * Testa referência profunda.
     */
    @Test
    fun testDeepGraphReference() {

        val t1 =
            Task("T1", null)

        val t2 =
            Task("T2", t1)

        val t3 =
            Task("T3", t2)

        val json =
            proJson.toJsonString(t3)

        assertTrue(
            json.contains("\"description\": \"T1\"")
        )

        assertTrue(
            json.contains("\"description\": \"T2\"")
        )

        assertTrue(
            json.contains("\"description\": \"T3\"")
        )
    }

    /**
     * Testa raiz com $id.
     */
    @Test
    fun testRootObjectReceivesId() {

        val person =
            PersonRef(
                "Ana",
                null
            )

        val json =
            proJson.toJsonString(person)

        assertTrue(
            json.contains("\"\$id\"")
        )
    }

    /**
     * Testa consistência de referência.
     */
    @Test
    fun testReferenceConsistency() {

        val ana =
            PersonRef("Ana", null)

        val joao =
            PersonRef("Joao", ana)

        ana.friend = joao

        val json =
            proJson.toJsonString(ana)

        assertTrue(
            json.contains("\"\$ref\": \"1\"")
        )
    }

    /**
     * Testa serialização de grafo complexo.
     */
    @Test
    fun testComplexGraph() {

        val a =
            PersonRef("A", null)

        val b =
            PersonRef("B", null)

        val c =
            PersonRef("C", null)

        a.friend = b
        b.friend = c
        c.friend = a

        val json =
            proJson.toJsonString(a)

        assertTrue(
            json.contains("\"name\": \"A\"")
        )

        assertTrue(
            json.contains("\"name\": \"B\"")
        )

        assertTrue(
            json.contains("\"name\": \"C\"")
        )

        assertTrue(
            json.contains("\"\$ref\"")
        )

        print(json)
    }
}