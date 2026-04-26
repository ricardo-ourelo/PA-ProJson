package projjson.model

sealed class JsonValue {
    abstract fun accept(visitor: (JsonValue) -> Unit)
}