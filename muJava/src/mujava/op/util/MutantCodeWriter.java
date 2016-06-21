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
import java.util.Enumeration;
import openjava.ptree.*;
import mujava.MutationSystem;
import mujava.op.util.CodeChangeLog;
import openjava.ptree.util.ParseTreeVisitor;

/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @updated by Nan Li July 2012, generics type parameters are added in class and method declarations
 * @updated by Lin Deng, add support for AssertStatement
 * @version 1.0
  */

public class MutantCodeWriter extends ParseTreeVisitor
{
   protected PrintWriter out;
   //public static String NEWLINE;

   public String class_name = null;
   //public String target_name = null;

   public int line_num=1;
   public int mutated_line=-1;

   /** to write debugging code */
   private String tab = "    ";
   private int nest = 0;
   public void setTab( String str ) { tab = str; }
   public String getTab() { return tab; }
   public void setNest( int i ) { nest = i; }
   public int getNest() { return nest; }
   public void pushNest() { setNest( getNest() + 1 ); }
   public void popNest() { setNest( getNest() - 1 ); }
   
   public MutantCodeWriter( PrintWriter out ) 
   {
      super();
      this.out = out;
   }

   public MutantCodeWriter( String mutant_dir, PrintWriter out ) 
   {
      super();
      this.out = out;
      class_name = mutant_dir;
   }

   public void setClassName(String str)
   {
      class_name = str;
   }

   protected final void writeTab() 
   {
      for (int i = 0; i < nest; i++)  
    	 out.print( getTab() );
   }

   protected final boolean isSameObject (ParseTree p, ParseTree q ) {
    	if(p == null && q == null)  return true;
    	if(p == null || q == null)  return false;
    	return (p.getObjectID() == q.getObjectID());
    }

    public void visit( ClassDeclaration p )
	 throws ParseTreeException
    {

        writeTab();
 
        if(p.isEnumeration()){
        	
        	/*ModifierList*/
			ModifierList modifs = p.getModifiers();
			if (modifs != null) {
				modifs.accept(this);
				if (!modifs.isEmptyAsRegular())
					out.print(" ");
			}
			out.print("enum ");  
			
			String name = p.getName();
			out.print(name);
			
			/* "implements" ClassTypeList */
			TypeName[] impl = p.getInterfaces();
			if (impl.length != 0) {
				out.print(" implements ");
				impl[0].accept(this);
				for (int i = 1; i < impl.length; ++i) {
					out.print(", ");
					impl[i].accept(this);
				}
			} 
	
			out.println();
			line_num++;		
			
        	EnumConstantList enumConstants = p.getEnumConstants();
        	MemberDeclarationList  mdl = p.getBody();
        	out.print("{");
            if(enumConstants != null){
            	 pushNest();
            	 enumConstants.accept(this);
            	 popNest();
            }            	
             
            if(mdl != null){
            	out.println();
 				line_num++;
 	            pushNest(); 
             	mdl.accept(this);
             	popNest();
	            out.println();
				line_num++;
            }
            
            writeTab();
            out.print("}");
        }
        else{
	        ModifierList modifs = p.getModifiers();
	        if (modifs != null) {
	            modifs.accept( this );
	            if (! modifs.isEmptyAsRegular())  out.print( " " );
	        }
	
	
	        if (p.isInterface()) {
	            out.print( "interface " );
	        } else {
	            out.print( "class " );
	        }
	
			String name = p.getName();
			out.print(name);
			
			if(p.getTypeParameters() != null)
				out.print(p.getTypeParameters().toString());
			  
	        TypeName[] zuper = p.getBaseclasses();
	        if (zuper.length != 0){
	            out.print( " extends " );
	            zuper[0].accept( this );
	            for (int i = 1; i < zuper.length; ++i) {
	                out.print( ", " );
	                zuper[i].accept( this );
	            }
	        } else {
	
	        }
	
	
	        TypeName[] impl = p.getInterfaces();
	        if (impl.length != 0) {
	            out.print( " implements " );
	            impl[0].accept( this );
	            for (int i = 1; i < impl.length; ++i) {
	                out.print( ", " );
	                impl[i].accept( this );
	            }
	        } else {
	
	        }
	
	        out.println();
			  line_num++;
	
	
	        MemberDeclarationList classbody = p.getBody();
	
	        writeTab();  out.println( "{" ); line_num++;
	        if (classbody.isEmpty()) {
	            classbody.accept( this );
	        } else {
	            out.println();
				line_num++;
	            pushNest();  classbody.accept( this );  popNest();
	            out.println();
				line_num++;
	        }
	        writeTab();  out.print( "}" );
	        out.println();  line_num++;
			//}
        }
    }


