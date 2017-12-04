package com.gmail.chianelli.chris.processor;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

import com.gmail.chianelli.chris.tasks.AbstractTask;
import com.gmail.chianelli.chris.tasks.Task;
import com.squareup.javapoet.*;
import javax.lang.model.element.Modifier;
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
	MethodSpec.Builder method;
	MethodTree methodTree;
	ClassName className;
	final String INSTANCE = "__instance__";
	long gotoPoint = 0;

	public MethodProcessor(String clazz, MethodTree tree) {
		className = ClassName.bestGuess(clazz);
		methodTree = tree;
		method = MethodSpec.methodBuilder(tree.getName().toString());
		method.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
		method.returns(ParameterizedTypeName.get(ClassName.get(Task.class), getType(tree.getReturnType()).box()));
		
		method.addParameter(className, "__instance", Modifier.FINAL);
		
		for (VariableTree parameter : tree.getParameters()) {
			method.addParameter(getType(parameter.getType()), parameter.getName().toString());
		}
		
		method.addCode(processMethod(tree.getBody()));
	}
	
	public MethodSpec getGeneratedMethod() {
		return method.build();
	}
	
	private CodeBlock processMethod(BlockTree methodBody) {
		CodeBlock.Builder code = CodeBlock.builder();
		code.add("return new $T() { $T $N = $N; ",
				ParameterizedTypeName.get(ClassName.get(AbstractTask.class), getType(methodTree.getReturnType()).box()), className, INSTANCE, "__instance");
		//
		code.add("public boolean resume() {return true;}");
		code.add("public $T getReturnValue() {return null;}", getType(methodTree.getReturnType()).box());
		code.add("};");
		return code.build();
	}
	
	public void processBlock(BlockTree blockTree) {
		for (StatementTree tree : blockTree.getStatements()) {
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
	
	public TypeName getType(Tree type) {
		switch(type.getKind()) {
	case PRIMITIVE_TYPE:
		PrimitiveTypeTree primitiveType = (PrimitiveTypeTree) type;		
		switch(primitiveType.getPrimitiveTypeKind().toString().toLowerCase()) {
		case "void":
			return TypeName.VOID;
		case "short":
		    return TypeName.SHORT;
		case "long":
			return TypeName.LONG;
		case "int":
			return TypeName.INT;
		case "float":
			return TypeName.FLOAT;
		case "double":
			return TypeName.DOUBLE;
		case "char":
			return TypeName.CHAR;
		case "byte":
			return TypeName.BYTE;
		case "boolean":
			return TypeName.BOOLEAN;
		}
		
	case ARRAY_TYPE:
		ArrayTypeTree arrayType = (ArrayTypeTree) type;
		return ArrayTypeName.of(getType(arrayType.getType()));
		
	case IDENTIFIER:
		IdentifierTree nonprimitiveType = (IdentifierTree) type;
		return TypeVariableName.get(nonprimitiveType.getName().toString());
		
	case PARAMETERIZED_TYPE:
		ParameterizedTypeTree parameterizedType = (ParameterizedTypeTree) type;
		List<TypeName> parameters = new ArrayList<>(parameterizedType.getTypeArguments().size());
		
		for (Tree typeParameter : parameterizedType.getTypeArguments()) {
			parameters.add(getType(typeParameter));
		}
		
		return ParameterizedTypeName.get(ClassName.bestGuess(getType(parameterizedType.getType()).toString()),
				parameters.toArray(new TypeName[] {}));
		
	case UNBOUNDED_WILDCARD:
		return WildcardTypeName.subtypeOf(TypeName.OBJECT);
		
	case EXTENDS_WILDCARD:
		WildcardTree extendsWildcard = (WildcardTree) type;
		return WildcardTypeName.subtypeOf(getType(extendsWildcard.getBound()));
		
	case SUPER_WILDCARD:
		WildcardTree superWildcard = (WildcardTree) type;
		return WildcardTypeName.supertypeOf(getType(superWildcard.getBound()));
		
	default:
		return null;
	}
    }
}