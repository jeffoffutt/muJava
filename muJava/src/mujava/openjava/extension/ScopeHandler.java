
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


import java.util.Stack;

import openjava.mop.AnonymousClassEnvironment;
import openjava.mop.ClassEnvironment;
import openjava.mop.ClosedEnvironment;
import openjava.mop.Environment;
import openjava.mop.FileEnvironment;
import openjava.mop.OJClass;
import openjava.ptree.AllocationExpression;
import openjava.ptree.Block;
import openjava.ptree.ClassDeclaration;
import openjava.ptree.CompilationUnit;
import openjava.ptree.ConstructorDeclaration;
import openjava.ptree.DoWhileStatement;
import openjava.ptree.EnumDeclaration;
import openjava.ptree.Expression;
import openjava.ptree.ForStatement;
import openjava.ptree.IfStatement;
import openjava.ptree.MemberDeclaration;
import openjava.ptree.MemberDeclarationList;
import openjava.ptree.MemberInitializer;
import openjava.ptree.MethodDeclaration;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Statement;
import openjava.ptree.SwitchStatement;
import openjava.ptree.SynchronizedStatement;
import openjava.ptree.TryStatement;
import openjava.ptree.WhileStatement;

/**
 * The class <code>ScopeHandler</code>
 * <p>
 * For example
 * <pre>
 * </pre>
 * <p>
 *
 * @author   Michiaki Tatsubori
 * @version  1.0
 * @since    $Id: ScopeHandler.java,v 1.2 2003/02/19 02:55:00 tatsubori Exp $
 * @see openjava.ptree.ParseTree
 * @see openjava.ptree.util.ParseTreeVisitor
 * @see openjava.ptree.util.EvaluationShuttle
 */
public abstract class ScopeHandler extends EvaluationShuttle {
	protected Stack env_nest = new Stack();

	public ScopeHandler(Environment base_env) {
		super(base_env);
	}

	protected final void pushClosedEnvironment() {
		push(new ClosedEnvironment(getEnvironment()));
	}

	protected final void push(Environment env) {
		env_nest.push(getEnvironment());
		setEnvironment(env);
	}

	protected final void pop() {
		setEnvironment((Environment) env_nest.pop());
	}

	/* in walking down through parse tree */

	/* compilation unit */
	public CompilationUnit evaluateDown(CompilationUnit ptree)
		throws ParseTreeException {
		ClassDeclaration pubclazz = ptree.getPublicClass();
		String name =
			(pubclazz != null) ? pubclazz.getName() : "<no public class>";
		FileEnvironment fenv =
			new FileEnvironment(getEnvironment(), ptree, name);

		push(fenv);

		return ptree;
	}

	/* class declaration */
	public ClassDeclaration evaluateDown(ClassDeclaration ptree)
		throws ParseTreeException {
		/* records this class */
		if (getEnvironment() instanceof ClosedEnvironment) {
			recordLocalClass(ptree);
		}

		/* creates a new class environment */
		ClassEnvironment env =
			new ClassEnvironment(getEnvironment(), ptree.getName());
		MemberDeclarationList mdecls = ptree.getBody();
		for (int i = 0; i < mdecls.size(); ++i) {
			MemberDeclaration m = mdecls.get(i);
			if (m instanceof ClassDeclaration) {

				ClassDeclaration inner = (ClassDeclaration) m;
				env.recordMemberClass(inner.getName());
			}
			else if(m instanceof EnumDeclaration){
				EnumDeclaration inner = (EnumDeclaration) m;
				env.recordMemberClass(inner.getName());
			}
		}
		push(env);

		return ptree;
	}

	
	/* class declaration */
	public MemberDeclaration evaluateDown(EnumDeclaration ptree)
		throws ParseTreeException {

		/* records this class */
		if (getEnvironment() instanceof ClosedEnvironment) {
			recordLocalClass(ptree);
		}
		
		/* creates a new class environment */
		ClassEnvironment env =
			new ClassEnvironment(getEnvironment(), ptree.getName());
		MemberDeclarationList mdecls = ptree.getClassBodayDeclaration();
		if(mdecls != null){
			for (int i = 0; i < mdecls.size(); ++i) {
				MemberDeclaration m = mdecls.get(i);
				if (m instanceof ClassDeclaration) {
					ClassDeclaration inner = (ClassDeclaration) m;
					env.recordMemberClass(inner.getName());
				}
				else if(m instanceof EnumDeclaration){
					EnumDeclaration inner = (EnumDeclaration) m;
					env.recordMemberClass(inner.getName());
				}
				
			}
		}
		push(env);
		//System.out.println("EnumDeclaration evaluateDown " + ptree.getName() +": " + env.currentClassName() + ": " + env.getMemberClasses().size());
		return ptree;
	}
	