    public void visit( ConstructorDeclaration p )
	throws ParseTreeException
    {
        writeTab();

       /*ModifierList*/
        ModifierList modifs = p.getModifiers();
        if (modifs != null) {
            modifs.accept( this );
            if (! modifs.isEmptyAsRegular())  out.print( " " );
        }

        /*if(class_name!=null){
	  out.print( class_name );
        }else{
	  String name = p.getName();
	  out.print(name);
        }
		  */

	  String name = p.getName();
	  out.print(name);

        ParameterList params = p.getParameters();
        out.print( "(" );
        if (params.size() != 0) {
            out.print( " " );
            params.accept( this );
            out.print( " " );
        }
        out.print( ")" );

        TypeName[] tnl = p.getThrows();
        if (tnl.length != 0) {
	    out.println();
	    line_num++;
	    writeTab();  writeTab();
            out.print( "throws " );
            tnl[0].accept( this );
            for (int i = 1; i < tnl.length; ++i) {
                out.print ( ", " );
                tnl[i].accept( this );
            }
        }

        ConstructorInvocation sc = p.getConstructorInvocation();
        StatementList body = p.getBody();
        if (body == null && sc == null) {
            out.println( ";" );
	    line_num++;
        } else {
            out.println();
	    line_num++;

            writeTab();
	    out.println( "{" );
	    line_num++;
            pushNest();

            if (sc != null)  sc.accept( this );
	    if (body != null)  body.accept( this );

            popNest();
            writeTab();  out.print( "}" );
        }

        out.println(); line_num++;
    }



    public void visit( AllocationExpression p )
	throws ParseTreeException
    {

        Expression encloser = p.getEncloser();
        if (encloser != null) {
            encloser.accept( this );
            out.print( " . " );
        }

        out.print( "new " );

        TypeName tn = p.getClassType();
        tn.accept( this );

        ExpressionList args = p.getArguments();
	writeArguments( args );

        MemberDeclarationList mdlst = p.getClassBody();
        if (mdlst != null) {
            out.println( "{" ); line_num++;
            pushNest();  mdlst.accept( this );  popNest();
            writeTab();  out.print( "}" );
        }
    }

    public void visit( ArrayAccess p )
	throws ParseTreeException
    {
        Expression expr = p.getReferenceExpr();
        if (expr instanceof Leaf
            || expr instanceof ArrayAccess
            || expr instanceof FieldAccess
            || expr instanceof MethodCall
            || expr instanceof Variable) {
            expr.accept( this );
        } else {
	    writeParenthesis( expr );
        }

        Expression index_expr = p.getIndexExpr();
        out.print( "[" );
        index_expr.accept( this );
        out.print( "]" );
    }

    public void visit( ArrayAllocationExpression p )
	throws ParseTreeException
    {
        out.print( "new " );

        TypeName tn = p.getTypeName();
        tn.accept( this );

        ExpressionList dl = p.getDimExprList();
	for (int i = 0; i < dl.size(); ++i) {
	    Expression expr = dl.get( i );
	    out.print( "[" );
	    if (expr != null) {
		expr.accept( this );
	    }
	    out.print( "]" );
	}

        ArrayInitializer ainit = p.getInitializer();
        if (ainit != null)  ainit.accept( this );
    }

    public void visit( ArrayInitializer p )
	throws ParseTreeException
    {
        out.print( "{ " );
	writeListWithDelimiter( p, ", " );
        if (p.isRemainderOmitted())  out.print( "," );
        out.print( " }" );
    }

    public void visit( AssignmentExpression p )
	throws ParseTreeException
    {
        Expression lexpr = p.getLeft();

        if (lexpr instanceof AssignmentExpression) {
	    writeParenthesis( lexpr );

        } else {
	    lexpr.accept( this );
        }

	String operator = p.operatorString();
        out.print( " " + operator + " " );

        Expression rexp = p.getRight();
        rexp.accept( this );
    }

    public void visit( BinaryExpression p )
	throws ParseTreeException
    {
        Expression lexpr = p.getLeft();
        if (isOperatorNeededLeftPar( p.getOperator(), lexpr )) {
	    writeParenthesis( lexpr );
        } else {
            lexpr.accept( this );
        }

	String operator = p.operatorString();
        out.print( " " + operator + " " );

        Expression rexpr = p.getRight();
        if (isOperatorNeededRightPar( p.getOperator(), rexpr )) {
	    writeParenthesis( rexpr );
        } else {
            rexpr.accept( this );
        }
    }

    public void visit( Block p )
	throws ParseTreeException
    {
	StatementList stmts = p.getStatements();
	writeTab();
	writeStatementsBlock( stmts );
	out.println(); line_num++;
    }

    public void visit( BreakStatement p )
	throws ParseTreeException
    {
        writeTab();

        out.print( "break" );

        String label = p.getLabel();
        if (label != null) {
            out.print( " " );
            out.print( label );
        }

        out.print( ";" );

        out.println(); line_num++;
    }

