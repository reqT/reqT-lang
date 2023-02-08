# Specification of the reqt language version 1.0
## Preface

Reqt is a language for software requirements modelling.

A reqt model is a sequence of elements, where each element can be either an entity, an attribute or a relation.
* An **entity** has an **entity type**, e.g. `Feature`, `UseCase`, and a **unique id**. The id is a string of any character except whitespace.
* An **attribute** has an **attribute type**, e.g. `Prio`, `Spec`, and a **value**. The attribute type determines if the value is either an integer, e.g. Prio 12, or a string, e.g. `Spec a string of characters`.
* A **relation** recursively combines an entity and a **relation type** with a non-empty sub-model. 

Example:
```
Comment A string attribute with informative text.
Feature yyy has Prio 42
Feature yyy requires Feature xxx
Feature xxx has 
  Prio 12
  Spec An informal description
  UseCase zzz has
    Prio 23
```

## Lexical Syntax

`reqt` is case sensitive, has significant indentation, and use UTF-8 encoding. 
The input is split into lines based on the newline character (Unicode `U+000A`). 
Each line is split into a sequence of the following tokens types:

* `Word` is a sequence of non-whitespace characters, according to `isWhitespace` on `Char` in Scala.

* `Num` is an integer literal following the same rules as a defined `toIntOption` on `String` in Scala.

* `Indent(n)` is a possibly empty sequence of either space or tab characters at the beginning of a line. 
  - It is illegal to mix spaces and tabs at the beginning of a line. 
  - Each tab is converted to 2 space characters.
  - The indent level `n` is defined as the number of spaces at the beginning of the line.

* An `<Outdent>` is inserted before an `Indent(n)` if `n < p` where `p` is the level of the previous `Indent(p)`.

 
### Model Syntax

A legal `reqt` model abides the following context-free grammar: 
```
EntityType ::= `Actor` | `App` | `Barrier` | `Breakpoint` | `Class` | `Component` | `Configuration` | `Data` | `Design` | `Domain` | `Epic` | `Event` | `Feature` | `Function` | `Goal` | `Idea` | `Interface` | `Item` | `Issue` | `Label` | `Meta` | `Member` | `Module` | `MockUp` | `Product` | `Quality` | `Relationship` | `Release` | `Req` | `Resource` | `Risk` | `Scenario` | `Screen` | `Section` | `Service` | `Stakeholder` | `State` | `Story` | `System` | `Target` | `Task` | `Term` | `Test` | `Ticket` | `UseCase` | `User` | `Variant` | `VariationPoint` | `WorkPackage`

IntAttributeType ::= `Benefit` | `Capacity` | `Cost` | `Damage` | `Frequency` | `Max` | `Min` | `Order` | `Prio` | `Probability` | `Profit` | `Value`

StringAttributeType ::= `Comment` | `Deprecated` | `Example` | `Expectation` | `FileName` | `Gist` | `Image` | `Input` | `Output` | `Spec` | `Status` | `Text` | `Title` | `Why`

RelationType ::= `Binds` | `Deprecates` | `Excludes` | `Has` | `Helps` | `Hurts` | `Impacts` | `Implements` | `InteractsWith` | `Is` | `Precedes` | `RelatesTo` | `Requires` | `SuperOf` | `Verifies`

Model ::= Line*

Line ::= Indent(n) Elem

Elem ::= Attribute | Entity | Relation

Attribute ::= IntAttribute |  StringAttribute

IntAttribute ::= IntAttributeType Num

StringAttribute ::= StringAttributeType (Word)*

Entity ::= EntityType Id

Id :: = Word

Relation ::= SingleLineRelation | MultiLineRelation

SingleLineRelation ::= Entity RelationType Attribute

MultiLineRelation ::= Entity RelationType SubModel

SubModel ::= Model <Outdent>
```

### Parsing rules

1. If a line starts with a `StringAttributeType` then the rest of the line is part of its string value, as well as subsequent lines with a higher Indent level.

2. If a line starts with an `IntAttributeType` then the rest of the line must be a single Num token.

3. An Id string cannot be any of the strings `EntityType`, `IntAttributeType`, `StringAttributeType`, `RelationType`.

4. `Attribute`, `Entity`, `SingleLineRelation`, cannot be followed by a subsequent line with a higher Indent level.

5. The elements following a `MultiLineRelation` that are on a higher Indent level are part of the (possibly empty) `SubModel`.
