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
 * <p>Generate LOR (Logical Operator Replacement) mutants --
 *    replace each occurrence of each bitwise logical operator 
 *    (bitwise and-& ,bitwise or-|, exclusive or-^) by each of 
 *    the other operators
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class LOR extends MethodLevelMutator
{
   public LOR(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }

   public void visit( BinaryExpression p ) throws ParseTreeException 
   {
      Expression left = p.getLeft();
      left.accept(this);
      Expression right = p.getRight();
      right.accept(this);

      if ( (getType(p.getLeft()) != OJSystem.BOOLEAN) && 
    	   (getType(p.getRight()) != OJSystem.BOOLEAN))
      {
         int op_type = p.getOperator();
         
         if ( (op_type == BinaryExpression.BITAND) || (op_type == BinaryExpression.BITOR)
               ||(op_type == BinaryExpression.XOR))
         {
            corMutantGen(p, op_type);
         }
      }
   }

   private void corMutantGen(BinaryExpression exp, int op)
   {
      BinaryExpression mutant;

      if (op != BinaryExpression.BITAND)
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.BITAND);
         outputToFile(exp, mutant);
      }
      
      if (op != BinaryExpression.BITOR)
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.BITOR);
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
    * Output LOR mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(BinaryExpression original, BinaryExpression mutant)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("LOR");
      String mutant_dir = getMuantID("LOR");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 LOR_Writer writer = new LOR_Writer(mutant_dir, out);
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