    public void visit( CaseGroup p )
	throws ParseTreeException
    {
        ExpressionList labels = p.getLabels();
        for (int i = 0; i < labels.size(); ++i) {
            writeTab();
	    Expression label = labels.get( i );
	    if (label == null) {
	        out.print( "default " );
	    } else {
	        out.print( "case " );
		label.accept( this );
	    }
	    out.println( " :" ); line_num++;
        }

        pushNest();
        StatementList stmts = p.getStatements();
	stmts.accept( this );
        popNest();
    }

    public void visit( CaseGroupList p )
	throws ParseTreeException
    {
	//writeListWithSuffix( p, NEWLINE );
	writeListWithSuffixNewline(p);
    }

    public void visit( CaseLabel p )
	throws ParseTreeException
    {
	Expression expr = p.getExpression();
	if (expr != null) {
	    out.print( "case " );
	    expr.accept( this );
	} else {
	    out.print( "default" );
	}
	out.print( ":" );
    }

    public void visit( CaseLabelList p )
	throws ParseTreeException
    {
	//writeListWithSuffix( p, NEWLINE );
	writeListWithSuffixNewline( p );
    }

    public void visit( CastExpression p )
	throws ParseTreeException
    {
        out.print( "(" );
        TypeName ts = p.getTypeSpecifier();
        ts.accept( this );
        out.print( ")" );

	out.print( " " );

        Expression expr = p.getExpression();

        if(expr instanceof AssignmentExpression
           || expr instanceof ConditionalExpression
           || expr instanceof BinaryExpression
           || expr instanceof InstanceofExpression
           || expr instanceof UnaryExpression){

	    writeParenthesis( expr );
        } else {
	    expr.accept( this );
        }
    }

    public void visit( CatchBlock p )
	throws ParseTreeException
    {
        out.print( " catch " );

	out.print( "( " );

        Parameter param = p.getParameter();
        param.accept( this );

        out.print( " ) " );

        StatementList stmts = p.getBody();
	writeStatementsBlock( stmts );
    }

    public void visit( CatchList p )
	throws ParseTreeException
    {
	writeList( p );
    }


    public void visit( ClassDeclarationList p )
	throws ParseTreeException
    {
		 //writeListWithDelimiter( p, NEWLINE + NEWLINE );
		 writeListWithDelimiterNewline( p);
    }

    public void visit( ClassLiteral p )
	throws ParseTreeException
    {
	TypeName type = p.getTypeName();
	type.accept(this);
	out.print(".class");
    }

    public void visit( CompilationUnit p )
	throws ParseTreeException
    {
          out.println( "// This is a mutant program." );
		  line_num++;
          out.println( "// Author : ysma" );
		  line_num++;
		  out.println();
		  line_num++;


        /* package statement */
        String qn = p.getPackage();
        if (qn != null) {
            out.print( "package " + qn + ";" );
            out.println(); line_num++;

            out.println(); line_num++;
			   out.println(); line_num++;
        }

        /* import statement list */
        String[] islst = p.getDeclaredImports();
        if (islst.length != 0) {
            for (int i = 0; i < islst.length; ++i) {
                out.println( "import " + islst[i] + ";" );
				    line_num++;
            }
            out.println(); line_num++;
			   out.println(); line_num++;
        }

        /* type declaration list */
        ClassDeclarationList tdlst = p.getClassDeclarations();
		  tdlst.accept( this );
    }

    public void visit( ConditionalExpression p )
	throws ParseTreeException
    {
        Expression condition = p.getCondition();
        if (condition instanceof AssignmentExpression
	    || condition instanceof ConditionalExpression) {
	    writeParenthesis( condition );
        } else {
	    condition.accept( this );
        }

        out.print( " ? " );

        Expression truecase = p.getTrueCase();
        if (truecase instanceof AssignmentExpression) {
	    writeParenthesis( truecase );
        } else {
	    truecase.accept( this );
        }

        out.print( " : " );

        Expression falsecase = p.getFalseCase();
        if (falsecase instanceof AssignmentExpression) {
	    writeParenthesis( falsecase );
        } else {
	    falsecase.accept( this );
        }
    }

    public void visit( ConstructorInvocation p )
	throws ParseTreeException
    {
        writeTab();

        if (p.isSelfInvocation()) {
            out.print( "this" );
        } else {
            Expression enclosing = p.getEnclosing();
            if (enclosing != null) {
                enclosing.accept( this );
                out.print( " . " );
            }
            out.print( "super" );
        }

        ExpressionList exprs = p.getArguments();
	writeArguments( exprs );

	out.print( ";" );

        out.println(); line_num++;
    }

    public void visit( ContinueStatement p )
	throws ParseTreeException
    {
        writeTab();

        out.print( "continue" );

        String label = p.getLabel();
        if (label != null) {
            out.print( " " + label );
        }

        out.print( ";" );

        out.println(); line_num++;
    }

