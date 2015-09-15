#org.brutusin:json-provider [![Build Status](https://api.travis-ci.org/brutusin/json-provider.svg?branch=master)](https://travis-ci.org/brutusin/json-provider) [![Maven Central Latest Version](https://maven-badges.herokuapp.com/maven-central/org.brutusin/json-provider/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.brutusin/json-provider/)
Service provider for [org.brutusin:json SPI](https://github.com/brutusin/json), based on Jackson-stack:

* [FasterXML/jackson stack](https://github.com/FasterXML/jackson): The underlying JSON stack.
* [com.fasterxml.jackson.module:jackson-module-jsonSchema](https://github.com/FasterXML/jackson-module-jsonSchema): For java class to JSON schema mapping 
* [com.github.fge:json-schema-validator](https://github.com/fge/json-schema-validator): For validation against a JSON schema

##Expression DSL
This module defines its own expression semantics, supporting both data, and schema projections (wildcard expressions evaluating to multiple nodes), and also keeping explicit information of the schema structure.

Case | Expression
-----| -----------------
Root node| `$`
Simple property| `$.id`
Nested property| `$.header.id`
Array/Collection property| `$.items[#]`
Map property (additionalProperty in schema)| `$.map` for keys and `$.map[*]` for values


| Operator                  | Applied to JsonNode  | Applied to JsonSchema
| :------------------------ | :------------------- |:-------------------- |
| `$`                       | The root node        | Schema of root node |
| `.<name>`                 | Dot-notated child    | Schema of child node
| `#`                       | Numeric wildcard. Selects all elements of an array | Schema of the array node
| `*`                       | String wildcard. Selects all elements of an array | Schema of the object node. Only valid in schemas having additionalProperties
| `['<name>']` | Bracket-notated child or children | Only valid in schemas having additionalProperties. Otherwise use dot-notation |                                 |
| `[<number>]` | number-th element in the array                                            |Schema of the element node
| `[$]` | Last element in the array | Schema of the element node

## Support, bugs and requests
https://github.com/brutusin/json-provider/issues

## Authors

- Ignacio del Valle Alles (<https://github.com/idelvall/>)

Contributions are always welcome and greatly appreciated!

##License
Apache License, Version 2.0
