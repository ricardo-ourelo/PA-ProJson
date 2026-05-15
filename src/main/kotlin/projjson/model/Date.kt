package projjson.model

import projjson.annotations.JsonString
import projjson.plugins.DateAsText

/**
 * Representa uma data simples.
 *
 * Utiliza serialização customizada
 * através de @JsonString.
 */

@JsonString(DateAsText::class)
data class Date(
    val day: Int,
    val month: Int,
    val year: Int
)