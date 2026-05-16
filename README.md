# ProJson

Lightweight JSON serialization framework implemented in Kotlin using:

- Reflection
- Composite Pattern
- Visitor Pattern
- Functional APIs
- Annotation-based serialization
- Plugin architecture
- Graph serialization

---

# Table of Contents

1. Introduction
2. Features
3. Project Structure
4. Architecture
5. Core JSON Model
6. Serialization Process
7. Reflection-based Serialization
8. Annotations
9. Plugins
10. Graph Serialization
11. Visitor Pattern
12. Functional Operations
13. Examples
14. Tests
15. Design Decisions
16. Limitations
17. Future Improvements
18. Conclusion

---

# 1. Introduction

ProJson is a lightweight JSON framework developed in Kotlin for educational purposes in the Advanced Programming course.

The project demonstrates the application of several advanced programming concepts including:

- Reflection
- Composite Pattern
- Visitor Pattern
- Functional Programming
- Dynamic serialization
- Plugin architecture
- Graph serialization

The framework converts Kotlin objects into a JSON tree representation and supports recursive traversal, annotations, references, and custom serializers.

---

# 2. Features

## Core Features

- JSON object model
- Reflection-based object serialization
- Support for:
  - primitives
  - collections
  - arrays
  - maps
  - nested objects
- Visitor traversal
- Functional operations

---

## Annotation Support

- `@JsonIgnore`
- `@JsonProperty`
- `@Reference`
- `@JsonString`

---

## Advanced Features

- Plugin-based serializers
- Graph serialization
- Circular reference handling
- Shared reference detection
- `$id` / `$ref` generation

---

# 3. Project Structure

```text
src/
├── main/
│   └── kotlin/
│       └── projjson/
│           ├── annotations/
│           ├── core/
│           ├── model/
│           ├── plugins/
│           └── utils/
│
└── test/
    └── kotlin/
        └── projjson/
            ├── PrimitiveJsonTest.kt
            ├── CollectionJsonTest.kt
            ├── ObjectSerializationTest.kt
            ├── AnnotationTest.kt
            ├── PluginTest.kt
            ├── GraphSerializationTest.kt
            ├── VisitorTest.kt
            ├── JsonApiTest.kt
            └── ValidationTest.kt
```

---

# 4. Architecture

The framework is based on multiple architectural patterns.

## 4.1 Composite Pattern

The JSON tree is represented through a composite hierarchy.

### Base Component

```kotlin
abstract class JsonValue
```

### Composite Nodes

- `JsonObject`
- `JsonArray`

### Leaf Node

- `JsonPrimitive`

This structure allows recursive traversal of any JSON structure.

---

## 4.2 Visitor Pattern

All JSON nodes implement:

```kotlin
fun accept(visitor: (JsonValue) -> Unit)
```

The visitor recursively traverses the complete JSON tree using depth-first traversal.

---

## 4.3 Reflection-based Serialization

Reflection is used to dynamically inspect Kotlin classes and serialize their properties.

The framework uses:

```kotlin
primaryConstructor.parameters
```

to preserve property declaration order.

---

# 5. Core JSON Model

## JsonPrimitive

Represents:

- String
- Number
- Boolean
- null

Example:

```json
"Hello"
```

---

## JsonArray

Represents JSON arrays.

Example:

```json
[1, 2, 3]
```

---

## JsonObject

Represents JSON objects.

Example:

```json
{
  "name": "Ana"
}
```

---

# 6. Serialization Process

Serialization is performed recursively.

Main entry point:

```kotlin
ProJson().toJson(obj)
```

or

```kotlin
ProJson().toJsonString(obj)
```

## Serialization Flow

```text
Object
   ↓
Reflection
   ↓
JsonValue hierarchy
   ↓
JSON string
```

---

# 7. Reflection-based Serialization

Objects are dynamically serialized using Reflection.

Example:

```kotlin
data class Person(
    val name: String,
    val age: Int
)
```

Result:

```json
{
  "$type": "Person",
  "name": "Ana",
  "age": 25
}
```

## Property Discovery

Properties are resolved using:

```kotlin
clazz.primaryConstructor?.parameters
```

This guarantees stable property ordering.

---

# 8. Annotations

## 8.1 @JsonIgnore

Ignores a property during serialization.

Example:

```kotlin
@JsonIgnore
val password: String
```

---

## 8.2 @JsonProperty