    public void visit( DoWhileStatement p )
	throws ParseTreeException
    {
        writeTab();

        out.print( "do " );

        StatementList stmts = p.getStatements();

        if (stmts.isEmpty()) {
	    out.print( " ; " );
        } else {
	    writeStatementsBlock( stmts );
        }

        out.print( " while " );

        out.print( "(" );
        Expression expr = p.getExpression();
        expr.accept( this );
        out.print( ")" );

        out.print( ";" );

        out.println(); line_num++;
    }

    public void visit( EmptyStatement p )
	throws ParseTreeException
    {
        writeTab();

        out.print( ";" );

        out.println(); line_num++;
    }
    
    /**
     * Added for Java 1.5 Enumeration
     */
    
    public void visit( EnumDeclaration p )
	throws ParseTreeException
    {
        writeTab();

        String identifier = p.getName();
        TypeName [] tn = p.getImplementsList();
        EnumConstantList enumConstants = p.getEnumConstantList();
        MemberDeclarationList  mdl = p.getClassBodayDeclaration();
        
        
		ModifierList modifs = p.getModifiers();
		if (modifs != null) {
			modifs.accept(this);
			if (!modifs.isEmptyAsRegular())
				out.print(" ");
		}
		
        out.print("enum ");  
        out.print(identifier + " ");
        
        if(tn != null){
	        if (tn.length != 0) {
	            out.print( " implements " );
	            tn[0].accept( this );
	            for (int i = 1; i < tn.length; ++i) {
	                out.print( ", " );
	                tn[i].accept( this );
	            }
	        } 
        }
        out.println();
        writeTab();
        out.print("{");
        line_num++;

        if(enumConstants != null){
        	pushNest();
        	enumConstants.accept(this);
        	popNest();
        }
                
        if(mdl != null){ 
        	pushNest();
        	mdl.accept(this);
        	popNest();
        }
        
        out.println(); line_num++;
        writeTab();
        out.print("}");
        
        out.println(); line_num++;
    }
    
    public void visit( EnumConstantList p )
	throws ParseTreeException
    {
    	out.println();
        line_num++;
        
        if(p != null)
        	writeListWithDelimiterNewLine(p, ",");
        out.print(";");
        
        out.println(); line_num++;
    }
    
    public void visit( EnumConstant p )
	throws ParseTreeException
    {
        writeTab();

        ModifierList modifiers = p.getModifiers();
        String identifier = p.getName();
        ExpressionList arguments = p.getArguments();
        MemberDeclarationList classBoday = p.getClassBody();
        
        ModifierList modifs = p.getModifiers();
        if(modifs != null)
        	modifs.accept( this );
        
        if (! modifs.isEmptyAsRegular())  
        	out.print( " " );
        
        out.print(identifier);
        
        if(arguments != null)
        	writeArguments( arguments );
    	
        if(classBoday != null){       	
	        writeTab();  
	        out.println( "{" ); 
	        line_num++;
	        if (classBoday.isEmpty()) {
	        	classBoday.accept( this );
	        } else {
	            out.println();
	  			line_num++;
	            pushNest();  classBoday.accept( this );  popNest();
	            out.println();
	  			line_num++;
	        }
	        out.println( "}" ); 
	        line_num++;
        }
    }

    public void visit( ExpressionList p )
	throws ParseTreeException
    {
	writeListWithDelimiter( p, ", " );
    }

    public void visit( ExpressionStatement p )
	throws ParseTreeException
    {
	writeTab();

	Expression expr = p.getExpression();

	expr.accept( this );

	out.print( ";" );

	out.println(); line_num++;
    }

    public void visit( FieldAccess p )
	throws ParseTreeException
    {
        Expression expr = p.getReferenceExpr();
        TypeName typename = p.getReferenceType();


        if (expr != null) {
            if (expr instanceof Leaf
                || expr instanceof ArrayAccess
                || expr instanceof FieldAccess
                || expr instanceof MethodCall
                || expr instanceof Variable) {
                expr.accept( this );
            } else {
                out.print( "(" );
                expr.accept( this );
                out.print( ")" );
            }
            out.print( "." );
        } else if (typename != null) {
	    typename.accept( this );
	    out.print( "." );
	}

        String name = p.getName();
        out.print( name );
    }

    public void visit( FieldDeclaration p )
	throws ParseTreeException
    {
        writeTab();

        /*ModifierList*/
        ModifierList modifs = p.getModifiers();
        if (modifs != null) {
            modifs.accept( this );
            if (! modifs.isEmptyAsRegular())  out.print( " " );
        }

        /*TypeName*/
        TypeName ts = p.getTypeSpecifier();
        ts.accept(this);
        out.print(" ");

        /*Variable*/
        String variable = p.getVariable();
        out.print(variable);

        /*"=" VariableInitializer*/
        VariableInitializer initializer = p.getInitializer();
        if (initializer != null) {
            out.print(" = ");
            initializer.accept(this);
        }
        /*";"*/
        out.print(";");

        out.println();
	line_num++;
    }

