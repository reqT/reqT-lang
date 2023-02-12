# Language Specification of reqt version 4.0
## Preface

Reqt is a language for software requirements modelling.

A reqt Model is a sequence of elements, where each element can be either an entity, an attribute or a relation.
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
       that is continued on the next line
  UseCase zzz has
    Prio 23
```

## Lexical Tokens

`reqt` is case sensitive, has significant indentation, and use UTF-8 encoding. 
The input is split into lines based on the newline character (Unicode `U+000A`). 
Each line is split into a sequence of the following tokens types:

* `Word` is a sequence of non-whitespace characters, according to `isWhitespace` on the `Char` type in Scala.
* `Num` is an integer literal following the same rules as a defined `toIntOption` on `String` in Scala.
* `Indent(n)` is a possibly empty sequence of either space or tab characters at the beginning of a line. 
  - It is illegal to mix spaces and tabs at the beginning of a line. 
  - Each tab is converted to 2 space characters.
  - The indent level `n` is defined as the number of spaces at the beginning of the line.
* An `Outdent(n)` is inserted before an `Indent(n)` if `n < p` where `p` is the level of the previous `Indent(p)`.


## Model Syntax

A legal `reqt` model abides the following grammar, where `|` denotes alternative and `*` denotes zero or more: 
```
Model ::= (Indent(i) Elem)*

Elem ::= Node | Relation

Node ::= Attribute | Entity

Attribute ::= IntAttribute |  StringAttribute

IntAttribute ::= IntAttributeType Num

StringAttribute ::= StringAttributeType (Word)*

Entity ::= EntityType Id

Id :: = Word

Relation ::= SingleLineRelation | MultiLineRelation

SingleLineRelation ::= Entity RelationType Node

MultiLineRelation ::= Entity RelationType SubModel

SubModel ::= (Indent(j > i) Elem)* Outdent(i)

EntityType ::= `Actor` | `App` | `Barrier` | `Breakpoint` | `Class` | `Component` | `Configuration` | `Data` | 
`Design` | `Domain` | `Epic` | `Event` | `Err` | `Feature` | `Function` | `Goal` | `Idea` | 
`Interface` | `Item` | `Issue` | `Label` | `Meta` | `Member` | `Module` | `MockUp` | `Product` | 
`Quality` | `Relationship` | `Release` | `Req` | `Resource` | `Risk` | `Scenario` | `Screen` | 
`Section` | `Service` | `Stakeholder` | `State` | `Story` | `System` | `Target` | `Task` | 
`Term` | `Test` | `Ticket` | `UseCase` | `User` | `Variant` | `VariationPoint` | `WorkPackage`

IntAttributeType ::= `Benefit` | `Capacity` | `Cost` | `Damage` | `Frequency` | `Max` | `Min` | `Order` | `Prio` | 
`Probability` | `Profit` | `Value`

StringAttributeType ::= `Comment` | `Deprecated` | `Example` | `Expectation` | `FileName` | `Gist` | `Image` | `Input` 
| `Output` | `Spec` | `Status` | `Text` | `Title` | `Why`

RelationType ::= `Binds` | `Deprecates` | `Excludes` | `Has` | `Helps` | `Hurts` | `Impacts` | `Implements` | 
`InteractsWith` | `Is` | `Precedes` | `RelatesTo` | `Requires` | `SuperOf` | `Verifies`
```

## Special Parsing Rules

The following rules provides exceptions to the above grammar: 

1. An Id cannot be an `EntityType`, `IntAttributeType`, `StringAttributeType`, or `RelationType`.
This gives the Error: "Reserved word cannot be used as Id of Entity."

2. The rest of the line after a `StringAttributeType` is part of its string value,
as well as subsequent lines with a higher Indent level.

3. The rest of the line after an `IntAttributeType` is part of its integer value. 

4. If a `Num` token have more tokens following on the same line then the following error is given:
"Illegal extra tokens after integer value."

4. An `IntAttribute`, `Entity` or `SingleLineRelation` cannot be followed by a subsequent line with a higher Indent level.
This gives the Error: "Higher indentation level is not allowed here."

5. The elements following a `MultiLineRelation` that are on a higher Indent level are part of the (possibly empty) `SubModel`.
