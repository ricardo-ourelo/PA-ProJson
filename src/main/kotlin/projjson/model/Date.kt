package projjson.model

import projjson.annotations.JsonString
import projjson.plugins.DateAsText

@JsonString(DateAsText::class)
data class Date(
    val day: Int,
    val month: Int,
    val year: Int
)