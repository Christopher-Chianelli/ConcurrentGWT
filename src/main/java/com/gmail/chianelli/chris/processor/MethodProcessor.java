package com.gmail.chianelli.chris.processor;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;

public class MethodProcessor {
	StringBuilder method = new StringBuilder();
	long gotoPoint = 0;

	public MethodProcessor(String clazz, MethodTree tree) {
		method.append("public static com.gmail.chianelli.chris.tasks.Task<");
		method.append(tree.getReturnType());
		method.append("> ");
		method.append(tree.getName());
		method.append("(");
		method.append(clazz);
		method.append(" instance,");
		
		for (VariableTree parameter : tree.getParameters()) {
			method.append(getType(parameter.getType()));
			method.append(" ");
			method.append(parameter.getName());
			method.append(",");
		}
		deleteLastChar();
		method.append(") {\n");
		method.append("}");
	}
	
	public String getGeneratedMethod() {
		return method.toString();
	}
	
	public void processBlock(BlockTree block) {
		for (StatementTree tree : block.getStatements()) {
			processStatement(tree);
		}
	}
	
	public void processStatement(StatementTree tree) {
		switch (tree.getKind()) {
			//Loops
			case FOR_LOOP://ForLoopTree
				ForLoopTree forLoopTree = (ForLoopTree) tree;
				break;
	
			case ENHANCED_FOR_LOOP://EnhancedForLoopTree
				EnhancedForLoopTree enhancedForLoopTree = (EnhancedForLoopTree) tree;
				break;
	
			case DO_WHILE_LOOP://DoWhileLoopTree
				DoWhileLoopTree doWhileLoopTree = (DoWhileLoopTree) tree;
				break;
	
			case WHILE_LOOP://WhileLoopTree
				WhileLoopTree whileLoopTree = (WhileLoopTree) tree;
				break;
	
				//Loop Control
			case BREAK://BreakTree
				BreakTree breakTree = (BreakTree) tree;
				break;
	
			case CONTINUE://ContinueTree
				ContinueTree continueTree = (ContinueTree) tree;
				break;
	
			case ASSERT://AssertTree
				AssertTree assertTree = (AssertTree) tree;
				break;
	
			case BLOCK://BlockTree
				BlockTree blockTree = (BlockTree) tree;
				processBlock(blockTree);
				break;
	
				//Control
			case IF://IfTree
				IfTree ifTree = (IfTree) tree;
				break;
	
			case SWITCH://SwitchTree
				SwitchTree switchTree = (SwitchTree) tree;
				break;
	
			case LABELED_STATEMENT://LabeledStatementTree (cases in a switch)
				LabeledStatementTree labeledStatementTree = (LabeledStatementTree) tree;
				break;
	
				//Expressions
			case RETURN://ReturnTree
				ReturnTree returnTree = (ReturnTree) tree;
				break;
	
			case VARIABLE://VariableTree
				VariableTree variableTree = (VariableTree) tree;
				break;
	
			case EXPRESSION_STATEMENT://ExpressionStatementTree
				ExpressionStatementTree expressionStatementTree = (ExpressionStatementTree) tree;
				break;
	
				//Exceptions
			case THROW://ThrowTree
				ThrowTree throwTree = (ThrowTree) tree;
				break;
	
			case TRY://TryTree
				TryTree tryTree = (TryTree) tree;
				break;
	
				//Other
			case CLASS://ClassTree
				ClassTree classTree = (ClassTree) tree;
				break;
	
			case SYNCHRONIZED://SynchronizedTree
				SynchronizedTree synchronizedTree = (SynchronizedTree) tree;
				break;
	
			case EMPTY_STATEMENT://EmptyStatementTree
				EmptyStatementTree emptyStatementTree = (EmptyStatementTree) tree;
				break;
	
			default://Should never enter here
		}
	}
	
	public String getType(Tree type) {
		switch(type.getKind()) {
	case PRIMITIVE_TYPE:
		PrimitiveTypeTree primitiveType = (PrimitiveTypeTree) type;
		return primitiveType.getPrimitiveTypeKind().toString().toLowerCase();
		
	case ARRAY_TYPE:
		ArrayTypeTree arrayType = (ArrayTypeTree) type;
		return getType(arrayType.getType()) + "[]";
		
	case IDENTIFIER:
		IdentifierTree nonprimitiveType = (IdentifierTree) type;
		return nonprimitiveType.getName().toString();
		
	case PARAMETERIZED_TYPE:
		ParameterizedTypeTree parameterizedType = (ParameterizedTypeTree) type;
		StringBuilder out = new StringBuilder(getType(parameterizedType.getType()));
		out.append("<");
		for (Tree typeParameter : parameterizedType.getTypeArguments()) {
			out.append(getType(typeParameter));
			out.append(",");
		}
		out.deleteCharAt(out.length() - 1);
		out.append(">");
		return out.toString();
		
	case UNBOUNDED_WILDCARD:
		return "?";
		
	case EXTENDS_WILDCARD:
		WildcardTree extendsWildcard = (WildcardTree) type;
		return "? extends " + getType(extendsWildcard.getBound());
		
	case SUPER_WILDCARD:
		WildcardTree superWildcard = (WildcardTree) type;
		return "? super " + getType(superWildcard.getBound());
		
	default:
		return null;
	}
	}
	
	private void deleteLastChar() {
		deleteLast(1);
	}
	
	private void deleteLast(int n) {
		method.delete(method.length() - n, method.length());
	}
}
