// Generated from Dependencies.g4 by ANTLR 4.3
package org.okbqa.rocknrole.graph.interpreter.grammar;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DependenciesParser}.
 */
public interface DependenciesListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DependenciesParser#node}.
	 * @param ctx the parse tree
	 */
	void enterNode(@NotNull DependenciesParser.NodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#node}.
	 * @param ctx the parse tree
	 */
	void exitNode(@NotNull DependenciesParser.NodeContext ctx);

	/**
	 * Enter a parse tree produced by {@link DependenciesParser#edge}.
	 * @param ctx the parse tree
	 */
	void enterEdge(@NotNull DependenciesParser.EdgeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#edge}.
	 * @param ctx the parse tree
	 */
	void exitEdge(@NotNull DependenciesParser.EdgeContext ctx);

	/**
	 * Enter a parse tree produced by {@link DependenciesParser#graph}.
	 * @param ctx the parse tree
	 */
	void enterGraph(@NotNull DependenciesParser.GraphContext ctx);
	/**
	 * Exit a parse tree produced by {@link DependenciesParser#graph}.
	 * @param ctx the parse tree
	 */
	void exitGraph(@NotNull DependenciesParser.GraphContext ctx);
}