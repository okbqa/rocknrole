
## Organization of files

The config files (`en.config` for English and `ko.config` for Korean) specify in which rule files are used and in which order they are loaded. In the end, the rules are applied in the order in which they are specified in the files. Order matters, because we will do things like renaming very specific edge labels `nsubj` and `dobj` to a more general label `ARG`, and later have a rule that matches this general label `ARG`.

The files are currently organized in three different folders:

* `en` and `ko`
* `srl`
* `general`

The main idea behind this is the following:

* `en` and `ko` contain all rules that are language-specific.
* `srl` contains all rules that are specific to the vocabulary of a particular dependency parser. The goal is to map the parser-specific dependency relations to general, parser-independent relations.
* The rules in `general` then match those parser-independent dependencies.

All rules in files called `template.rules` are more or less tailored towards the task of building templates for question answering.


## Structure of the rule files

There are three types of rules: for renaming nodes, for renaming edges, and for transforming subgraphs.
The rule files specify for each part of the document which rules are specified. So the general form of the file looks like this:

```
## RENAME NODES

...

## RENAME EDGES

...

## TRANSFORM

...
```

Order and number of subsections don't matter, so it could also look like this:

```
## RENAME EDGES

...

## TRANSFORM

...

## RENAME EDGES

...

```

Or this:

```
## RENAME NODES

...

```

## Rules

### Renaming nodes and edges

Renaming rules are of the general form `NEW <- OLD` or `NEW <- OLD_1, OLD_2, ..., OLD_n`.
The left-hand side of `<-` specifies the new node form or edge label, the right-hand side matches which node forms or edge labels will be replaced.

For example, the node renaming rules

```
AGENT    <- 누구, 누가, 누굴
DATETIME <- 언제
```

will change each node with form `누구`, `누가` or `누굴` so that it now has form `AGENT`, and every node with form `언제` will get form `DATETIME`.

Analogously, the edge renaming rule

```
ARG <- nsubj, dobj
```

will change all edges with label `nsubj` and `dobj` to having label `ARG`.

### Transformation rules

Transformation rules consist of two parts: a regular expression that specifies the subgraph that will be transformed, prefixed with a `#` and followed by a specification of the transformation.  
The general structure of a transformation rule looks like this:

```
# <subgraph_regex>

<action>
```

Whenever a corresponding subgraph is matched, the action will be applied to the graph.
You can also specify several subgraphs, for which several actions will be applied, for example:

```
# <subgraph_regex_1>
# <subgraph_regex_2>

<action_1>
<action_2>
<action_3>  
```

Whenever `<subgraph_regex_1>` or `<subgraph_regex_2>` is matched, all three actions will be applied to the graph.  
Empty lines are ignored, so it doesn't matter whether there are any and how many of them you add.

#### Regular expressions for subgraph matching

Subgraphs are specified in the same way as the graphs are displayed: they are a set of edges separated by a line break. Each edge has the form `<label>(<head_node>,<dependent_node>)` and nodes are either written as `<form>/<POS>-<integer>` or `<form>-<integer>`.

Example:
```
ARG(hello-1,world/NN-2)
punct(world/NN-2,!-3)
```  

POS tags are optional, so `hello-1` matches all nodes that have form `hello`, no matter what POS tag they have, while `world/NN-2` matches only those nodes that have form `world` and POS tag `NN`.

Instead of a form, you can also use the place holder `*`, which matches any form.

Example:
```
ARG(hello-1,*/NN-2)
punct(*/NN-2,!-3)
```  

#### Format of action specifications

_Collapsing nodes_

Example:
* `1 << 2`
Result: The form of node 2 will be prefixed to the form of node 1.

_Declaring a variable_

The left-hand side string of `=` is assigned the value of the right-hand side, and can be used in later rules.

Example:
* `v = 1`

Once you declared a variable, you can use that variable instead of integers as node identifiers (e.g. `v`, which will be replaced with its value `1`).

_Adding and removing edges_

Adding and removing edges is done by prefixing a `+` (for adding) or `-` (for removing) to an edge specification.
The specified edge is added to or removed from the graph.

Examples:
* `+ REL(1,2)`
* `- MOD(v,2)`
* `+ 1(2,3)`

If you don't specify a string as edge label but an integer, it will use the form of the node with that integer as edge label.

In all actions, white spaces are ignored, so it doesn't matter whether you write `+REL(1,2)` or ` + REL ( 1 , 2 )`.

_Adding and removing nodes_

Adding and removing nodes is done by prefixing a `+` (for adding) or `-` (for removing) to a node specification.
The specified edge is added to or removed from the graph.

Examples:
* `+ LITERAL-2`
* `- mountain/NN-1`
* `+ 1/NN-2`

If you don't specify a string as node form but an integer, it will use the form of the node with that integer as form.

Node adding is destructive, so if you add a node with an identifier that is already used by some other node in the graph, the latter will be overwritten.

_Fresh node identifiers_

The node identifier `new` will create a fresh variable that so far is not used in the graph. It will do so every time it is used, so
`+ REL(new,new)` will add an edge like `REL(1,2)`. If you want to create a new identifier and use it several times, you have to assign it to a variable and use that variable, e.g.
```
v = new
+ REL(v,v)
```
will add an edge like `REL(1,1)`.

_Deleting or not deleting matched subgraphs_

The default is that any subgraph that was matched is deleted.
For example, the rule
```
# ARG(*-1,*-2)

+ REL(*-1,*-2)
```
will result in a graph that has an edge `REL(1,2)`, but does not have an edge `ARG(1,2)` anymore.

If you don't want the matched subgraph to be deleted, add an action that starts with `NOT`.
For example, the rule
```
# ARG(*-1,*-2)

+ REL(*-1,*-2)
NOT delete match
```
will result in a graph that has both the edge `REL(1,2)` and the matched edge `ARG(1,2)`.