    public void visit( ForStatement p )
	throws ParseTreeException
    {
        writeTab();

        out.print( "for " );

        out.print( "(" );

        ExpressionList init = p.getInit();
        TypeName tspec = p.getInitDeclType();
        VariableDeclarator[] vdecls = p.getInitDecls();
        String identifier = p.getIdentifier();
        // if identifier != null, it is an enhanced for loop; otherwise, it is a traditional for loop.
        if(identifier != null){
        	if(p.getModifier() != null)
        		out.print(p.getModifier());
        	out.print(tspec);
        	out.print( " " );
        	out.print(identifier);
        	out.print(":");
        	
        	Expression expr = p.getCondition();
            if (expr != null) {
                out.print( " " );
                expr.accept( this );
            }
        }
        else{
        	 if (init != null && (! init.isEmpty() )) {
                 init.get( 0 ).accept( this );
                 for (int i = 1; i < init.size(); ++i) {
                     out.print( ", " );
                     init.get( i ).accept( this );
                 }
             } else if (tspec != null && vdecls != null && vdecls.length != 0) {
                 tspec.accept( this );
                 out.print( " " );
                 vdecls[0].accept( this );
                 for (int i = 1; i < vdecls.length; ++i) {
                     out.print( ", " );
                     vdecls[i].accept( this );
                 }
             }

             out.print( ";" );

             Expression expr = p.getCondition();
             if (expr != null) {
                 out.print( " " );
                 expr.accept( this );
             }

             out.print( ";" );

             ExpressionList incr = p.getIncrement();
             if (incr != null && (! incr.isEmpty())) {
                 out.print( " " );
                 incr.get( 0 ).accept( this );
                 for (int i = 1; i < incr.size(); ++i) {
                     out.print( ", " );
                     incr.get( i ).accept( this );
                 }
             }
        }
       
        out.print( ") " );

        StatementList stmts = p.getStatements();
        if (stmts.isEmpty()) {
            out.print( ";" );
        } else {
	    writeStatementsBlock( stmts );
        }

        out.println(); line_num++;
    }

    public void visit( IfStatement p )
	throws ParseTreeException
    {
        writeTab();

        out.print( "if " );

        out.print( "(" );
        Expression expr = p.getExpression();
        expr.accept( this );
        out.print( ") " );

        /* then part */
        StatementList stmts = p.getStatements();
	writeStatementsBlock( stmts );

        /* else part */
        StatementList elsestmts = p.getElseStatements();
        if (! elsestmts.isEmpty()) {
	    out.print( " else " );
	    writeStatementsBlock( elsestmts );
        }

        out.println(); line_num++;
    }
	/*
	 * Update: Aug 23, 2014
	 * Author: Lin Deng
	 * Add support for AssertStatement
	 */
	public void visit(AssertStatement p) throws ParseTreeException {
	writeTab();

	out.print(" assert ");

	Expression expr = p.getExpression();
	expr.accept(this);
	
	// if exists a : with second expression
	Expression expr2 = p.getExpression2();
	if (expr2!=null)
	{
		out.print(" : ");
		expr2.accept(this);
	}

	out.print(";");

	out.println(); line_num++;
	
}

    public void visit( InstanceofExpression p )
	throws ParseTreeException
    {
	/* this is too strict for + or - */
        Expression lexpr = p.getExpression();
        if (lexpr instanceof AssignmentExpression
	    || lexpr instanceof ConditionalExpression
	    || lexpr instanceof BinaryExpression) {
	    writeParenthesis( lexpr );
        } else {
	    lexpr.accept( this );
        }

        out.print( " instanceof " );

        TypeName tspec = p.getTypeSpecifier();
        tspec.accept( this );

    }

    public void visit( LabeledStatement p )
	throws ParseTreeException
    {
        writeTab();

        String name = p.getLabel();
        out.print( name );

        out.println( " : " ); line_num++;

        Statement statement = p.getStatement();
        statement.accept( this );
    }

    public void visit( Literal p )
	throws ParseTreeException
    {
	out.print( p.toString() );
    }

    public void visit( MemberDeclarationList p )
	throws ParseTreeException
    {
	writeListWithDelimiterNewline( p );
    }

    public void visit( MemberInitializer p )
	throws ParseTreeException
    {
        writeTab();

	if (p.isStatic()) {
	    out.print( "static " );
	}

        StatementList stmts = p.getBody();
	writeStatementsBlock( stmts );

        out.println(); line_num++;
    }

