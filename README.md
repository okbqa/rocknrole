# Rock'n'Role

Dependency-driven SPARQL template generation from natural language.

Designed for the [OKBQA](http://www.okbqa.org) question answering framework.

Currently works for English and Korean.

## REST service

Target: `http://ws.okbqa.org:1515/templategeneration/rocknrole`

The repository contains two examples files (`src/test/test_en.json` and `src/test/test_ko.json`) that can be used for testing:

```
curl -i -H "Content-Type: application/json" -X POST -d @test_ko.json http://ws.okbqa.org:1515/templategeneration/rocknrole
```

## Example

_Input:_

```
{ "string": "Which river flows through Busan?", "language": "en" }
```

_Output:_

```
["string":"Which river flows through Busan?","language":"en","templates":{"query":"SELECT ?v2 WHERE { ?v2 ?v9 ?v8 ; ?v7 ?v5 . } ","slots":[{"s":"v7","p":"is","o":"rdf:Property"},{"s":"v7","p":"verbalization","o":"flows"},{"s":"v8","p":"is","o":"rdfs:Class"},{"s":"v8","p":"verbalization","o":"river"},{"s":"v9","p":"is","o":"<http://lodqa.org/vocabulary/sort_of>"},{"s":"v5","p":"is","o":"rdfs:Resource"},{"s":"v5","p":"verbalization","o":"Busan"}],"score":"1.0"}]
```

Which expresses the following SPARQL template:

```
SELECT  ?v2
WHERE
  { ?v2  ?v9  ?v8 ;
         ?v7  ?v5 .
  }

 v8 river (rdfs:Class)
 v5 Busan (rdfs:Resource)
 v9 - (<http://lodqa.org/vocabulary/sort_of>)
 v7 flows (rdf:Property)

Score: 1.0
```
