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

package mujava.op;

import openjava.mop.*;
import openjava.ptree.*;
import java.io.*;

/**
 * <p>Generate ABS (Absolute Value Insertion), AOR (Arithmetic Operator Replacement),
 *    LCR (Logical Operator Replacement), ROR (Relational Operator Replacement), 
 *    and UOI (Unary Operator Insertion) mutants.    
 * </p>
 * <p>
 * -- <i>ABS</i> modifies each arithmetic expression by the function <i>abs()</i>,
 *               <i>negAbs()</i>, and <i>failOnZero()</i> <br />
 * -- <i>AOR</i> replaces each occurrence of one of the arithmetic operators +, -, *, /,
 *               and % by each of the other operators <br />
 * -- <i>LCR</i> replaces each occurrence of each bitwise logical operator 
 *               (bitwise and - &, bitwise or - |, exclusive or - ^) by each of 
 *               the other operators <br />
 * -- <i>ROR</i> replaces each occurrence of one of the relational operators
 *               (<, <=, >, >=, =, <>) by each of the other operators and by 
 *               <i>falseOp</i> which assigns a <i>false</i> value to a predicate, and
 *               <i>trueOp</i> which assigns a <i>true</i> value to a predicate <br />
 * -- <i>UOI</i> inserts each unary operator (arithmetic +, arithmetic -, conditional !,
 *               logical ~) before each expression of the correct type                                                             
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0 
  */

public class ABS_AOR_LCR_ROR_UOI extends mujava.op.util.Mutator
{
   boolean mutant_possible = true;

   boolean absFlag = true;
   boolean aorFlag = true;
   boolean lcrFlag = true;
   boolean rorFlag = true;
   boolean uoiFlag = true;

   public ABS_AOR_LCR_ROR_UOI(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }

   /**
    * Set ABS flag to a given predicate
    * @param b
    */ 
   public void setABSFlag(boolean b)
   {
      absFlag  = b;
   }
   
   /**
    * Set AOR flag to a given predicate
    * @param b
    */
   public void setAORFlag(boolean b)
   {
      aorFlag  = b;
   }
   
   /**
    * Set LCR flag to a given predicate 
    * @param b
    */
   public void setLCRFlag(boolean b)
   {
      lcrFlag  = b;
   }
   
   /**
    * Set ROR flag to a given predicate 
    * @param b
    */
   public void setRORFlag(boolean b)
   {
      rorFlag  = b;
   }
   
   /**
    * Set UOI flag to a given predicate
    * @param b
    */
   public void setUOIFlag(boolean b)
   {
      uoiFlag  = b;
   }

   public void visit( FieldDeclaration p ) throws ParseTreeException 
   {
      mutant_possible = false;
      super.visit(p);
      mutant_possible = true;
   }

   public void visit( MethodCall p ) throws ParseTreeException 
   {
      try
      {
         OJClass type = getType(p);
         if (mutant_possible)
         {
            if ( type == OJSystem.INT || type == OJSystem.DOUBLE || type == OJSystem.FLOAT
                  || type == OJSystem.LONG || type == OJSystem.SHORT)
            {
               if (absFlag) 
            	  absMutantGen(p);
            } 
            else if ( type == OJSystem.BOOLEAN)
            {
               if (uoiFlag) 
            	  uoi_boolean_MutantGen(p);
            }
         }

         mutant_possible = true;
         super.visit(p);
      } catch (Exception e) 
      {
    	 // do nothing 
      }
   }

