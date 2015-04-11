/**
 * Copyright (C) 2015  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package mujava.openjava.extension;


import openjava.mop.Environment;
import openjava.ptree.AllocationExpression;
import openjava.ptree.ArrayAccess;
import openjava.ptree.ArrayAllocationExpression;
import openjava.ptree.ArrayInitializer;
import openjava.ptree.AssertStatement;
import openjava.ptree.AssignmentExpression;
import openjava.ptree.BinaryExpression;
import openjava.ptree.Block;
import openjava.ptree.BreakStatement;
import openjava.ptree.CaseGroup;
import openjava.ptree.CaseGroupList;
import openjava.ptree.CaseLabel;
import openjava.ptree.CaseLabelList;
import openjava.ptree.CastExpression;
import openjava.ptree.CatchBlock;
import openjava.ptree.CatchList;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.ClassDeclarationList;
import openjava.ptree.ClassLiteral;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ConditionalExpression;
import openjava.ptree.ConstructorDeclaration;
import openjava.ptree.ConstructorInvocation;
import openjava.ptree.ContinueStatement;
import openjava.ptree.DoWhileStatement;
import openjava.ptree.EmptyStatement;
import openjava.ptree.EnumConstant;
import openjava.ptree.EnumConstantList;
import openjava.ptree.EnumDeclaration;
import openjava.ptree.Expression;
import openjava.ptree.ExpressionList;
import openjava.ptree.ExpressionStatement;
import openjava.ptree.FieldAccess;
import openjava.ptree.FieldDeclaration;
import openjava.ptree.ForStatement;
import openjava.ptree.IfStatement;
import openjava.ptree.InstanceofExpression;
import openjava.ptree.LabeledStatement;
import openjava.ptree.Literal;
import openjava.ptree.MemberDeclaration;
import openjava.ptree.MemberDeclarationList;
import openjava.ptree.MemberInitializer;
import openjava.ptree.MethodCall;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ModifierList;
import openjava.ptree.Parameter;
import openjava.ptree.ParameterList;
import openjava.ptree.ParseTreeException;
import openjava.ptree.ReturnStatement;
import openjava.ptree.SelfAccess;
import openjava.ptree.Statement;
import openjava.ptree.StatementList;
import openjava.ptree.SwitchStatement;
import openjava.ptree.SynchronizedStatement;
import openjava.ptree.ThrowStatement;
import openjava.ptree.TryStatement;
import openjava.ptree.TypeName;
import openjava.ptree.TypeParameter;
import openjava.ptree.TypeParameterList;
import openjava.ptree.UnaryExpression;
import openjava.ptree.Variable;
import openjava.ptree.VariableDeclaration;
import openjava.ptree.VariableDeclarator;
import openjava.ptree.VariableInitializer;
import openjava.ptree.WhileStatement;
import openjava.ptree.util.ParseTreeVisitor;

/**
 * The class <code>EvaluationShuttle</code> is a Visitor role
 * in the Visitor pattern and this also visits each child
 * <code>ParseTree</code> object from left to right.
 * <p>
 * The class <code>Evaluator</code> is an evaluator of each
 * objects of <code>ParseTree</code> family.  Each methods in
 * this class is invoked from the class <code>EvaluationShuttle</code>.
 * <p>
 * The method <code>evaluateDown()</code> is invoked before evaluating
 * the children of the parse tree object, and <code>evaluateUp()</code>
 * is invoked after the evaluation.
 * <p>
 *
 * @author   Michiaki Tatsubori
 * @version  1.0
 * @since    $Id: EvaluationShuttle.java,v 1.2 2003/02/19 02:55:00 tatsubori Exp $
 * @see openjava.ptree.ParseTree
 * @see openjava.ptree.util.ParseTreeVisitor
 * 
 * @update Lin Deng, add support for AssertStatement, Aug 23, 2014
 */


public abstract class EvaluationShuttle extends ParseTreeVisitor {
	protected Environment env;

	public EvaluationShuttle(Environment env) {
		this.env = env;
	}

	protected Environment getEnvironment() {
		return env;
	}

	protected void setEnvironment(Environment env) {
		this.env = env;
	}

