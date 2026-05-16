package projjson

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import projjson.core.ProJson
import projjson.model.JsonArray
import projjson.model.JsonPrimitive

class CollectionJsonTest {

    private val proJson = ProJson()

    /**
     * Testa serialização de List<String>.
     */
    @Test
    fun testStringList() {

        val json =
            proJson.toJson(
                listOf(
                    "Ana",
                    "Maria"
                )
            )

        assertInstanceOf(
            JsonArray::class.java,
            json
        )

        val array =
            json as JsonArray

        assertEquals(
            2,
            array.size()
        )

        assertEquals(
            "\"Ana\"",
            array[0].toString()
        )

        assertEquals(
            "\"Maria\"",
            array[1].toString()
        )
    }

    /**
     * Testa serialização de List<Int>.
     */
    @Test
    fun testIntList() {

        val json =
            proJson.toJson(
                listOf(1, 2, 3)
            )

        val array =
            json as JsonArray

        assertEquals(
            3,
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

        assertEquals(
            "3",
            array[2].toString()
        )
    }

    /**
     * Testa serialização de Array<String>.
     */
    @Test
    fun testStringArray() {

        val json =
            proJson.toJson(
                arrayOf(
                    "A",
                    "B"
                )
            )

        val array =
            json as JsonArray

        assertEquals(
            2,
            array.size()
        )

        assertEquals(
            "\"A\"",
            array[0].toString()
        )

        assertEquals(
            "\"B\"",
            array[1].toString()
        )
    }

    /**
     * Testa serialização de IntArray.
     */
    @Test
    fun testPrimitiveIntArray() {

        val json =
            proJson.toJson(
                intArrayOf(10, 20, 30)
            )

        val array =
            json as JsonArray

        assertEquals(
            3,
            array.size()
        )

        assertEquals(
            "10",
            array[0].toString()
        )

        assertEquals(
            "20",
            array[1].toString()
        )

        assertEquals(
            "30",
            array[2].toString()
        )
    }

    /**
     * Testa serialização de DoubleArray.
     */
    @Test
    fun testPrimitiveDoubleArray() {

        val json =
            proJson.toJson(
                doubleArrayOf(
                    1.5,
                    2.5
                )
            )

        val array =
            json as JsonArray

        assertEquals(
            2,
            array.size()
        )

        assertEquals(
            "1.5",
            array[0].toString()
        )

        assertEquals(
            "2.5",
            array[1].toString()
        )
    }

    /**
     * Testa coleções aninhadas.
     */
    @Test
    fun testNestedCollections() {

        val json =
            proJson.toJson(

                listOf(
                    listOf(1, 2),
                    listOf(3, 4)
                )
            )

        val root =
            json as JsonArray

        assertEquals(
            2,
            root.size()
        )

        val first =
            root[0] as JsonArray

        val second =
            root[1] as JsonArray

        assertEquals(
            "1",
            first[0].toString()
        )

        assertEquals(
            "2",
            first[1].toString()
        )

        assertEquals(
            "3",
            second[0].toString()
        )

        assertEquals(
            "4",
            second[1].toString()
        )
    }

    /**
     * Testa lista vazia.
     */
    @Test
    fun testEmptyList() {

        val json =
            proJson.toJson(
                emptyList<String>()
            )

        val array =
            json as JsonArray

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
     * Testa array vazio.
     */
    @Test
    fun testEmptyArray() {

        val json =
            proJson.toJson(
                emptyArray<String>()
            )

        val array =
            json as JsonArray

        assertEquals(
            0,
            array.size()
        )
    }

    /**
     * Testa lista com null.
     */
    @Test
    fun testCollectionWithNull() {

        val json =
            proJson.toJson(
                listOf(
                    "Ana",
                    null,
                    "Maria"
                )
            )

        val array =
            json as JsonArray

        assertEquals(
            3,
            array.size()
        )

        assertEquals(
            "\"Ana\"",
            array[0].toString()
        )

        assertEquals(
            "null",
            array[1].toString()
        )

        assertEquals(
            "\"Maria\"",
            array[2].toString()
        )
    }

    /**
     * Testa estrutura interna da coleção.
     */
    @Test
    fun testCollectionElementTypes() {

        val json =
            proJson.toJson(
                listOf(
                    "Ana",
                    10,
                    true
                )
            )

        val array =
            json as JsonArray

        assertInstanceOf(
            JsonPrimitive::class.java,
            array[0]
        )

        assertInstanceOf(
            JsonPrimitive::class.java,
            array[1]
        )

        assertInstanceOf(
            JsonPrimitive::class.java,
            array[2]
        )
    }


    @Test
    fun testCollectionToString() {

        val json =
            proJson.toJson(
                listOf(1, 2, 3)
            )

        assertEquals(
            "[1, 2, 3]",
            json.toString()
        )
    }


    @Test
    fun testNestedCollectionToString() {

        val json =
            proJson.toJson(

                listOf(
                    listOf(1, 2),
                    listOf(3, 4)
                )
            )

        assertEquals(
            "[[1, 2], [3, 4]]",
            json.toString()
        )
    }


    @Test
    fun testStringCollectionToString() {

        val json =
            proJson.toJson(

                listOf(
                    "Ana",
                    "Maria"
                )
            )

        assertEquals(
            "[\"Ana\", \"Maria\"]",
            json.toString()
        )
    }
}