package projjson.plugins

import projjson.model.Date

/**
 * Serializa Date como texto.
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