Renames a property in JSON output.

Example:

```kotlin
@JsonProperty("full_name")
val name: String
```

Result:

```json
{
  "full_name": "Ana"
}
```

---

## 8.3 @Reference

Marks a property as reference-enabled.

Used for:
- shared references
- graph serialization
- circular references

---

## 8.4 @JsonString

Associates a custom serializer plugin.

Example:

```kotlin
@JsonString(DateAsText::class)
data class Date(...)
```

---

# 9. Plugins

Plugins allow replacing the default Reflection serializer.

## Plugin Interface

```kotlin
interface JsonSerializer<T> {

    fun serialize(value: T): String
}
```

## Example Plugin

```kotlin
class DateAsText
    : JsonSerializer<Date> {

    override fun serialize(
        value: Date
    ): String {

        return "${value.day}/${value.month}/${value.year}"
    }
}
```

## Result

```json
"30/2/2026"
```

instead of:

```json
{
  "$type": "Date"
}
```

---

# 10. Graph Serialization

The framework supports object graphs and circular references.

## Identity Tracking

Objects are tracked using:

```kotlin
IdentityHashMap
```

Identity comparison is required to correctly detect:

- circular references
- shared objects
- repeated instances

## $id and $ref

Objects receive unique identifiers:

```json
{
  "$id": "1"
}
```

Repeated references generate:

```json
{
  "$ref": "1"
}
```

## Circular Reference Example

```json
{
  "$id": "1",
  "$type": "PersonRef",
  "name": "Ana",
  "friend": {
    "$id": "2",
    "$type": "PersonRef",
    "name": "Joao",
    "friend": {
      "$ref": "1"
    }
  }
}
```

---

# 11. Visitor Pattern

The Visitor API allows recursive traversal of the JSON tree.

Example:

```kotlin
json.accept {
    println(it)
}
```

## Traversal Strategy

Traversal is performed using:
- depth-first traversal
- recursive visitation

---

# 12. Functional Operations

The framework supports functional operations over JSON trees.

Examples:

- `filter`
- `find`
- `count`

Example:

```kotlin
val numbers =
    json.filter {
        it is JsonPrimitive
    }
```

---

# 13. Examples

## Primitive Serialization

```kotlin
ProJson().toJsonString("Ana")
```

Result:

```json
"Ana"
```

---

## Object Serialization

```kotlin
Person("Ana", 25)
```

Result:

```json
{
  "$type": "Person",
  "name": "Ana",
  "age": 25
}
```

---

## Nested Objects

```json
{
  "$type": "User",
  "name": "Ana",
  "address": {
    "$type": "Address",
    "city": "Lisboa"
  }
}
```

---

## Circular References

```json
{
  "$ref": "1"
}
```

---

# 14. Tests

The project includes organized unit tests covering:

| Test File | Purpose |
|---|---|
| PrimitiveJsonTest | Primitive values |
| CollectionJsonTest | Collections and arrays |
| ObjectSerializationTest | Reflection serialization |
| AnnotationTest | Annotation behavior |
| PluginTest | Plugin architecture |
| GraphSerializationTest | References and cycles |
| VisitorTest | Visitor traversal |
| JsonApiTest | JSON model API |
| ValidationTest | Validation and edge cases |

---

# 15. Design Decisions

## Reflection over Manual Mapping

Reflection was chosen to:
- reduce boilerplate
- dynamically support arbitrary classes

## IdentityHashMap

Reference tracking uses object identity instead of equality.

This avoids incorrect graph merging.

## Annotation-driven Configuration

Annotations provide declarative serialization customization.

## Recursive Tree Structure

The JSON hierarchy uses recursive composition to simplify traversal and extensibility.

---

# 16. Limitations

Current limitations include:

- no JSON parser (deserialization)
- no polymorphic type registry
- no streaming serialization
- no performance optimization for very large graphs

---

# 17. Future Improvements

Possible future extensions:

- JSON deserialization
- generic type support
- configurable formatting
- pretty printing
- polymorphic serialization
- streaming writer
- custom naming strategies

---

# 18. Conclusion

ProJson demonstrates how advanced programming concepts can be combined to implement a flexible JSON serialization framework.

The project integrates:
- Reflection
- Visitor Pattern
- Composite Pattern
- Functional Programming
- Plugin systems
- Graph serialization

while maintaining a lightweight and extensible architecture.
