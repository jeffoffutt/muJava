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
package mujava.op.util;

import java.io.*;
import java.util.*;
import openjava.mop.*;
import openjava.ptree.*;

/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class ScopeHandlingMutantCodeWriter extends MutantCodeWriter
{
  private Stack env_nest = new Stack();
  private Environment env;

  public ScopeHandlingMutantCodeWriter(Environment base_env, PrintWriter out)
  {
    super(out);
    env = base_env;
  }

  public ScopeHandlingMutantCodeWriter( Environment base_env, String file_name, PrintWriter out )
  {
	  super(file_name,out);
      env = base_env;
  }

    protected final void pushClosedEnvironment() {
      env_nest.push( getEnvironment() );
      setEnvironment( new ClosedEnvironment( getEnvironment() ) );
    }

    protected final void push( Environment env ) {
      env_nest.push( getEnvironment() );
      setEnvironment( env );
    }

    protected final void pop() {
  	  setEnvironment( (Environment) env_nest.pop() );
    }

    protected Environment getEnvironment() {
	  return env;
    }

    protected void setEnvironment( Environment env ) {
	  this.env = env;
    }


    /* compilation unit */
    public void evaluateDown(CompilationUnit ptree)
    throws ParseTreeException
    {
        ClassDeclaration pubclazz = ptree.getPublicClass();
        String name = (pubclazz != null) ? pubclazz.getName()
	    : "<no public class>";
        FileEnvironment fenv
	    = new FileEnvironment(getEnvironment(), ptree, name);

        push( fenv );
    }

    /* class declaration */
    public void evaluateDown( ClassDeclaration ptree )
	throws ParseTreeException
    {
        /* records this class */
        if (getEnvironment() instanceof ClosedEnvironment) {
            recordLocalClass(ptree);
        }

        /* creates a new class environment */
        ClassEnvironment env
                = new ClassEnvironment(getEnvironment(), ptree.getName());
        MemberDeclarationList mdecls = ptree.getBody();
        for (int i = 0; i < mdecls.size(); ++i) {
            MemberDeclaration m = mdecls.get(i);
            if (! (m instanceof ClassDeclaration))  continue;
            ClassDeclaration inner = (ClassDeclaration) m;
            env.recordMemberClass(inner.getName());
        }
        push(env);
   }

    public void evaluateDown() throws ParseTreeException
    {
	    pushClosedEnvironment();
    }

    public void  evaluateUp() throws ParseTreeException
    {
        pop();
    }

    private void recordLocalClass(ClassDeclaration ptree) {
        String classname = ptree.getName();
        Environment outer_env = getEnvironment();
        String qname = outer_env.toQualifiedName(classname);
        if (outer_env.lookupClass(qname) != null)  return;
        try {
            OJClass out_clazz
                = outer_env.lookupClass(outer_env.currentClassName());
            /***** this will be recorded in global env */
            //OJClass clazz = OJClass.forParseTree(outer_env, out_clazz, ptree);
            OJClass clazz = new OJClass(outer_env, out_clazz, ptree);
            outer_env.record(classname, clazz);
        } catch (Exception ex) {
            System.err.println("unknown error: " + ex);
            return;
        }
    }

    public void visit( CompilationUnit p ) throws ParseTreeException {
        this.evaluateDown(p);
        super.visit(p);
        this.evaluateUp();
    }


    public void visit( ConstructorDeclaration p ) throws ParseTreeException {
        this.evaluateDown();
        super.visit(p);
        this.evaluateUp();
    }

    public void visit( MemberInitializer p ) throws ParseTreeException {
        this.evaluateDown();
        super.visit(p);
        this.evaluateUp();
    }

    public void visit( SwitchStatement p ) throws ParseTreeException {
        this.evaluateDown();
        super.visit(p);
        this.evaluateUp();
    }

    public void visit( SynchronizedStatement p ) throws ParseTreeException {
        this.evaluateDown();
        super.visit(p);
        this.evaluateUp();
    }

    public void visit( TryStatement p ) throws ParseTreeException {
        this.evaluateDown();
        super.visit(p);
        this.evaluateUp();
    }

  public void visit(Parameter p) throws openjava.ptree.ParseTreeException
  {
    bindParameter( p, getEnvironment() );
    super.visit(p);
  }


    public OJClass getType( Expression p ) throws ParseTreeException {
      OJClass result = null;
      try {
          result = p.getType( getEnvironment() );
      } catch ( Exception e ) {
          e.printStackTrace();
          throw new ParseTreeException( e );
      }
      if (result == null) {
          System.err.println("cannot resolve the type of expression");
          System.err.println(p.getClass() + " : " + p);
          System.err.println(getEnvironment());
          /*****DebugOut.println(getEnvironment().toString());*/
          if (p instanceof ArrayAccess) {
          ArrayAccess aaexpr = (ArrayAccess) p;
          Expression refexpr = aaexpr.getReferenceExpr();
          OJClass refexprtype = null;
          OJClass comptype = null;
          try {
              refexprtype = refexpr.getType(getEnvironment());
              comptype = refexprtype.getComponentType();
          } catch (Exception ex) {}
          System.err.println(refexpr + " : " + refexprtype + " : " +
                     comptype);
          }
      }
      return result;
    }

    public  OJClass getSelfType() throws ParseTreeException {
	OJClass result;
	try {
	    Environment env = getEnvironment();
	    String selfname = env.currentClassName();
	    result = env.lookupClass(selfname);
	} catch (Exception ex) {
	    throw new ParseTreeException(ex);
	}
	return result;
    }

    public OJClass getType( TypeName typename ) throws ParseTreeException {
	OJClass result = null;
	try {
	    Environment env = getEnvironment();
	    String qname = env.toQualifiedName(typename.toString());
	    result = env.lookupClass(qname);
	} catch (Exception ex) {
            throw new ParseTreeException(ex);
	}
        if (result == null) {
            System.err.println("unknown type for a type name : " + typename);
        }
	return result;
    }


    protected OJClass computeRefType( TypeName typename, Expression expr )
	throws ParseTreeException
    {
	if (typename != null)  return getType( typename );
	if (expr != null)  return getType( expr );
	return getSelfType();
    }

    protected static void bindLocalVariable( VariableDeclaration var_decl,
					   Environment env )
    {
      String type = var_decl.getTypeSpecifier().toString();
      String name = var_decl.getVariable();
      bindName( env, type, name );
    }

    private static void bindName( Environment env, String type, String name ) {
        String qtypename = env.toQualifiedName( type );
        try {
          OJClass clazz = env.lookupClass( qtypename );
          if (clazz == null)  clazz = OJClass.forName( qtypename );
          env.bindVariable( name, clazz );
        } catch ( OJClassNotFoundException e ) {
                System.err.println( "VariableBinder.bindName() " +
                        e.toString() + " : " + qtypename );
            System.err.println( env );
        }
    }

    private static void bindParameter( Parameter param, Environment env ) {
      String type = param.getTypeSpecifier().toString();
      String name = param.getVariable();
	  bindName( env, type, name );
    }

    private static void bindForInit( TypeName tspec,
				     VariableDeclarator[] vdecls,
				     Environment env ) {
      for (int i = 0; i < vdecls.length; ++i) {
          String type = tspec.toString() + vdecls[i].dimensionString();
          String name = vdecls[i].getVariable();
          bindName( env, type, name );
      }
    }


   public void visit( ClassDeclaration p ) throws ParseTreeException {
        this.evaluateDown( p );
        super.visit(p);
        this.evaluateUp();
    }

    public void visit( Block p ) throws ParseTreeException {
        this.evaluateDown();
        super.visit(p);
        this.evaluateUp();
    }

    public void visit( DoWhileStatement p ) throws ParseTreeException {
        this.evaluateDown();
        super.visit(p);
        this.evaluateUp();
    }

    public void visit( ForStatement p ) throws ParseTreeException {
    	System.out.println("mujava-scopehandingmutantcodewriter=forstatemnt");
        this.evaluateDown();
        TypeName tspec = p.getInitDeclType();
        if (tspec != null){
          VariableDeclarator[] vdecls = p.getInitDecls();
          bindForInit( tspec, vdecls, getEnvironment() );
        }
        super.visit(p);
        this.evaluateUp();
    }

    public void visit( IfStatement p ) throws ParseTreeException {
        this.evaluateDown();
        super.visit(p);
        this.evaluateUp();
    }

    public void visit( MethodDeclaration p ) throws ParseTreeException {
        this.evaluateDown();
        super.visit(p);
        this.evaluateUp();
    }

    public void visit( WhileStatement p ) throws ParseTreeException {
        this.evaluateDown();
        super.visit(p);
        this.evaluateUp();
    }

  public void visit(VariableDeclaration p) throws openjava.ptree.ParseTreeException
  {
	bindLocalVariable( p, getEnvironment() );
      super.visit(p);
  }

}
