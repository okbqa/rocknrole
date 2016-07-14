package de.citec.sc.rocknrole.template;

/**
 *
 * @author cunger
 */
public enum SlotType {
    
    CLASS, 
    PROPERTY, OBJECTPROPERTY, DATAPROPERTY, SORTAL,
    RESOURCE, LITERAL, RESOURCEorLITERAL;
    
    @Override
    public String toString() {
        
        String s;
        switch (this) {
            case CLASS: s = "rdf:Class"; break;
            case PROPERTY: s = "rdf:Property"; break;
            case OBJECTPROPERTY: s = "owl:ObjectProperty"; break;
            case DATAPROPERTY: s = "owl:DatatypeProperty"; break;
            case SORTAL: s = "<http://lodqa.org/vocabulary/sort_of>"; break;
            case RESOURCE: s = "rdf:Resource"; break;
            case LITERAL: s = "rdfs:Literal"; break;
            case RESOURCEorLITERAL: s = "rdf:Resource|rdfs:Literal"; break;
            default: s = "UNSPEC";
        };
        return s;
    }
}