   public void visit( BinaryExpression p ) throws ParseTreeException 
   {
      OJClass type = getType(p);
      // arithmetic expression
      // for ABS, UOI operator
      if ( type == OJSystem.INT || type == OJSystem.DOUBLE || type == OJSystem.FLOAT
            || type == OJSystem.LONG || type == OJSystem.SHORT)
      {
         if (absFlag) 
        	absMutantGen(p);
      }

      Expression lexpr = p.getLeft();
	  lexpr.accept( this );
 
	  int op_type = p.getOperator();

	  switch (op_type)
	  {
         // for AOR mutation operator
         // 5 Arithmetic Operators : TIMES, DIVIDE, MOD, PLUS, MONUS
          case BinaryExpression.TIMES :
                  if (aorFlag) 
                	 aorMutantGen(p,BinaryExpression.TIMES);
                  break;

          case BinaryExpression.DIVIDE :
                  if (aorFlag) 
                	 aorMutantGen(p,BinaryExpression.DIVIDE);
                  break;

          case BinaryExpression.MOD :
                  if (aorFlag) 
                	 aorMutantGen(p,BinaryExpression.MOD);
                  break;

          case BinaryExpression.PLUS :
                  if (aorFlag) 
                	 aorMutantGen(p,BinaryExpression.PLUS);
                  break;

          case BinaryExpression.MINUS :
                  if (aorFlag) 
                	 aorMutantGen(p,BinaryExpression.MINUS);
                  break;

          // for ROR mutation operator
          // 7 Relational Operators : LESS,GREATER,LESSEQUAL,GREATEREQUAL,
          //                          EQUAL,NOTEQUAL
          case BinaryExpression.LESS :
                  if (rorFlag) 
                	 rorMutantGen(p,BinaryExpression.LESS);
                  break;

          case BinaryExpression.GREATER :
                  if (rorFlag) 
                	 rorMutantGen(p,BinaryExpression.GREATER);
                  break;

          case BinaryExpression.LESSEQUAL :
                  if (rorFlag) 
                	 rorMutantGen(p,BinaryExpression.LESSEQUAL);
                  break;

          case BinaryExpression.GREATEREQUAL :
                  if (rorFlag) 
                	 rorMutantGen(p,BinaryExpression.GREATEREQUAL);
                  break;

          case BinaryExpression.EQUAL :
                  if (rorFlag) 
                	 rorMutantGen(p,BinaryExpression.EQUAL);
                  break;

          case BinaryExpression.NOTEQUAL :
                  if (rorFlag) 
                	 rorMutantGen(p,BinaryExpression.NOTEQUAL);
                  break;

          // for LCR mutation operator
          //  3 Logical Operators : AND, OR, XOR
          case BinaryExpression.LOGICAL_AND :
                  if (lcrFlag) 
                	 lcrMutantGen(p,BinaryExpression.LOGICAL_AND);
                  break;

          case BinaryExpression.LOGICAL_OR :
                  if (lcrFlag) 
                	 lcrMutantGen(p,BinaryExpression.LOGICAL_OR);
                  break;

          case BinaryExpression.XOR :
                  if (lcrFlag) 
                	 lcrMutantGen(p,BinaryExpression.XOR);
                  break;
      }

      Expression rexpr = p.getRight();
	  rexpr.accept( this );
   }

   public void visit( StatementList p ) throws ParseTreeException 
   {
      mutant_possible = true;
      super.visit(p);
   }

   public void visit( Parameter p ) throws ParseTreeException 
   {
      mutant_possible = false;
      super.visit(p);
      mutant_possible = true;
   }

   public void visit( VariableDeclaration p ) throws ParseTreeException 
   {
      mutant_possible = false;
      super.visit(p);
      mutant_possible = true;
   }

   public void visit( ExpressionStatement p ) throws ParseTreeException 
   {
      Expression exp = p.getExpression();
      if (exp instanceof MethodCall)
      {
         mutant_possible = false;
         super.visit(p);
         mutant_possible = true;
      }
      else
      {
         super.visit(p);
      }
   }

   public void visit( CaseGroup p ) throws ParseTreeException 
   {
      mutant_possible = false;
      super.visit(p);
      mutant_possible = true;
   }

   public void visit( Variable p ) throws ParseTreeException 
   {
      OJClass type = getType(p);

      if ( mutant_possible)
      {
         // arithmetic expression
         // for ABS, UOI operator
         if ( type == OJSystem.INT || type == OJSystem.DOUBLE || type == OJSystem.FLOAT
                || type == OJSystem.LONG || type == OJSystem.SHORT)
         {
            if (mutant_possible)
            {
               if (absFlag) 
            	  absMutantGen(p);
               
               if (uoiFlag) 
            	  uoi_arithmetic_MutantGen(p);
            }
         }
         else if (type == OJSystem.BOOLEAN)
         {
            if (mutant_possible)
            {
               if (uoiFlag) 
            	  uoi_boolean_MutantGen(p);
            }
         }
      }

      super.visit(p);
   }

   public void visit( UnaryExpression p ) throws ParseTreeException 
   {
      OJClass type = getType(p);
      if (type == OJSystem.BOOLEAN)
      {
         lcr_outputToFile(p);
      }

      //--------------------------------------
      mutant_possible = false;

      Expression newp = this.evaluateDown( p );
      if (newp != p) 
      {
         p.replace( newp );
         return;
      }
      
      p.childrenAccept( this );
      newp = this.evaluateUp( p );
      if (newp != p)  
    	 p.replace( newp );

      mutant_possible = true;
   }