	public Expression evaluateDown(AllocationExpression p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(ArrayAccess p) throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(ArrayAllocationExpression p)
		throws ParseTreeException {
		return p;
	}
	public VariableInitializer evaluateDown(ArrayInitializer p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(AssignmentExpression p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(BinaryExpression p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(Block p) throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(BreakStatement p) throws ParseTreeException {
		return p;
	}
	public CaseGroup evaluateDown(CaseGroup p) throws ParseTreeException {
		return p;
	}
	public CaseGroupList evaluateDown(CaseGroupList p)
		throws ParseTreeException {
		return p;
	}
	public CaseLabel evaluateDown(CaseLabel p) throws ParseTreeException {
		return p;
	}
	public CaseLabelList evaluateDown(CaseLabelList p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(CastExpression p)
		throws ParseTreeException {
		return p;
	}
	public CatchBlock evaluateDown(CatchBlock p) throws ParseTreeException {
		return p;
	}
	public CatchList evaluateDown(CatchList p) throws ParseTreeException {
		return p;
	}
	public ClassDeclaration evaluateDown(ClassDeclaration p)
		throws ParseTreeException {
		return p;
	}
	public ClassDeclarationList evaluateDown(ClassDeclarationList p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(ClassLiteral p) throws ParseTreeException {
		return p;
	}
	public CompilationUnit evaluateDown(CompilationUnit p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(ConditionalExpression p)
		throws ParseTreeException {
		return p;
	}
	public MemberDeclaration evaluateDown(ConstructorDeclaration p)
		throws ParseTreeException {
		return p;
	}
	public ConstructorInvocation evaluateDown(ConstructorInvocation p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(ContinueStatement p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(DoWhileStatement p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(EmptyStatement p) throws ParseTreeException {
		return p;
	}
	/**
	 * Added for Java 1.5 Enumration
	 * @param p
	 * @return
	 * @throws ParseTreeException
	 */
	public MemberDeclaration evaluateDown(EnumDeclaration p) throws ParseTreeException {
		return p;
	}
	
	public EnumConstant evaluateDown(EnumConstant p) throws ParseTreeException {
		return p;
	}
	
	public EnumConstantList evaluateDown(EnumConstantList p) throws ParseTreeException {
		return p;
	}
	
	public ExpressionList evaluateDown(ExpressionList p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(ExpressionStatement p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(FieldAccess p) throws ParseTreeException {
		return p;
	}
	public MemberDeclaration evaluateDown(FieldDeclaration p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(ForStatement p) throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(IfStatement p) throws ParseTreeException {
		return p;
	}
	/*
	 * Update: Aug 23, 2014
	 * Author: Lin Deng
	 * Add support for AssertStatement
	 */
	public Statement evaluateDown(AssertStatement p) throws ParseTreeException {
		return p;
	}
	
	public Expression evaluateDown(InstanceofExpression p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(LabeledStatement p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(Literal p) throws ParseTreeException {
		return p;
	}
	public MemberDeclarationList evaluateDown(MemberDeclarationList p)
		throws ParseTreeException {
		return p;
	}
	public MemberDeclaration evaluateDown(MemberInitializer p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(MethodCall p) throws ParseTreeException {
		return p;
	}
	public MemberDeclaration evaluateDown(MethodDeclaration p)
		throws ParseTreeException {
		return p;
	}
	public ModifierList evaluateDown(ModifierList p)
		throws ParseTreeException {
		return p;
	}
	public Parameter evaluateDown(Parameter p) throws ParseTreeException {
		return p;
	}
	public ParameterList evaluateDown(ParameterList p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(ReturnStatement p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(SelfAccess p) throws ParseTreeException {
		return p;
	}
	public StatementList evaluateDown(StatementList p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(SwitchStatement p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(SynchronizedStatement p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(ThrowStatement p) throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(TryStatement p) throws ParseTreeException {
		return p;
	}
	public TypeName evaluateDown(TypeName p) throws ParseTreeException {
		return p;
	}
	public TypeParameter evaluateDown(TypeParameter p) throws ParseTreeException {
		return p;
	}
	public TypeParameterList evaluateDown(TypeParameterList p) throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(UnaryExpression p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateDown(Variable p) throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(VariableDeclaration p)
		throws ParseTreeException {
		return p;
	}
	public VariableDeclarator evaluateDown(VariableDeclarator p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateDown(WhileStatement p) throws ParseTreeException {
		return p;
	}

	public Expression evaluateUp(AllocationExpression p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(ArrayAccess p) throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(ArrayAllocationExpression p)
		throws ParseTreeException {
		return p;
	}
	public VariableInitializer evaluateUp(ArrayInitializer p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(AssignmentExpression p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(BinaryExpression p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(Block p) throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(BreakStatement p) throws ParseTreeException {
		return p;
	}
	public CaseGroup evaluateUp(CaseGroup p) throws ParseTreeException {
		return p;
	}
	public CaseGroupList evaluateUp(CaseGroupList p)
		throws ParseTreeException {
		return p;
	}
	public CaseLabel evaluateUp(CaseLabel p) throws ParseTreeException {
		return p;
	}
	public CaseLabelList evaluateUp(CaseLabelList p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(CastExpression p) throws ParseTreeException {
		return p;
	}
	public CatchBlock evaluateUp(CatchBlock p) throws ParseTreeException {
		return p;
	}
	public CatchList evaluateUp(CatchList p) throws ParseTreeException {
		return p;
	}
	public ClassDeclaration evaluateUp(ClassDeclaration p)
		throws ParseTreeException {
		return p;
	}
	public ClassDeclarationList evaluateUp(ClassDeclarationList p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(ClassLiteral p) throws ParseTreeException {
		return p;
	}
	public CompilationUnit evaluateUp(CompilationUnit p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(ConditionalExpression p)
		throws ParseTreeException {
		return p;
	}
	public MemberDeclaration evaluateUp(ConstructorDeclaration p)
		throws ParseTreeException {
		return p;
	}
	public ConstructorInvocation evaluateUp(ConstructorInvocation p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(ContinueStatement p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(DoWhileStatement p) throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(EmptyStatement p) throws ParseTreeException {
		return p;
	}
	/**
	 * Added for Java 1.5 Enumeration
	 */
	public MemberDeclaration evaluateUp(EnumDeclaration p) throws ParseTreeException {
		return p;
	}
	
	public EnumConstant evaluateUp(EnumConstant p) throws ParseTreeException {
		return p;
	}
	
	public EnumConstantList evaluateUp(EnumConstantList p) throws ParseTreeException {
		return p;
	}
	
	public ExpressionList evaluateUp(ExpressionList p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(ExpressionStatement p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(FieldAccess p) throws ParseTreeException {
		return p;
	}
	public MemberDeclaration evaluateUp(FieldDeclaration p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(ForStatement p) throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(IfStatement p) throws ParseTreeException {
		return p;
	}
	/*
	 * Update: Aug 23, 2014
	 * Author: Lin Deng
	 * Add support for AssertStatement
	 */
	public Statement evaluateUp(AssertStatement p) throws ParseTreeException {
		return p;
	}
	
	
	public Expression evaluateUp(InstanceofExpression p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(LabeledStatement p) throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(Literal p) throws ParseTreeException {
		return p;
	}
	public MemberDeclarationList evaluateUp(MemberDeclarationList p)
		throws ParseTreeException {
		return p;
	}
	public MemberDeclaration evaluateUp(MemberInitializer p)
		throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(MethodCall p) throws ParseTreeException {
		return p;
	}
	public MemberDeclaration evaluateUp(MethodDeclaration p)
		throws ParseTreeException {
		return p;
	}
	public ModifierList evaluateUp(ModifierList p) throws ParseTreeException {
		return p;
	}
	public Parameter evaluateUp(Parameter p) throws ParseTreeException {
		return p;
	}
	public ParameterList evaluateUp(ParameterList p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(ReturnStatement p) throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(SelfAccess p) throws ParseTreeException {
		return p;
	}
	public StatementList evaluateUp(StatementList p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(SwitchStatement p) throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(SynchronizedStatement p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(ThrowStatement p) throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(TryStatement p) throws ParseTreeException {
		return p;
	}
	public TypeName evaluateUp(TypeName p) throws ParseTreeException {
		return p;
	}
	public TypeParameter evaluateUp(TypeParameter p) throws ParseTreeException {
		return p;
	}
	public TypeParameterList evaluateUp(TypeParameterList p) throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(UnaryExpression p) throws ParseTreeException {
		return p;
	}
	public Expression evaluateUp(Variable p) throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(VariableDeclaration p)
		throws ParseTreeException {
		return p;
	}
	public VariableDeclarator evaluateUp(VariableDeclarator p)
		throws ParseTreeException {
		return p;
	}
	public Statement evaluateUp(WhileStatement p) throws ParseTreeException {
		return p;
	}

	public void visit(AllocationExpression p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ArrayAccess p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ArrayAllocationExpression p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ArrayInitializer p) throws ParseTreeException {
		VariableInitializer newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(AssignmentExpression p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(BinaryExpression p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(Block p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(BreakStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(CaseGroup p) throws ParseTreeException {
		this.evaluateDown(p);
		p.childrenAccept(this);
		this.evaluateUp(p);
	}

	public void visit(CaseGroupList p) throws ParseTreeException {
		CaseGroupList newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(CaseLabel p) throws ParseTreeException {
		CaseLabel newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(CaseLabelList p) throws ParseTreeException {
		CaseLabelList newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(CastExpression p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(CatchBlock p) throws ParseTreeException {
		CatchBlock newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(CatchList p) throws ParseTreeException {
		CatchList newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ClassDeclaration p) throws ParseTreeException {
		ClassDeclaration newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ClassDeclarationList p) throws ParseTreeException {
		ClassDeclarationList newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ClassLiteral p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(CompilationUnit p) throws ParseTreeException {
		CompilationUnit newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ConditionalExpression p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ConstructorDeclaration p) throws ParseTreeException {
		MemberDeclaration newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ConstructorInvocation p) throws ParseTreeException {
		ConstructorInvocation newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ContinueStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(DoWhileStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(EmptyStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}
	
	/**
	 * Added for Java 1.5 Enumeration
	 */
	public void visit(EnumDeclaration p) throws ParseTreeException {
		
		MemberDeclaration newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}
	
	public void visit(EnumConstant p) throws ParseTreeException {
		EnumConstant newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}
	
	public void visit(EnumConstantList p) throws ParseTreeException {
		EnumConstantList newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ExpressionList p) throws ParseTreeException {
		ExpressionList newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ExpressionStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	/* if not same as original, do not continue */
	public void visit(FieldAccess p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(FieldDeclaration p) throws ParseTreeException {
		MemberDeclaration newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ForStatement p) throws ParseTreeException {
	
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(IfStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}
	/*
	 * Update: Aug 23, 2014
	 * Author: Lin Deng
	 * Add support for AssertStatement
	 */
	public void visit(AssertStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(InstanceofExpression p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(LabeledStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(Literal p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(MemberDeclarationList p) throws ParseTreeException {
		MemberDeclarationList newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(MemberInitializer p) throws ParseTreeException {
		MemberDeclaration newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(MethodCall p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(MethodDeclaration p) throws ParseTreeException {
		MemberDeclaration newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ModifierList p) throws ParseTreeException {
		ModifierList newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(Parameter p) throws ParseTreeException {
		Parameter newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ParameterList p) throws ParseTreeException {
		ParameterList newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ReturnStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(SelfAccess p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(StatementList p) throws ParseTreeException {
		StatementList newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(SwitchStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(SynchronizedStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(ThrowStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(TryStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(TypeName p) throws ParseTreeException {
		TypeName newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}
	
	public void visit(TypeParameter p) throws ParseTreeException {
		TypeParameter newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}
	
	public void visit(TypeParameterList p) throws ParseTreeException {
		TypeParameterList newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(Variable p) throws ParseTreeException {
		Expression newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(VariableDeclaration p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(VariableDeclarator p) throws ParseTreeException {
		VariableDeclarator newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

	public void visit(WhileStatement p) throws ParseTreeException {
		Statement newp = this.evaluateDown(p);
		if (newp != p) {
			p.replace(newp);
			return;
		}
		p.childrenAccept(this);
		newp = this.evaluateUp(p);
		if (newp != p)
			p.replace(newp);
	}

}