    public void visit( MethodCall p )
	throws ParseTreeException
    {
        Expression expr = p.getReferenceExpr();
        TypeName reftype = p.getReferenceType();

        if (expr != null) {

            if (expr instanceof Leaf
                || expr instanceof ArrayAccess
                || expr instanceof FieldAccess
                || expr instanceof MethodCall
                || expr instanceof Variable) {
                expr.accept( this );
            } else {
            	writeParenthesis( expr );
            }
            out.print( "." );
        } 
        else if (reftype != null) {
        	reftype.accept( this );
        	out.print( "." );
        }

        String name = p.getName();
        out.print( name );

        ExpressionList args = p.getArguments();
	writeArguments( args );
    }

    public void visit( MethodDeclaration p )
	throws ParseTreeException
    {

        writeTab();

        /*ModifierList*/
        ModifierList modifs = p.getModifiers();
        if (modifs != null) {
            modifs.accept( this );
            if (! modifs.isEmptyAsRegular())  out.print( " " );
        }
        
        //print generics type parameters
        TypeParameterList tpl = p.getTypeParameterList();
        if(tpl != null)
        	tpl.accept(this);
        out.print(" ");
        
        TypeName ts = p.getReturnType();
        ts.accept( this );

        out.print( " " );

        String name = p.getName();
        out.print( name );

        ParameterList params = p.getParameters();
        out.print( "(" );
        if (! params.isEmpty()) {
        	out.print( " " );  
        	params.accept( this );  
        	out.print( " " );
        } else {
        	params.accept( this );
        }
        out.print( ")" );

        TypeName[] tnl = p.getThrows();
        if (tnl.length != 0) {
        	out.println(); 
        	line_num++;
        	writeTab();  
        	writeTab();
            out.print( "throws " );
            tnl[0].accept( this );
            for (int i = 1; i < tnl.length; ++i) {
                out.print ( ", " );
                tnl[i].accept( this );
            }
        }

        StatementList bl = p.getBody();
        if (bl == null) {
			 out.print( ";" );
        } else {
			   out.println(); line_num++; writeTab();
			   out.print( "{" );
				out.println(); line_num++;
				pushNest();  bl.accept( this );  popNest();
				writeTab();
			   out.print( "}" );
        }

        out.println(); line_num++;
    }

    public void visit( ModifierList p )
	throws ParseTreeException
    {
        out.print( ModifierList.toString( p.getRegular() ) );
    }

    public void visit( Parameter p )
	throws ParseTreeException
    {
        ModifierList modifs = p.getModifiers();
        modifs.accept( this );
        if (! modifs.isEmptyAsRegular())  out.print( " " );

        TypeName typespec = p.getTypeSpecifier();
        typespec.accept( this );
        
        if(p.isVarargs() == true){
        	out.print("... ");
        }else{
            out.print( " " );
        }

        String declname = p.getVariable();
        out.print( declname );
    }

    public void visit( ParameterList p )
	throws ParseTreeException
    {
	writeListWithDelimiter( p, ", " );
    }

    public void visit( ReturnStatement p )
	throws ParseTreeException
    {
        writeTab();

        out.print( "return" );

        Expression expr = p.getExpression();
        if (expr != null) {
	    out.print(" ");
	    expr.accept( this );
        }

        out.print( ";" );

        out.println(); line_num++;
    }

    public void visit( SelfAccess p )
	throws ParseTreeException
    {
	out.print( p.toString() );
    }

    public void visit( StatementList p )
	throws ParseTreeException
    {
	writeList( p );
    }

    public void visit( SwitchStatement p )
	throws ParseTreeException
    {

        writeTab();

        out.print( "switch " );
        out.print( "(" );
        Expression expr = p.getExpression();
        expr.accept( this );
        out.print( ")" );

        out.println( " {" );
	line_num++;

        CaseGroupList casegrouplist = p.getCaseGroupList();
        casegrouplist.accept( this );

        writeTab();  out.print( "}" );
	out.println(); line_num++;
    }

    public void visit( SynchronizedStatement p )
	throws ParseTreeException
    {
        writeTab();

        out.print( "synchronized " );

        out.print( "(" );
        Expression expr = p.getExpression();
        expr.accept( this );
        out.println( ")" ); line_num++;

        StatementList stmts = p.getStatements();
	writeStatementsBlock( stmts );

        out.println(); line_num++;
    }

    public void visit( ThrowStatement p )
	throws ParseTreeException
    {
        writeTab();

        out.print( "throw " );

        Expression expr = p.getExpression();
        expr.accept( this );

        out.print( ";" );

        out.println(); line_num++;
    }

    public void visit( TryStatement p )
	throws ParseTreeException
    {
        writeTab();

        out.print( "try " );

        StatementList stmts = p.getBody();
	writeStatementsBlock( stmts );

        CatchList catchlist = p.getCatchList();
        if (! catchlist.isEmpty()) {
	    catchlist.accept( this );
        }

        StatementList finstmts = p.getFinallyBody();
        if(! finstmts.isEmpty()){
	    out.println( " finally " );
	    line_num++;
	    writeStatementsBlock( finstmts );
        }

        out.println(); line_num++;
    }

