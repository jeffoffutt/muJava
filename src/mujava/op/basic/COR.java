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
 * <p>Generate COR (Conditional Operator Replacement mutants --
 *    replace each logical operator by each of the other operators 
 *    (and-&&, or-||, and with no conditional evaluation-&, 
 *    or with no conditional evaluation-|, not equivalent-^)    
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class COR extends MethodLevelMutator
{
   public COR(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }

   /**
    * If the operator is one of logical operators, replace it with
    * each of the other logical operators 
    */
   public void visit( BinaryExpression p ) throws ParseTreeException 
   {
      Expression left = p.getLeft();
      left.accept(this);
      Expression right = p.getRight();
      right.accept(this);

      if ( (getType(p.getLeft()) == OJSystem.BOOLEAN) && 
     	  (getType(p.getRight()) == OJSystem.BOOLEAN))
      {
         int op_type = p.getOperator();
         if ( (op_type == BinaryExpression.LOGICAL_AND) ||
          	  (op_type == BinaryExpression.LOGICAL_OR) ||
              (op_type == BinaryExpression.BITAND) ||
              (op_type == BinaryExpression.BITOR) ||
              (op_type == BinaryExpression.XOR))
         {
            corMutantGen(p, op_type);
         }
      }
   }

   private void corMutantGen(BinaryExpression exp, int op)
   {
      BinaryExpression mutant;
      if ((op != BinaryExpression.LOGICAL_AND) && (op != BinaryExpression.BITAND))
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.LOGICAL_AND);
         outputToFile(exp, mutant);
      }
      
      if ((op != BinaryExpression.LOGICAL_OR) && (op != BinaryExpression.BITOR))
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.LOGICAL_OR);
         outputToFile(exp, mutant);
      }
      
      if (op != BinaryExpression.XOR)
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.XOR);
         outputToFile(exp, mutant);
      }
   }

   /**
    * Output COR mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(BinaryExpression original, BinaryExpression mutant)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("COR");
      String mutant_dir = getMuantID("COR");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 COR_Writer writer = new COR_Writer(mutant_dir, out);
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