   public void visit( AssignmentExpression p ) throws ParseTreeException 
   {
      //mutant_possible = false;
      //Expression left = p.getLeft();
      //super.visit( p );

	  //FieldAccess fldac = (FieldAccess) left;
	  //Expression refexpr = fldac.getReferenceExpr();
      //TypeName reftype = fldac.getReferenceType();
      mutant_possible = true;

      Expression value = p.getRight();
	  /* custom version of  visit() skipping the field */

      Expression newp;
      newp = this.evaluateDown( p );

      if (newp != p) 
      {
		 p.replace( newp );
		 newp.accept( this );
		 return;
   	  }

	  value.accept( this );
     
	  newp = this.evaluateUp( p );
	  if (newp != p)  
		 p.replace( newp );
   }

   private void lcrMutantGen(BinaryExpression exp, int op)
   {
      if (op != BinaryExpression.LOGICAL_AND)
      {
         lcr_outputToFile(exp,BinaryExpression.LOGICAL_AND);
      }

      if (op != BinaryExpression.LOGICAL_OR)
      {
         lcr_outputToFile(exp,BinaryExpression.LOGICAL_OR);
      }
      
      if (op != BinaryExpression.XOR)
      {
         lcr_outputToFile(exp,BinaryExpression.XOR);
      }
   }

   private void absMutantGen(MethodCall exp)
   {
      abs_zero_outputToFile(exp);
      abs_negative_outputToFile(exp);
   }

   private void absMutantGen(BinaryExpression exp)
   {
      abs_zero_outputToFile(exp);
      abs_negative_outputToFile(exp);
   }

   private void absMutantGen(Variable var)
   {
      abs_zero_outputToFile(var);
      abs_negative_outputToFile(var);
   }

   private void uoi_boolean_MutantGen(Variable exp)
   {
      uoi_outputToFile(exp,UnaryExpression.NOT);
   }

   private void uoi_boolean_MutantGen(MethodCall exp)
   {
      uoi_outputToFile(exp,UnaryExpression.NOT);
   }

   private void uoi_arithmetic_MutantGen(Variable exp)
   {
      uoi_outputToFile(exp,UnaryExpression.POST_DECREMENT);
      uoi_outputToFile(exp,UnaryExpression.POST_INCREMENT);
      uoi_outputToFile(exp,UnaryExpression.PRE_DECREMENT);
      uoi_outputToFile(exp,UnaryExpression.PRE_INCREMENT);
   }

   private void aorMutantGen(BinaryExpression exp, int op)
   {
	  if (op != BinaryExpression.TIMES)
	  {
         aor_outputToFile(exp,BinaryExpression.TIMES);
      }
      
	  if (op != BinaryExpression.DIVIDE)
	  {
         aor_outputToFile(exp,BinaryExpression.DIVIDE);
      }
      
	  if (op != BinaryExpression.MOD)
	  {
         aor_outputToFile(exp,BinaryExpression.MOD);
      }
       
	  if (op != BinaryExpression.PLUS)
	  {
         aor_outputToFile(exp,BinaryExpression.PLUS);
      }
      
	  if (op != BinaryExpression.MINUS)
	  {
         aor_outputToFile(exp,BinaryExpression.MINUS);
      }
   }

   private void rorMutantGen(BinaryExpression exp, int op)
   {
      if (op != BinaryExpression.LESS)
      {
         ror_outputToFile(exp,BinaryExpression.LESS);
      }
      
      if (op != BinaryExpression.GREATER)
      {
         ror_outputToFile(exp,BinaryExpression.GREATER);
      }
      
      if (op != BinaryExpression.LESSEQUAL)
      {
         ror_outputToFile(exp,BinaryExpression.LESSEQUAL);
      }
      
      if (op != BinaryExpression.GREATEREQUAL)
      {
         ror_outputToFile(exp,BinaryExpression.GREATEREQUAL);
      }
       
      if (op != BinaryExpression.EQUAL)
      {
         ror_outputToFile(exp,BinaryExpression.EQUAL);
      }
      
      if (op != BinaryExpression.NOTEQUAL)
      {
         ror_outputToFile(exp,BinaryExpression.NOTEQUAL);
      }
   }

