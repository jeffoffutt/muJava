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
 * <p>Generate AORU (Arithmetic Operator Replacement (Unary)) mutants --
 *    replace each occurrence of one of the arithmetic operators + and - 
 *    by each of the other operators 
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class AORU extends MethodLevelMutator
{
   public AORU(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }

   /**
    * If a given unary expression contains an arithmetic operator + or -,
    * generate an AORU mutant
    */
   public void visit( UnaryExpression p ) throws ParseTreeException
   {
      int op = p.getOperator();
      if ( (op == UnaryExpression.MINUS) || (op == UnaryExpression.PLUS) ) 
      {
         genBasicUnaryMutants(p,op);
      }
   }

   void genBasicUnaryMutants(UnaryExpression p, int op)
   {
      UnaryExpression mutant;
      if ( op == UnaryExpression.PLUS )
      {
         mutant = (UnaryExpression)(p.makeRecursiveCopy());
         mutant.setOperator(UnaryExpression.MINUS);
         outputToFile(p, mutant);
      }
      else if ( op == UnaryExpression.MINUS )
      {
         mutant = (UnaryExpression)(p.makeRecursiveCopy());
         mutant.setOperator(UnaryExpression.PLUS);
         outputToFile(p, mutant);
      }
   }

   /**
    * Output AORU mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(UnaryExpression original, UnaryExpression mutant)
   {
      if (comp_unit == null) 
    	 return;
      
      String f_name;
      num++;
      f_name = getSourceName("AORU");
      String mutant_dir = getMuantID("AORU");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 AORU_Writer writer = new AORU_Writer(mutant_dir, out);
		 writer.setMutant(original, mutant);
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
