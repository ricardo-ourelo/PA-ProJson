package projjson.plugins

import projjson.model.Date

/**
 * Serializer customizado para Date.
 *
 * Converte objetos Date para
 * representação textual simples.
 */

class DateAsText : JsonSerializer<Date> {

    /**
     * Converte Date para texto no formato:
     *
     * dia/mês/ano
     *
     * @param value data a serializar
     *
     * @return representação textual da data
     */

    override fun serialize(
        value: Date
    ): String {

        return "${value.day}/" +
                "${value.month}/" +
                "${value.year}"
    }
}