    /******rough around innerclass********/
    public void visit( TypeName p )
	throws ParseTreeException
    {
        String typename = p.getName().replace( '$', '.' );
        out.print( typename );

        int dims = p.getDimension();
        out.print( TypeName.stringFromDimension( dims ) );
    }

	public void visit(TypeParameterList p) throws ParseTreeException {	        
	    
		out.print("<");
		
		if(p != null)
	        writeListWithDelimiter(p, ",");

		out.print(">");
		
	}

	public void visit(TypeParameter p) throws ParseTreeException {
		
		out.print(p.toString());
		
	}
	
    public void visit( UnaryExpression p )
	throws ParseTreeException
    {
        if (p.isPrefix()) {
	    String operator = p.operatorString();
	    out.print( operator );
        }

        Expression expr = p.getExpression();
        if (expr instanceof AssignmentExpression
	    || expr instanceof ConditionalExpression
	    || expr instanceof BinaryExpression
	    || expr instanceof InstanceofExpression
	    || expr instanceof CastExpression
	    || expr instanceof UnaryExpression){
	    writeParenthesis( expr );
        } else {
	    expr.accept( this );
        }

        if (p.isPostfix()) {
	    String operator = p.operatorString();
	    out.print( operator );
        }
    }

    public void visit( Variable p )
	throws ParseTreeException
    {
	out.print( p.toString() );
    }

    public void visit( VariableDeclaration p )
	throws ParseTreeException
    {
        writeTab();

        ModifierList modifs = p.getModifiers();
        modifs.accept( this );
        if (! modifs.isEmptyAsRegular())  out.print( " " );

        TypeName typespec = p.getTypeSpecifier();
        typespec.accept( this );

        out.print( " " );

        VariableDeclarator vd = p.getVariableDeclarator();
        vd.accept( this );


        out.print( ";" );

        out.println(); line_num++;
    }

    public void visit( VariableDeclarator p )
	throws ParseTreeException
    {
        String declname = p.getVariable();
        out.print( declname );

        for (int i = 0; i < p.getDimension(); ++i) {
            out.print( "[]" );
        }

        VariableInitializer varinit = p.getInitializer();
        if (varinit != null) {
            out.print( " = " );
            varinit.accept( this );
        }
    }

    public void visit( WhileStatement p )
	throws ParseTreeException
    {
        writeTab();

        out.print( "while " );

        out.print( "(" );
        Expression expr = p.getExpression();
        expr.accept( this );
        out.print( ") " );

        StatementList stmts = p.getStatements();
        if (stmts.isEmpty()) {
	    out.print( " ;" );
        } else {
	    writeStatementsBlock( stmts );
        }

        out.println(); line_num++;
    }

    protected  void writeListWithDelimiterNewline( List list )
	throws ParseTreeException
    {
	Enumeration it = list.elements();

        if (! it.hasMoreElements())  return;

	writeAnonymous( it.nextElement() );
        while (it.hasMoreElements()) {
	    out.println();
	    line_num++;
            writeAnonymous( it.nextElement() );
        }
    }

    protected  void writeListWithSuffixNewline( List list )
	throws ParseTreeException
    {
	Enumeration it = list.elements();

        while (it.hasMoreElements()) {
            writeAnonymous( it.nextElement() );
	    out.println();
	    line_num++;
        }
    }

    protected void writeLog()
    {
      CodeChangeLog.writeLog(class_name+ MutationSystem.LOG_IDENTIFIER +mutated_line);
    }

    protected void writeLog(String changed_content)
    {
      CodeChangeLog.writeLog(class_name+ MutationSystem.LOG_IDENTIFIER
	  +mutated_line+MutationSystem.LOG_IDENTIFIER+changed_content);
    }

    protected String removeNewline(String str){
      int index;
      while((index = str.indexOf("\n"))>=0){
	if(index>0 && index<str.length()){
	  str = str.substring(0,index-1)+str.substring(index+1,str.length());
	}else if(index==0){
	  str = str.substring(1,str.length());
	}else if(index==str.length()){
	  str = str.substring(0,index-1);
	}
      }
      return str;
    }
    protected void writeArguments( ExpressionList args )
	throws ParseTreeException
    {
        out.print( "(" );
        if (! args.isEmpty()) {
            out.print( " " );
            args.accept( this );
            out.print( " " );
        } else {
            args.accept( this );
        }
        out.print( ")" );
    }

    protected  void writeAnonymous( Object obj )
	throws ParseTreeException
    {
        if (obj == null) {
        } else if (obj instanceof ParseTree) {
            ((ParseTree) obj).accept( this );
        } else {
            out.print( obj.toString() );
        }
    }