   /**
    * Output UOI mutants to files
    * @param original
    * @param mutant_op
    */
   public void uoi_outputToFile(Variable original, int mutant_op)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("UOI");
      String mutant_dir = getMuantID("UOI");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 UOI_Writer writer = new UOI_Writer(mutant_dir, out);
		 writer.setMutant(original, mutant_op);
		 comp_unit.accept( writer );
		 out.flush();  
		 out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }

   /**
    * Output UOI mutants to files
    * @param original
    * @param mutant_op
    */
   public void uoi_outputToFile(MethodCall original, int mutant_op)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("UOI");
      String mutant_dir = getMuantID("UOI");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 UOI_Writer writer = new UOI_Writer(mutant_dir, out);
		 writer.setMutant(original, mutant_op);
		 comp_unit.accept( writer );
		 out.flush();  out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }

   public void aor_outputToFile(BinaryExpression original, int mutant_op)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("AOR");
      String mutant_dir = getMuantID("AOR");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 AOR_Writer writer = new AOR_Writer(mutant_dir, out);
		 writer.setMutant(original, mutant_op);
		 comp_unit.accept( writer );
		 out.flush();  out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }

   /**
    * Output LCR mutants to files
    * @param original
    * @param mutant_op
    */
   public void lcr_outputToFile(BinaryExpression original, int mutant_op)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("LCR");
      String mutant_dir = getMuantID("LCR");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 LCR_Writer writer = new LCR_Writer(mutant_dir, out);
		 writer.setMutant(original, mutant_op);
		 comp_unit.accept( writer );
		 out.flush();  
		 out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }

   /**
    * Output LCR mutants to files
    * @param original
    */
   public void lcr_outputToFile(UnaryExpression original)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("LCR");
      String mutant_dir = getMuantID("LCR");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 LCR_Writer writer = new LCR_Writer(mutant_dir, out);
		 writer.setMutant(original);
		 comp_unit.accept( writer );
		 out.flush();  
		 out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }
   
   /**
    * Output ABS (<i>failOnZero()</i> on expression) mutants to files 
    * @param original
    */
   public void abs_zero_outputToFile(BinaryExpression original)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("ABS");
      String mutant_dir = getMuantID("ABS");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 ABS_Writer writer = new ABS_Writer(mutant_dir, out);
		 writer.setZeroFlag(original);
		 comp_unit.accept( writer );
		 out.flush();  
		 out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }

   /**
    * Output ABS (<i>absNeg()</i> on expression) mutants to files
    * @param original
    */
   public void abs_negative_outputToFile(BinaryExpression original)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("ABS");
      String mutant_dir = getMuantID("ABS");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 ABS_Writer writer = new ABS_Writer(mutant_dir, out);
		 writer.setNegativeFlag(original);
		 comp_unit.accept( writer );
		 out.flush();  
		 out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }
   
   /**
    * Output ABS (<i>failOnZero()</i> (on method call) mutants to files
    * @param original
    */
   public void abs_zero_outputToFile(MethodCall original)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("ABS");
      String mutant_dir = getMuantID("ABS");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 ABS_Writer writer = new ABS_Writer(mutant_dir, out);
		 writer.setZeroFlag(original);
		 comp_unit.accept( writer );
		 out.flush();  
		 out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }

   /**
    * Output ABS (<i>absNeg()</i> on method call) mutants to files
    * @param original
    */
   public void abs_negative_outputToFile(MethodCall original)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("ABS");
      String mutant_dir = getMuantID("ABS");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 ABS_Writer writer = new ABS_Writer(mutant_dir, out);
		 writer.setNegativeFlag(original);
		 comp_unit.accept( writer );
		 out.flush();  
		 out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }
   
   /**
    * Output ABS (<i>failOnZero()</i> (on variable) mutants to files
    * @param original
    */
   public void abs_zero_outputToFile(Variable original)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("ABS");
      String mutant_dir = getMuantID("ABS");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 ABS_Writer writer = new ABS_Writer(mutant_dir, out);
		 writer.setZeroFlag(original);
		 comp_unit.accept( writer );
		 out.flush();  
		 out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }

   /**
    * Output ABS (<i>absNeg()</i> on variable) mutants to files
    * @param original
    */
   public void abs_negative_outputToFile(Variable original)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("ABS");
      String mutant_dir = getMuantID("ABS");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 ABS_Writer writer = new ABS_Writer(mutant_dir, out);
		 writer.setNegativeFlag(original);
		 comp_unit.accept( writer );
		 out.flush();  
		 out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }

   /**
    * Output ROR mutants to files
    * @param original
    * @param mutant_op
    */
   public void ror_outputToFile(BinaryExpression original, int mutant_op)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("ROR");
      String mutant_dir = getMuantID("ROR");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 ROR_Writer writer = new ROR_Writer(mutant_dir, out);
		 writer.setMutant(original,mutant_op);
		 comp_unit.accept( writer );
		 out.flush();  
		 out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }

}
