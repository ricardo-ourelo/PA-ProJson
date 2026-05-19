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
2. Installation
3. Quick Start
4. Features
5. Project Structure
6. Architecture
7. Core JSON Model
8. Serialization Process
9. Reflection-based Serialization
10. Usage Tutorial
11. Annotations
12. Plugins
13. Graph Serialization
14. Visitor Pattern
15. Functional Operations
16. Examples
17. Tests
18. Design Decisions
19. Limitations
20. Future Improvements
21. Conclusion

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

# 2. Installation

## Requirements

- Kotlin 2.x
- JDK 17+
- Gradle
- JUnit 5

## Clone Repository

```bash
git clone <repository-url>
```

## Build Project

```bash
./gradlew build
```

## Run Tests

```bash
./gradlew test
```

## Generated JAR

The generated JAR will be available in:

```text
build/libs/
```

---

# 3. Quick Start

Basic Serialization:
```kotlin
data class Person(
    val name: String,
    val age: Int
)

fun main() {

    val json =
        ProJson().toJsonString(
            Person("Ana", 25)
        )

    println(json)
}
```
Output:
```JSON
{
  "$type": "Person",
  "name": "Ana",
  "age": 25
}
```

# 4. Features

## Core Features

- JSON object model
- Reflection-based object serialization
- Visitor traversal
- Functional operations

---

## Supported Types

The framework supports serialization of:

- String
- Number
- Boolean
- null
- Lists
- Arrays
- Primitive Arrays
- Maps
- Nested Objects
- Data Classes

---

## Validation Rules

The framework validates unsupported JSON values during serialization.

Validation rules include:

- NaN values are rejected
- Infinite numbers are rejected
- Map keys must be strings
- Invalid values generate IllegalArgumentException.

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

# 5. Project Structure

```text
src/
├── main/
│   └── kotlin/
│       └── projjson/
│           ├── annotations/
│           ├── core/
│           ├── model/
│           └── plugins/
│           
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

# 6. Architecture

The framework is based on multiple architectural patterns.

## 6.1 Composite Pattern

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

## 6.2 Visitor Pattern

All JSON nodes implement:

```kotlin
fun accept(visitor: (JsonValue) -> Unit)
```

The visitor recursively traverses the complete JSON tree using depth-first traversal.

Traversal visits:
- the current node first
- child nodes recursively afterwards

This allows:
- recursive tree traversal
- functional operations
- tree inspection
- node filtering

---

## 6.3 Reflection-based Serialization

Reflection is used to dynamically inspect Kotlin classes and serialize their properties.

The framework uses:

```kotlin
primaryConstructor.parameters
```

to preserve property declaration order.

---

# 7. Core JSON Model

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

# 8. Serialization Process

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

# 9. Reflection-based Serialization

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

# 10. Usage Tutorial

## Serialize Primitive Values

```kotlin
println(
    ProJson().toJsonString(10)
)
```
Result:
```JSON
10
```
## Serialize Collections
```kotlin
val json =
    ProJson().toJsonString(
        listOf(1, 2, 3)
    )

println(json)
```
Result:
```JSON
[1, 2, 3]
```
## Serialize Maps
```kotlin
val json =
    ProJson().toJsonString(
        mapOf(
            "x" to 10,
            "y" to 20
        )
    )
```
Result:
```JSON
{
  "x": 10,
  "y": 20
}
```
## Serialize Data Classes
```kotlin
data class User(
    val name: String,
    val age: Int
)
```
```kotlin
val json =
    ProJson().toJsonString(
        User("Ana", 25)
    )
