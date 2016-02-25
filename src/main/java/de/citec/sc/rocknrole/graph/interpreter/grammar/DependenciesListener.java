// Generated from Dependencies.g4 by ANTLR 4.5.2
package de.citec.sc.rocknrole.graph.interpreter.grammar;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DependenciesParser}.
 */
public interface DependenciesListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#graph}.
	 * @param ctx the parse tree
	 */
	void enterGraph(DependenciesParser.GraphContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#graph}.
	 * @param ctx the parse tree
	 */
	void exitGraph(DependenciesParser.GraphContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#edge}.
	 * @param ctx the parse tree
	 */
	void enterEdge(DependenciesParser.EdgeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#edge}.
	 * @param ctx the parse tree
	 */
	void exitEdge(DependenciesParser.EdgeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#node}.
	 * @param ctx the parse tree
	 */
	void enterNode(DependenciesParser.NodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#node}.
	 * @param ctx the parse tree
	 */
	void exitNode(DependenciesParser.NodeContext ctx);
}