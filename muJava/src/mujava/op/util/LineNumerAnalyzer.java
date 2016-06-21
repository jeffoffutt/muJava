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
import openjava.ptree.util.ParseTreeVisitor;

/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @update Lin Deng   Add support for AssertStatement
 * @version 1.0
  */


public class LineNumerAnalyzer  extends ParseTreeVisitor
{

    public LineNumerAnalyzer()
    {
    }

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

    public void visit( ClassDeclaration p )
	 throws ParseTreeException
    {

        ModifierList modifs = p.getModifiers();
        if (modifs != null) {
            modifs.accept( this );
        }
        line_num++;


        MemberDeclarationList classbody = p.getBody();
        line_num++;
        if (classbody.isEmpty()) {
            classbody.accept( this );
        } else {
           line_num++;
           classbody.accept( this );
        }
        line_num++;

    }


    public void visit( ConstructorDeclaration p )
	throws ParseTreeException
    {
        TypeName[] tnl = p.getThrows();
        if (tnl.length != 0) {
	    line_num++;
	        tnl[0].accept( this );
        }

        ConstructorInvocation sc = p.getConstructorInvocation();
        StatementList body = p.getBody();
        if (body == null && sc == null) {
        line_num++;
        } else {
        line_num++;

       line_num++;

            if (sc != null)  sc.accept( this );
	    if (body != null)  body.accept( this );

        }

         line_num++;
    }



    public void visit( AllocationExpression p )
	throws ParseTreeException
    {

          TypeName tn = p.getClassType();
        tn.accept( this );

        ExpressionList args = p.getArguments();
	    writeArguments( args );

        MemberDeclarationList mdlst = p.getClassBody();
        if (mdlst != null) {
            line_num++;
            mdlst.accept( this );
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
        index_expr.accept( this );
    }

    public void visit( ArrayAllocationExpression p )
	throws ParseTreeException
    {

        TypeName tn = p.getTypeName();
        tn.accept( this );

        ExpressionList dl = p.getDimExprList();
	for (int i = 0; i < dl.size(); ++i) {
	    Expression expr = dl.get( i );
	    if (expr != null) {
		expr.accept( this );
	    }
	}

        ArrayInitializer ainit = p.getInitializer();
        if (ainit != null)  ainit.accept( this );
    }

    public void visit( ArrayInitializer p )
	throws ParseTreeException
    {
 	writeListWithDelimiter( p, ", " );
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

	writeStatementsBlock( stmts );
	line_num++;
    }

    public void visit( BreakStatement p )
	throws ParseTreeException
    {
        line_num++;
    }

    public void visit( CaseGroup p )
	throws ParseTreeException
    {
        ExpressionList labels = p.getLabels();
        for (int i = 0; i < labels.size(); ++i) {

	    Expression label = labels.get( i );
	    if (label == null) {
       } else {

		label.accept( this );
	    }
	     line_num++;
        }


        StatementList stmts = p.getStatements();
	    stmts.accept( this );

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

	    expr.accept( this );
	} else {

	}

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

        TypeName ts = p.getTypeSpecifier();
        ts.accept( this );

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
        Parameter param = p.getParameter();
        param.accept( this );

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
			 writeListWithDelimiterNewline( p);
    }

    public void visit( ClassLiteral p )
	throws ParseTreeException
    {
	TypeName type = p.getTypeName();
	type.accept(this);
   }

