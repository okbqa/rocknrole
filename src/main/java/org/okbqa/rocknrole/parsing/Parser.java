package org.okbqa.rocknrole.parsing;

import java.util.Set;
import org.okbqa.rocknrole.graph.Pair;

/**
 *
 * @author cunger
 */

public interface Parser {
    
    ParseResult parse(String text, Set<Pair<Integer,Integer>> entities);
            
}
