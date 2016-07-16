package org.okbqa.rocknrole.template;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cunger
 */
public enum SlotType {
    
    CLASS, 
    PROPERTY, OBJECTPROPERTY, DATAPROPERTY, SORTAL,
    RESOURCE, LITERAL, 
    RESOURCEorLITERAL, CLASSorRESOURCE;
      
    @Override
    public String toString() {
        
        String s;
        switch (this) {
            case CLASS: s = "rdfs:Class"; break;
            case PROPERTY: s = "rdf:Property"; break;
            case OBJECTPROPERTY: s = "owl:ObjectProperty"; break;
            case DATAPROPERTY: s = "owl:DatatypeProperty"; break;
            case SORTAL: s = "<http://lodqa.org/vocabulary/sort_of>"; break;
            case RESOURCE: s = "rdfs:Resource"; break;
            case LITERAL: s = "rdfs:Literal"; break;
            case RESOURCEorLITERAL: s = "rdfs:Resource|rdfs:Literal"; break;
            case CLASSorRESOURCE: s = "rdfs:Class|rdfs:Resource"; break;
            default: s = "UNSPEC";
        };
        return s;
    }
}