    public void visit( CompilationUnit p )
	throws ParseTreeException
    {
        String qn = p.getPackage();
        if (qn != null) {
            line_num++;
        }

        /* import statement list */
        String[] islst = p.getDeclaredImports();
        if (islst.length != 0) {
            for (int i = 0; i < islst.length; ++i) {

				    line_num++;
            }

		line_num++;
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


        Expression truecase = p.getTrueCase();
        if (truecase instanceof AssignmentExpression) {
	    writeParenthesis( truecase );
        } else {
	    truecase.accept( this );
        }


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
        if (p.isSelfInvocation()) {
        } else {
            Expression enclosing = p.getEnclosing();
            if (enclosing != null) {
                enclosing.accept( this );
            }
        }

        ExpressionList exprs = p.getArguments();
	writeArguments( exprs );

        line_num++;
    }

    public void visit( ContinueStatement p )
	throws ParseTreeException
    {
  line_num++;
    }

    public void visit( DoWhileStatement p )
	throws ParseTreeException
    {


        StatementList stmts = p.getStatements();

        if (stmts.isEmpty()) {

        } else {
	    writeStatementsBlock( stmts );
        }


        Expression expr = p.getExpression();
        expr.accept( this );

         line_num++;
    }

    public void visit( EmptyStatement p )
	throws ParseTreeException
    {
       line_num++;
    }
    

	public void visit(EnumDeclaration p) throws ParseTreeException {
		// TODO Auto-generated method stub
		
	}
	
	public void visit(EnumConstant p) throws ParseTreeException {
		// TODO Auto-generated method stub
		
	}

	public void visit(EnumConstantList p) throws ParseTreeException {
		// TODO Auto-generated method stub
		
	}

    public void visit( ExpressionList p )
	throws ParseTreeException
    {
	writeListWithDelimiter( p, ", " );
    }

    public void visit( ExpressionStatement p )
	throws ParseTreeException
    {

	Expression expr = p.getExpression();

	expr.accept( this );
 line_num++;
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

                expr.accept( this );

            }

        } else if (typename != null) {
	    typename.accept( this );
        }
   }

    public void visit( FieldDeclaration p )
	throws ParseTreeException
    {
        printComment(p);


        /*ModifierList*/
        ModifierList modifs = p.getModifiers();
        if (modifs != null) {
            modifs.accept( this );

        }

        /*TypeName*/
        TypeName ts = p.getTypeSpecifier();
        ts.accept(this);


        /*"=" VariableInitializer*/
        VariableInitializer initializer = p.getInitializer();
        if (initializer != null) {
            initializer.accept(this);
        }

	line_num++;
    }

    public void visit( ForStatement p )
	throws ParseTreeException
    {


        ExpressionList init = p.getInit();
        TypeName tspec = p.getInitDeclType();
        VariableDeclarator[] vdecls = p.getInitDecls();
        if (init != null && (! init.isEmpty() )) {
            init.get( 0 ).accept( this );
            for (int i = 1; i < init.size(); ++i) {

                init.get( i ).accept( this );
            }
        } else if (tspec != null && vdecls != null && vdecls.length != 0) {
            tspec.accept( this );
            vdecls[0].accept( this );
            for (int i = 1; i < vdecls.length; ++i) {

                vdecls[i].accept( this );
            }
        }

        Expression expr = p.getCondition();
        if (expr != null) {
              expr.accept( this );
        }

        ExpressionList incr = p.getIncrement();
        if (incr != null && (! incr.isEmpty())) {
              incr.get( 0 ).accept( this );
            for (int i = 1; i < incr.size(); ++i) {
                 incr.get( i ).accept( this );
            }
        }
       StatementList stmts = p.getStatements();
        if (stmts.isEmpty()) {
        } else {
	    writeStatementsBlock( stmts );
        }

         line_num++;
    }

    public void visit( IfStatement p )
	throws ParseTreeException
    {

        Expression expr = p.getExpression();
        expr.accept( this );

        /* then part */
        StatementList stmts = p.getStatements();
	writeStatementsBlock( stmts );

        /* else part */
        StatementList elsestmts = p.getElseStatements();
        if (! elsestmts.isEmpty()) {

	    writeStatementsBlock( elsestmts );
        }

        line_num++;
    }
	/*
	 * Update: Aug 23, 2014
	 * Author: Lin Deng
	 * Add support for AssertStatement
	 */
	public void visit(AssertStatement p) throws ParseTreeException {


	Expression expr = p.getExpression();
	expr.accept(this);
	
	// if exists a : with second expression
	Expression expr2 = p.getExpression2();
	if (expr2!=null)
	{
		expr2.accept(this);
	}
	line_num++;
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


        TypeName tspec = p.getTypeSpecifier();
        tspec.accept( this );

    }

    public void visit( LabeledStatement p )
	throws ParseTreeException
    {
 line_num++;

        Statement statement = p.getStatement();
        statement.accept( this );
    }

    public void visit( Literal p )
	throws ParseTreeException
    {

    }

    public void visit( MemberDeclarationList p )
	throws ParseTreeException
    {
	writeListWithDelimiterNewline( p );
    }

    public void visit( MemberInitializer p )
	throws ParseTreeException
    {




        StatementList stmts = p.getBody();
	writeStatementsBlock( stmts );

        line_num++;
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

        } else if (reftype != null) {

	    reftype.accept( this );

	}
        ExpressionList args = p.getArguments();
	writeArguments( args );
    }

    public void visit( MethodDeclaration p )
	throws ParseTreeException
    {
        printComment( p );





        TypeName ts = p.getReturnType();
        ts.accept( this );

       ParameterList params = p.getParameters();

        if (! params.isEmpty()) {
	     params.accept( this );
        } else {
	    params.accept( this );
        }


        TypeName[] tnl = p.getThrows();
        if (tnl.length != 0) {
	    line_num++;


            tnl[0].accept( this );
            for (int i = 1; i < tnl.length; ++i) {

                tnl[i].accept( this );
            }
        }

        StatementList bl = p.getBody();
        if (bl == null) {

        } else {
			   line_num++;
				 line_num++;
				bl.accept( this );

        }

        line_num++;
    }

    public void visit( ModifierList p )
	throws ParseTreeException
    {
    }

    public void visit( Parameter p )
	throws ParseTreeException
    {
        ModifierList modifs = p.getModifiers();
        modifs.accept( this );


        TypeName typespec = p.getTypeSpecifier();
        typespec.accept( this );


    }

    public void visit( ParameterList p )
	throws ParseTreeException
    {
	writeListWithDelimiter( p, ", " );
    }

    public void visit( ReturnStatement p )
	throws ParseTreeException
    {
         Expression expr = p.getExpression();
        if (expr != null) {

	    expr.accept( this );
        }

        line_num++;
    }

    public void visit( SelfAccess p )
	throws ParseTreeException
    {

    }

    public void visit( StatementList p )
	throws ParseTreeException
    {
	writeList( p );
    }

    public void visit( SwitchStatement p )
	throws ParseTreeException
    {


        Expression expr = p.getExpression();
        expr.accept( this );

	line_num++;

        CaseGroupList casegrouplist = p.getCaseGroupList();
        casegrouplist.accept( this );

      line_num++;
    }

    public void visit( SynchronizedStatement p )
	throws ParseTreeException
    {



        Expression expr = p.getExpression();
        expr.accept( this );
    line_num++;

        StatementList stmts = p.getStatements();
	writeStatementsBlock( stmts );

         line_num++;
    }

    public void visit( ThrowStatement p )
	throws ParseTreeException
    {

        Expression expr = p.getExpression();
        expr.accept( this );
        line_num++;
    }

    public void visit( TryStatement p )
	throws ParseTreeException
    {

        StatementList stmts = p.getBody();
	writeStatementsBlock( stmts );

        CatchList catchlist = p.getCatchList();
        if (! catchlist.isEmpty()) {
	    catchlist.accept( this );
        }

        StatementList finstmts = p.getFinallyBody();
        if(! finstmts.isEmpty()){

	    line_num++;
	    writeStatementsBlock( finstmts );
        }

        line_num++;
    }

    /******rough around innerclass********/
    public void visit( TypeName p )
	throws ParseTreeException
    {

    }

    public void visit( UnaryExpression p )
	throws ParseTreeException
    {


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
        }
    }

    public void visit( Variable p )
	throws ParseTreeException
    {

    }

    public void visit( VariableDeclaration p )
	throws ParseTreeException
    {
        ModifierList modifs = p.getModifiers();
        modifs.accept( this );


        TypeName typespec = p.getTypeSpecifier();
        typespec.accept( this );

        VariableDeclarator vd = p.getVariableDeclarator();
        vd.accept( this );

        line_num++;
    }

    public void visit( VariableDeclarator p )
	throws ParseTreeException
    {
        VariableInitializer varinit = p.getInitializer();
        if (varinit != null) {

            varinit.accept( this );
        }
    }

    public void visit( WhileStatement p )
	throws ParseTreeException
    {

        Expression expr = p.getExpression();
        expr.accept( this );

        StatementList stmts = p.getStatements();
        if (stmts.isEmpty()) {

        } else {
	    writeStatementsBlock( stmts );
        }

        line_num++;
    }

    protected void printComment( NonLeaf p ) {

    }

    protected final void writeListWithDelimiterNewline( List list )
	throws ParseTreeException
    {
	    Enumeration it = list.elements();

        if (! it.hasMoreElements())  return;

	    writeAnonymous( it.nextElement() );
        while (it.hasMoreElements()) {
	        line_num++;
            writeAnonymous( it.nextElement() );
        }
    }

    protected final void writeListWithSuffixNewline( List list )
	throws ParseTreeException
    {
	Enumeration it = list.elements();

        while (it.hasMoreElements()) {
            writeAnonymous( it.nextElement() );
	    line_num++;
        }
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
    protected final void writeArguments( ExpressionList args )
	throws ParseTreeException
    {

    }

    protected final void writeAnonymous( Object obj )
	throws ParseTreeException
    {
        if (obj == null) {
        } else if (obj instanceof ParseTree) {
            ((ParseTree) obj).accept( this );
        } else {
        }
    }

    protected final void writeList( List list )
	throws ParseTreeException
    {
	Enumeration it = list.elements();

        while (it.hasMoreElements()) {
	    Object elem = it.nextElement();
            writeAnonymous( elem );
        }
    }

    protected final void writeListWithDelimiter( List list, String delimiter )
	throws ParseTreeException
    {
	Enumeration it = list.elements();

        if (! it.hasMoreElements())  return;

	writeAnonymous( it.nextElement() );
        while (it.hasMoreElements()) {
            writeAnonymous( it.nextElement() );
        }
    }

    protected final void writeListWithSuffix( List list, String suffix )
	throws ParseTreeException
    {
	Enumeration it = list.elements();

        while (it.hasMoreElements()) {
            writeAnonymous( it.nextElement() );
        }
    }

    protected final void writeParenthesis( Expression expr )
	throws ParseTreeException
    {
	expr.accept( this );
    }

    protected final void writeStatementsBlock( StatementList stmts )
	throws ParseTreeException
    {
	    line_num++;

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
	@Override
	public void visit(TypeParameter arg0) throws ParseTreeException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void visit(TypeParameterList arg0) throws ParseTreeException {
		// TODO Auto-generated method stub
		
	}


}