	private void recordLocalClass(MemberDeclaration ptree) {
		String classname = "";
		if(ptree instanceof ClassDeclaration)
		   classname = ((ClassDeclaration)ptree).getName();
		else if(ptree instanceof EnumDeclaration)
			 classname = ((EnumDeclaration)ptree).getName();
		//System.out.println("recordLocalClass: "+ classname);
		Environment outer_env = getEnvironment();
		String qname = outer_env.toQualifiedName(classname);
		//System.out.println("ClassDeclaration_recordLocalClass: "+ qname);
		if (outer_env.lookupClass(qname) != null)
			return;
		try {
			OJClass out_clazz =
				outer_env.lookupClass(outer_env.currentClassName());
			OJClass clazz = null;
			/***** this will be recorded in global env */
			if(ptree instanceof ClassDeclaration)
				clazz = new OJClass(outer_env, out_clazz, ((ClassDeclaration)ptree));
			else if(ptree instanceof EnumDeclaration)
				clazz = new OJClass(outer_env, out_clazz, new ClassDeclaration(((EnumDeclaration)ptree)));
			outer_env.record(classname, clazz);
		} catch (Exception ex) {
			System.err.println("unknown error: " + ex);
			return;
		}
	}

	/* class body contents */
	public MemberDeclaration evaluateDown(MethodDeclaration ptree)
		throws ParseTreeException {
		pushClosedEnvironment();
		return ptree;
	}

	public MemberDeclaration evaluateDown(ConstructorDeclaration ptree)
		throws ParseTreeException {
		pushClosedEnvironment();
		return ptree;
	}

	public MemberDeclaration evaluateDown(MemberInitializer ptree)
		throws ParseTreeException {
		pushClosedEnvironment();
		return ptree;
	}

	/* statements */
	public Statement evaluateDown(Block ptree) throws ParseTreeException {
		pushClosedEnvironment();
		return ptree;
	}

	public Statement evaluateDown(SwitchStatement ptree)
		throws ParseTreeException {
		pushClosedEnvironment();
		return ptree;
	}

	public Statement evaluateDown(IfStatement ptree)
		throws ParseTreeException {
		pushClosedEnvironment();
		return ptree;
	}

	public Statement evaluateDown(WhileStatement ptree)
		throws ParseTreeException {
		pushClosedEnvironment();
		return ptree;
	}

	public Statement evaluateDown(DoWhileStatement ptree)
		throws ParseTreeException {
		pushClosedEnvironment();
		return ptree;
	}

	public Statement evaluateDown(ForStatement ptree)
		throws ParseTreeException {
		pushClosedEnvironment();
		return ptree;
	}

	public Statement evaluateDown(TryStatement ptree)
		throws ParseTreeException {
		pushClosedEnvironment();
		return ptree;
	}

	public Statement evaluateDown(SynchronizedStatement ptree)
		throws ParseTreeException {
		pushClosedEnvironment();
		return ptree;
	}

	public Expression evaluateDown(AllocationExpression ptree)
		throws ParseTreeException {
		MemberDeclarationList cbody = ptree.getClassBody();
		if (cbody != null) {
			String baseName = ptree.getClassType().toString();
			push(
				new AnonymousClassEnvironment(
					getEnvironment(),
					baseName,
					cbody));
		} else {
			pushClosedEnvironment();
		}
		return ptree;
	}

	/* in walking down through parse tree */

	/* class declaration */
	public CompilationUnit evaluateUp(CompilationUnit ptree)
		throws ParseTreeException {
		pop();
		return ptree;
	}

	/* class declaration */
	public ClassDeclaration evaluateUp(ClassDeclaration ptree)
		throws ParseTreeException {
		pop();
		return ptree;
	}

	/* Enum declaration */
	public EnumDeclaration evaluateUp(EnumDeclaration ptree)
		throws ParseTreeException {
		pop();
		return ptree;
	}
	
	/* class body contents */
	public MemberDeclaration evaluateUp(MethodDeclaration ptree)
		throws ParseTreeException {
		pop();
		return ptree;
	}

	public MemberDeclaration evaluateUp(ConstructorDeclaration ptree)
		throws ParseTreeException {
		pop();
		return ptree;
	}

	public MemberDeclaration evaluateUp(MemberInitializer ptree)
		throws ParseTreeException {
		pop();
		return ptree;
	}

	/* statements */
	public Statement evaluateUp(Block ptree) throws ParseTreeException {
		pop();
		return ptree;
	}

	public Statement evaluateUp(SwitchStatement ptree)
		throws ParseTreeException {
		pop();
		return ptree;
	}

	public Statement evaluateUp(IfStatement ptree) throws ParseTreeException {
		pop();
		return ptree;
	}

	public Statement evaluateUp(WhileStatement ptree)
		throws ParseTreeException {
		pop();
		return ptree;
	}

	public Statement evaluateUp(DoWhileStatement ptree)
		throws ParseTreeException {
		pop();
		return ptree;
	}

	public Statement evaluateUp(ForStatement ptree) throws ParseTreeException {
		pop();
		return ptree;
	}

	public Statement evaluateUp(TryStatement ptree) throws ParseTreeException {
		pop();
		return ptree;
	}

	public Statement evaluateUp(SynchronizedStatement ptree)
		throws ParseTreeException {
		pop();
		return ptree;
	}

	public Expression evaluateUp(AllocationExpression ptree)
		throws ParseTreeException {
		pop();
		return ptree;
	}

}
