package de.citec.sc.rocknrole.graph.interpreter;

import de.citec.sc.rocknrole.graph.*;
import de.citec.sc.rocknrole.graph.interpreter.grammar.DependenciesBaseListener;
import de.citec.sc.rocknrole.graph.interpreter.grammar.DependenciesParser;

/**
 *
 * @author cunger
 */
public class GraphConstructor extends DependenciesBaseListener {
    
    Graph graph = new Graph();
            
    public GraphConstructor() {
    }

    @Override
    public void enterGraph(DependenciesParser.GraphContext ctx) {
    }
    
    @Override
    public void enterEdge(DependenciesParser.EdgeContext ctx) {
    }
    
    @Override
    public void enterNode(DependenciesParser.NodeContext ctx) {
    }
    
    @Override
    public void exitNode(DependenciesParser.NodeContext ctx) {
        // STRING ('/' STRING)? '-' STRING 
          
        if (ctx.STRING().size() == 2) {
            
            int i = Integer.parseInt(ctx.STRING(1).getText());

            String form = ctx.STRING(0).getText();

            if (i != 0) graph.addNode(new Node(i,form));
        } 
        if (ctx.STRING().size() == 3) {
            
            int i = Integer.parseInt(ctx.STRING(2).getText());

            String form = ctx.STRING(0).getText();
            String pos  = ctx.STRING(1).getText();          

            if (i != 0) graph.addNode(new Node(i,form,pos));
        } 
    }
    
    @Override 
    public void exitEdge(DependenciesParser.EdgeContext ctx) {
        // STRING '(' node ',' node ')'

        String rel = ctx.STRING().getText();
        
        int i1 = -1; int i2 = -1;
        if (ctx.head.STRING().size() == 2) i1 = Integer.parseInt(ctx.head.STRING(1).getText());
        if (ctx.head.STRING().size() == 3) i1 = Integer.parseInt(ctx.head.STRING(2).getText());
        if (ctx.dependent.STRING().size() == 2) i2 = Integer.parseInt(ctx.dependent.STRING(1).getText());
        if (ctx.dependent.STRING().size() == 3) i2 = Integer.parseInt(ctx.dependent.STRING(2).getText());

        if (i1 == 0) graph.addRoot(i2);
        if (i1 > 0 && i2 > 0) graph.addEdge(new Edge(Edge.Color.SYN,i1,rel,i2));
    }
    
    @Override 
    public void exitGraph(DependenciesParser.GraphContext ctx) {
    }
    
}