    protected  void writeList( List list )
	throws ParseTreeException
    {
	Enumeration it = list.elements();

        while (it.hasMoreElements()) {
	    Object elem = it.nextElement();
            writeAnonymous( elem );
        }
    }

    protected  void writeListWithDelimiter( List list, String delimiter )
	throws ParseTreeException
    {
    	Enumeration it = list.elements();

        if (! it.hasMoreElements())  return;

        writeAnonymous( it.nextElement() );
        while (it.hasMoreElements()) {
        	out.print( delimiter );
            writeAnonymous( it.nextElement() );
        }
    }
    
    protected  void writeListWithDelimiterNewLine( List list, String delimiter )
	throws ParseTreeException
    {
    	Enumeration it = list.elements();

        if (! it.hasMoreElements())  return;

        writeAnonymous( it.nextElement() );
        while (it.hasMoreElements()) {
        	out.print( delimiter );
        	line_num++;    
        	out.println();        	   	
            writeAnonymous( it.nextElement() );
        }
    }

    protected  void writeListWithSuffix( List list, String suffix )
	throws ParseTreeException
    {
	Enumeration it = list.elements();

        while (it.hasMoreElements()) {
            writeAnonymous( it.nextElement() );
	    out.print( suffix );
        }
    }

    protected  void writeParenthesis( Expression expr )
	throws ParseTreeException
    {
	out.print( "(" );
	expr.accept( this );
	out.print( ")" );
    }

    protected  void writeStatementsBlock( StatementList stmts )
	throws ParseTreeException
    {
	out.println( "{" ); line_num++;
	pushNest();

	stmts.accept( this );

	popNest();  writeTab();  out.print( "}" );
    }

    protected static final boolean
    isOperatorNeededLeftPar( int operator, Expression leftexpr ) {
	if (leftexpr instanceof AssignmentExpression
	   || leftexpr instanceof ConditionalExpression) {
	    return true;
	}

	int op = operatorStrength( operator );

	if (leftexpr instanceof InstanceofExpression) {
	    return (op > operatorStrength( BinaryExpression.INSTANCEOF ));
	}

	if(! (leftexpr instanceof BinaryExpression))  return false;

	BinaryExpression lbexpr = (BinaryExpression) leftexpr;
	return (op > operatorStrength( lbexpr.getOperator() ));
    }

    protected static final boolean
    isOperatorNeededRightPar( int operator, Expression rightexpr ) {
	if (rightexpr instanceof AssignmentExpression
	   || rightexpr instanceof ConditionalExpression) {
	    return true;
	}

	int op = operatorStrength( operator );

	if (rightexpr instanceof InstanceofExpression) {
	    return (op >= operatorStrength( BinaryExpression.INSTANCEOF ));
	}

	if (! (rightexpr instanceof BinaryExpression))  return false;

	BinaryExpression lbexpr = (BinaryExpression) rightexpr;
	return (op >= operatorStrength( lbexpr.getOperator() ));
    }

    /**
     * Returns the strength of the union of the operator.
     *
     * @param  op  the id number of operator.
     * @return  the strength of the union.
     */
    protected static final int operatorStrength( int op ) {
	switch (op) {
	case BinaryExpression.TIMES :
	case BinaryExpression.DIVIDE :
	case BinaryExpression.MOD :
	    return 40;
	case BinaryExpression.PLUS :
	case BinaryExpression.MINUS :
	    return 35;
	case BinaryExpression.SHIFT_L :
	case BinaryExpression.SHIFT_R :
	case BinaryExpression.SHIFT_RR :
	    return 30;
	case BinaryExpression.LESS :
	case BinaryExpression.GREATER :
	case BinaryExpression.LESSEQUAL :
	case BinaryExpression.GREATEREQUAL :
	case BinaryExpression.INSTANCEOF :
	    return 25;
	case BinaryExpression.EQUAL :
	case BinaryExpression.NOTEQUAL :
	    return 20;
	case BinaryExpression.BITAND :
	    return 16;
	case BinaryExpression.XOR :
	    return 14;
	case BinaryExpression.BITOR :
	    return 12;
	case BinaryExpression.LOGICAL_AND :
	    return 10;
	case BinaryExpression.LOGICAL_OR :
	    return 8;
	}
	return 100;
    }

    public String remove(String str,String target)
    {
      int index = str.indexOf(target);
      int length = str.length();
      int offset = target.length();
      String result = str.substring(0,index);
      String last = str.substring(index+offset,length);
      result = result.concat(last);
      return result;
    }

    public String remove(String str,String target,int start_index)
    {
      int index = start_index;
      int length = str.length();
      int offset = target.length();
      String result = str.substring(0,index);
      String last = str.substring(index+offset,length);
      result = result.concat(last);
      return result;
    }

}
