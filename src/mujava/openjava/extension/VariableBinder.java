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
import openjava.mop.OJClass;
import openjava.mop.OJClassNotFoundException;
import openjava.mop.Toolbox;
import openjava.ptree.EnumConstant;
import openjava.ptree.ForStatement;
import openjava.ptree.MemberDeclaration;
import openjava.ptree.Parameter;
import openjava.ptree.ParseTreeException;
import openjava.ptree.Statement;
import openjava.ptree.TypeName;
import openjava.ptree.TypeParameter;
import openjava.ptree.TypeParameterList;
import openjava.ptree.VariableDeclaration;
import openjava.ptree.VariableDeclarator;
import openjava.tools.DebugOut;
import openjava.ptree.MethodDeclaration;;

/**
 * The class <code>VariableBinder</code>
 * <p>
 * For example
 * <pre>
 * </pre>
 * <p>
 *
 * @author   Michiaki Tatsubori
 * @version  1.0
 * @since    $Id: VariableBinder.java,v 1.2 2003/02/19 02:55:00 tatsubori Exp $
 * @see java.lang.Object
 * 
 * @update by Nan Li on 09/29/2012
 * evaluateDown(typeParameter) and record(Environment, String type, String name) are added
 * if a method has type parameters in which identifiers extends from some types, the identifiers and the corresponding types need to be recorded in the closedEnvironment of this method.
 * e.g.   <T extends Comparable<T>> is a type parameter, T and its type Comparable should be recorded such that T could be recognized as a variable of Comparable in this method 
 */
public class VariableBinder extends ScopeHandler {

	public VariableBinder(Environment env) {
		super(env);
	}

	public Statement evaluateDown(VariableDeclaration ptree)
		throws ParseTreeException {

		super.evaluateDown(ptree);
		bindLocalVariable(ptree, getEnvironment());

		return ptree;
	}

	public Statement evaluateDown(ForStatement ptree)
		throws ParseTreeException {

		super.evaluateDown(ptree);
		TypeName tspec = ptree.getInitDeclType();
	
		if (tspec == null)
			return ptree;
			
		VariableDeclarator[] vdecls = ptree.getInitDecls();
		if(vdecls != null)
			bindForInit(tspec, vdecls, getEnvironment());
		else{
			
			String identifier = ptree.getIdentifier();
			bindName(getEnvironment(), Toolbox.nameToJavaClassName(tspec.toString()), identifier);
		}

		return ptree;
	}
	/*
	public MemberDeclaration evaluateDown(MethodDeclaration ptree)
			throws ParseTreeException {
		System.out.println("VariableBinder: down" + ptree.getName()+ "; " + ptree.getGenericsTypeParameters() + ptree.getReturnType());
		return ptree;
	}
	
	public MemberDeclaration evaluateUp(MethodDeclaration ptree)
			throws ParseTreeException {
		System.out.println("VariableBinder: up" + ptree.getName()+ "; " + ptree.getGenericsTypeParameters() + ptree.getReturnType());
		return ptree;
	}
	*/

	public Parameter evaluateDown(Parameter ptree) throws ParseTreeException {
		super.evaluateDown(ptree);

		bindParameter(ptree, getEnvironment());

		return ptree;
	}
	
	public TypeParameter evaluateDown(TypeParameter ptree) throws ParseTreeException {
		super.evaluateDown(ptree);
		
		String identifier = ptree.getName();

		if(ptree.getTypeBound() != ""){			
			String[] types = ptree.getTypeBound().split("&");

			//System.out.println("identifier: " + identifier);
			//System.out.println("type: " + type);
			for(String type: types)
				record(env, type, identifier);
		}
		else{
			OJClass OBJECT = OJClass.forClass(Object.class);

			//env.record(identifier, OBJECT);
			getEnvironment().recordGenerics(identifier, OBJECT);
			
			//System.out.println("env: " + env.toString());
		}
		return ptree;
	}
	/*
	public TypeParameterList evaluateDown(TypeParameterList ptree) throws ParseTreeException {
		super.evaluateDown(ptree);

		System.out.println("VariableBinder: TypeParameterList: down " + ptree.toString());

		return ptree;
	}*/

	private static void bindLocalVariable(
		VariableDeclaration var_decl,
		Environment env) {
		
		String type = var_decl.getTypeSpecifier().toString();
		String name = var_decl.getVariable();
		bindName(env, type, name);
	}

	private static void bindForInit(
		TypeName tspec,
		VariableDeclarator[] vdecls,
		Environment env) {
		//If the for statement is a enhanced one, variable declarator will be null
		//so the statements below will only be executed for the traditional for statements
		if(vdecls != null){
			for (int i = 0; i < vdecls.length; ++i) {
				String type = tspec.toString() + vdecls[i].dimensionString();
				String name = vdecls[i].getVariable();
				bindName(env, type, name);
			}
		}
	}

	private static void bindParameter(Parameter param, Environment env) {
		String type = "";
		String name = param.getVariable();
		if(param.isVarargs() == false){
			type = param.getTypeSpecifier().toString();			
		}
		else{
			type = param.getTypeSpecifier().toString() + "[]";
		}
		//System.out.println("VariableBinder: Parameter " + type + " " + name);
		
		bindName(env, type, name);
	}

	private static void record(Environment env, String type, String name) {
				
		String qtypename = env.toQualifiedName(type);
		//System.out.println("qtypename: " + type);
		try {
			OJClass clazz = env.lookupClass(qtypename);
			if (clazz == null)
				clazz = OJClass.forName(qtypename);
			//System.out.println("OJClass: " + name + " " + clazz);
			env.record(name, clazz);
			//System.out.println("env: " + env.toString());
			DebugOut.println("record\t" + name + "\t: " + qtypename);
		} catch (OJClassNotFoundException e) {
			System.err.println(
				"VariableBinder.record() "
					+ e.toString()
					+ " : "
					+ qtypename);
			System.err.println(env);
		}
	}
	
	private static void bindName(Environment env, String type, String name) {
		
		String qtypename = env.toQualifiedName(type);
		//System.out.println("qtypename: " + type);
		try {
			OJClass clazz = env.lookupClass(qtypename);
			if (clazz == null)
				clazz = OJClass.forName(qtypename);
			//System.out.println("OJClass: " + name + " " + clazz);
			env.bindVariable(name, clazz);
			//System.out.println("env: " + env.toString());
			DebugOut.println("binds variable\t" + name + "\t: " + qtypename);
		} catch (OJClassNotFoundException e) {
			System.err.println(
				"VariableBinder.bindName() "
					+ e.toString()
					+ " : "
					+ qtypename);
			System.err.println(env);
		}
	}

}
