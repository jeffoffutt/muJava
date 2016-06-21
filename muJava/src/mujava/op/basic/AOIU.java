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
package mujava.op.basic;

import openjava.mop.*;
import openjava.ptree.*;
import java.io.*;

/**
 * <p>Generate AOIU (Arithmetic Operator Insertion (Unary)) mutants --
 *    insert a unary operator (arithmetic -) before each variable or 
 *    expression      
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class AOIU extends Arithmetic_OP
{
   boolean aor_flag = false;

   public AOIU(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }

   /**
    * Set an AOR flag 
    * @param b
    */
   public void setAORflag(boolean b)
   {
      aor_flag = b;
   }

   public void visit( UnaryExpression p ) throws ParseTreeException
   {
     // NO OPERATION
   }

   /**
    * Generate AOIU mutant
    */
   public void visit( Variable p) throws ParseTreeException
   {
      if (isArithmeticType(p))
      {
         outputToFile(p);
      }
   }

   /**
    * Generate AOIU mutant
    */
   public void visit( FieldAccess p ) throws ParseTreeException
   {
      if (isArithmeticType(p))
      {
         outputToFile(p);
      }
   }

   /**
    * Generate AOIU mutant
    */
   public void visit( BinaryExpression p) throws ParseTreeException
   {
      if (aor_flag && isArithmeticType(p)) 
      {
         if ( (p.getOperator() == BinaryExpression.MINUS) || 
        	  (p.getOperator() == BinaryExpression.PLUS)  ||
              (p.getOperator() == BinaryExpression.MOD))
         {
            Expression e1 = p.getLeft();
            super.visit(e1);
            // Ignore right expression because it produce equivalent mutants;
            // Expression e2 = p.getRight();
         } 
         else if ( (p.getOperator() == BinaryExpression.DIVIDE) || 
        		   (p.getOperator() == BinaryExpression.TIMES) )
         {
            Expression e1 = p.getLeft();
            Expression e2 = p.getRight();
            if ( ((e1 instanceof Variable) || (e1 instanceof FieldAccess)) &&
                 ((e2 instanceof Variable) || (e2 instanceof FieldAccess)) )
            {
               // Consider only one expression because it produces equivalent mutants;
               super.visit(e1);
            }
            else
            {
               super.visit(p);
            }
         }
      }
   }

   /**
    * Generate AOIU mutant
    */
   public void visit( AssignmentExpression p ) throws ParseTreeException
   {
      // [ Example ]
      // int a=0;int b=2;int c=4;
      // Right Expression : a = b = -c;
      // Wrong Expression : a = -b = c;
      // Ignore left expression
      Expression rexp = p.getRight();
      rexp.accept( this );
   }

   /***
    * Write AOIU mutants to files
    * @param original_field
    */
   public void outputToFile(FieldAccess original_field)
   {
      if (comp_unit == null) 
    	 return;
      
      String f_name;
      num++;
      f_name = getSourceName("AOIU");
      String mutant_dir = getMuantID("AOIU");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 AOIU_Writer writer = new AOIU_Writer(mutant_dir, out);
		 writer.setMutant(original_field);
         writer.setMethodSignature(currentMethodSignature);
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
    * Write AOIU mutants to files
    * @param original_var
    */
   public void outputToFile(Variable original_var)
   {
      if (comp_unit == null) 
    	 return;
      
      String f_name;
      num++;
      f_name = getSourceName("AOIU");
      String mutant_dir = getMuantID("AOIU");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 AOIU_Writer writer = new AOIU_Writer(mutant_dir, out);
		 writer.setMutant(original_var);
         writer.setMethodSignature(currentMethodSignature);
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
