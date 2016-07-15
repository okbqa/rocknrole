grammar Dependencies;

@header  {package de.citec.sc.rocknrole.graph.interpreter.grammar;}


graph : edge (NEWLINE edge)* ;

edge  : STRING '(' head=node ',' dependent=node ')' ;
node  : STRING ('/' STRING)? '-' STRING ;

STRING : [a-zA-Z0-9\u0080-\uFFFE'.?!*#&$%+_^:]+ ; 

NEWLINE    : '\r'? '\n' ; 
WHITESPACE : [ \t]+ -> skip ; 
