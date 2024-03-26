# Language Specification of reqt version 4.0
## Introduction

Reqt is a language for software requirements modelling.

A reqt Model is a sequence of elements, where each element can be either an entity, an attribute or a relation.
* An **entity** has an **entity type**, e.g. `Feature`, `UseCase`, and a **unique id**. The id is a string of any character except whitespace.
* An **attribute** has an **attribute type**, e.g. `Prio`, `Spec`, and a **value**. The attribute type determines if the value is either an integer, e.g. Prio 12, or a string, e.g. `Spec a string of characters`.
* A **relation** recursively combines an entity and a **relation type** with a non-empty sub-model. 

Example:
```
* Comment A string attribute with informative text.
* Feature yyy has Prio 42
* Feature yyy requires Feature xxx
* Feature xxx has 
  * Prio 12
  * Spec An informal description
      that is continued on the next line
  * UseCase zzz has
    * Prio 23
```

In markdown view the above is rendered like so:

* Comment A string attribute with informative text.
* Feature yyy has Prio 42
* Feature yyy requires Feature xxx
* Feature xxx has 
  * Prio 12
  * Spec An informal description
      that is continued on the next line
  * UseCase zzz has
    * Prio 23

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

# Lexical Syntax

The source code of a Model consists of Unicode text. 

## Preprocessing

The source code is pre-processed as follows: 
* The source code is split by '
' into a sequence of lines. 
* Each line with index i is given an integer value `leading(i)` corresponding to the number of leading whitespace characters. 
* The lexical analyzer inserts Indent(n) and Outdent(n) tokens that represent regions of indented code based on leading(i), where n denotes indent level.

## Grammar

A legal `reqt` model abides the following grammar, where `|` denotes alternative and `*` denotes zero or more: 
```
ElemStart ::= '* '

Model ::= (Indent(n) ElemStart Elem)*

Elem ::= Node | Rel

Node ::= Attr | Ent

Attr ::= IntAttr |  StrAttr

IntAttr ::= IntAttrType Num

StrAttr ::= StrAttr (Word)*

Ent ::= EntityType Id

Id :: = Word

Rel ::= SingleLineRel | MultiLineRel

SingleLineRel ::= Ent RelType Node

MultiLineRel ::= Ent RelType SubModel

SubModel ::= (Indent(n + 1) ElemStart Elem)* Outdent(n)

EntityType ::= 'Barrier' | 'Breakpoint' | 'Class' | 'Component' | 'Configuration' | 'Data' | 'Design' | 
'Domain' | 'Epic' | 'Event' | 'Feature' | 'Function' | 'Goal' | 'Idea' | 'Image' | 'Interface' | 
'Issue' | 'Label' | 'Member' | 'Module' | 'Product' | 'Prototype' | 'Quality' | 'Relationship' 
| 'Release' | 'Req' | 'Resource' | 'Risk' | 'Screen' | 'Section' | 'Stakeholder' | 'State' | 
'Story' | 'System' | 'Target' | 'Task' | 'Term' | 'Test' | 'UseCase' | 'User' | 'Variant' | 
'VariationPoint'

IntAttrType ::= 'Benefit' | 'Capacity' | 'Cost' | 'Damage' | 'Frequency' | 'Max' | 'Min' | 'Order' | 'Prio' | 
'Probability' | 'Profit' | 'Value'

StrAttr ::= 'Comment' | 'Constraints' | 'Deprecated' | 'Example' | 'Expectation' | 'Failure' | 'Gist' | 
'Input' | 'Location' | 'Output' | 'Spec' | 'Text' | 'Title' | 'Why'

RelType ::= 'Binds' | 'Deprecates' | 'Excludes' | 'Has' | 'Helps' | 'Hurts' | 'Impacts' | 'Implements' | 
'Inherits' | 'Interacts' | 'Precedes' | 'Relates' | 'Requires' | 'Verifies'
```

## Special Parsing Rules

The following rules provides exceptions to the above grammar: 

1. An Id cannot be an `EntityType`, `IntAttrType`, `StrAttributeType`, or `RelType`.
This gives the Error: "Reserved word cannot be used as Id of Ent."

2. The rest of the line after a `StrAttributeType` is part of its string value,
as well as subsequent lines with a higher Indent level.

3. The rest of the line after an `IntAttrType` is part of its integer value. 

4. If a `Num` token have more tokens following on the same line then the following error is given:
"Illegal extra tokens after integer value."

4. An `IntAttribute`, `Ent` or `SingleLineRel` cannot be followed by a subsequent line with a higher Indent level.
This gives the Error: "Higher indentation level is not allowed here."

5. The elements following a `MultiLineRel` that are on a higher Indent level are part of the (possibly empty) `SubModel`.