```
Result:
```JSON
{
  "$type": "User",
  "name": "Ana",
  "age": 25
}
```
## Nested Structures
```kotlin
val data = mapOf(
    "name" to "Ana",
    "numbers" to listOf(1, 2, 3)
)
```
Result:
```JSON
{
  "name": "Ana",
  "numbers": [1, 2, 3]
}
```
## Graph Serialization

Graph serialization is supported through identity tracking and `@Reference` annotations.

The framework supports:

- circular references
- shared objects
- object graphs
- cycle prevention

Properties annotated with `@Reference`
participate in graph serialization.

Objects reachable through these properties may generate:

- `$id`
- `$ref`

during serialization.

The serializer automatically tracks object identity using:

```kotlin
IdentityHashMap
```

IdentityHashMap is required because graph serialization depends on object identity rather than structural equality.

This allows the framework to correctly detect:

- repeated objects
- shared references
- circular structures

Example:

```kotlin
data class Person(

    val name: String,

    @Reference
    val friend: Person?
)
```

```kotlin
val ana =
    Person("Ana", null)

val joao =
    Person("Joao", ana)

ana.friend = joao

val json =
    ProJson().toJsonString(ana)
```

Result:

```json
{
  "$id": "1",
  "$type": "Person",
  "name": "Ana",
  "friend": {
    "$id": "2",
    "$type": "Person",
    "name": "Joao",
    "friend": {
      "$ref": "1"
    }
  }
}
```

Repeated objects generate:

```json
{
  "$ref": "1"
}
```

instead of duplicating the object again.

---

# 11. Annotations

## 11.1 @JsonIgnore

Ignores a property during serialization.

Example:

```kotlin
@JsonIgnore
val password: String
```

---

## 11.2 @JsonProperty

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

## 11.3 @Reference

Marks a property as eligible for object reference reuse using `$ref`.

Used for:
- shared references
- graph serialization
- circular references
- cycle prevention

Example:

```kotlin
data class Task(

    val description: String,

    @Reference
    val dependency: Task?
)
```

Result:

```json
{
  "$type": "Task",
  "description": "T2",
  "dependency": {
    "$id": "1",
    "$type": "Task",
    "description": "T1",
    "dependency": null
  }
}
```

---

## 11.4 @JsonString

Associates a custom serializer plugin.

Example:

```kotlin
@JsonString(DateAsText::class)
data class Date(...)
```

---

# 12. Plugins

Plugins allow replacing the default Reflection-based serialization strategy.

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

---

## Plugin Usage

```kotlin
@JsonString(DateAsText::class)
data class Date(

    val day: Int,
    val month: Int,
    val year: Int
)
```

---

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

# 13. Graph Serialization

The framework supports object graphs and circular references.

## Identity Tracking

Objects are tracked using:

```kotlin
IdentityHashMap
```

IdentityHashMap was required because graph serialization depends on object identity rather than structural equality.

Identity comparison is required to correctly detect:

- circular references
- shared objects
- repeated instances

---

## `$id` and `$ref`

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

# 14. Visitor Pattern

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

# 15. Functional Operations

The framework supports functional operations over JSON trees.

Examples:

- `filter`
- `find`
- `count`
- `map`

Example:

```kotlin
val numbers =
    json.filter {
        it is JsonPrimitive
    }
```

---

# 16. Examples

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

# 17. Tests

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

# 18. Design Decisions

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

# 19. Limitations

Current limitations include:

- no JSON parser (deserialization)
- no polymorphic type registry
- no streaming serialization
- no performance optimization for very large graphs

---

# 20. Future Improvements

Possible future extensions:

- JSON deserialization
- generic type support
- configurable formatting
- pretty printing
- polymorphic serialization
- streaming writer
- custom naming strategies

---

# 21. Conclusion

ProJson demonstrates how advanced programming concepts can be combined to implement a flexible and extensible JSON serialization framework.

The project integrates:
- Reflection
- Visitor Pattern
- Composite Pattern
- Functional Programming
- Plugin architecture
- Graph serialization

The framework supports:
- dynamic object serialization
- annotation-based customization
- recursive tree traversal
- plugin-based serializers
- circular reference handling
- object graph serialization

while maintaining a lightweight and modular architecture.

The project also demonstrates how design patterns and Reflection can be combined to build extensible serialization systems in Kotlin.