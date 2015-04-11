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
 * <p>Generate AODU (Arithmetic Operator Deletion (Unary)) mutants --
 *    delete a unary operator (arithmetic -) before each variable or 
 *    expression
 * </p> 
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class AODU extends Arithmetic_OP
{
   boolean aor_flag = false;

   public AODU(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }

   /**
    * Set AOR flag 
    * @param b
    */
   public void setAORflag(boolean b)
   {
      aor_flag = b;
   }

   public void visit( BinaryExpression p) throws ParseTreeException
   {
      // Examine equivalent
      if (aor_flag && isArithmeticType(p)) 
      {
         if ( (p.getOperator() == BinaryExpression.MINUS) || 
              (p.getOperator() == BinaryExpression.PLUS)  ||
              (p.getOperator() == BinaryExpression.MOD))
         {
            Expression e1 = p.getLeft();
            super.visit(e1);
         }
      }
   }

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

   public void visit( UnaryExpression p ) throws ParseTreeException
   {
      if (isArithmeticType(p))
      {
         int op = p.getOperator();
         if ( (op == UnaryExpression.MINUS) || (op == UnaryExpression.PLUS) )
         {
            outputToFile(p);
         } 
      }
   }

   /**
    * Write AODU mutants to files
    * @param original
    */
   public void outputToFile(UnaryExpression original)
   {
      if (comp_unit == null) 
    	 return;
      
      String f_name;
      num++;
      f_name = getSourceName("AODU");
      String mutant_dir = getMuantID("AODU");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 AODU_Writer writer = new AODU_Writer(mutant_dir, out);
		 writer.setMutant(original);
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
