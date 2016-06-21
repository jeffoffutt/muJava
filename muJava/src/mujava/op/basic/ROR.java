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
 * <p>Generate ROR (Rational Operator Replacement) mutants --
 *    replace each occurrence of one of the relational operators 
 *    (<, <=, >, >=, =, <>) by each of the other operators 
 *    and by <i>falseOp</i> and <i>trueOp</i> where 
 *    <i>falseOp</i> always returns <i>false</i> and 
 *    <i>trueOp</i> always returns <i>true</i> 
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class ROR extends Arithmetic_OP
{
   public ROR(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }

   public void visit( BinaryExpression p ) throws ParseTreeException 
   {
      Expression left = p.getLeft(); 
      left.accept(this);
      Expression right = p.getRight();
      right.accept(this);

      int op_type = p.getOperator();  
    
      if (isArithmeticType(p.getLeft()) && isArithmeticType(p.getRight()))
      {
    	  // fix the fault that missed <, Lin, 050814
         if ((op_type == BinaryExpression.GREATER) || (op_type == BinaryExpression.GREATEREQUAL) ||
             (op_type == BinaryExpression.LESSEQUAL) || (op_type == BinaryExpression.EQUAL) ||
             (op_type == BinaryExpression.NOTEQUAL)
             || (op_type == BinaryExpression.LESS))
         {
        	 primitiveRORMutantGen(p, op_type);
         }
      }
      else if ( (op_type == BinaryExpression.EQUAL) || (op_type == BinaryExpression.NOTEQUAL) )
      {
    	  objectRORMutantGen(p, op_type);
      }
   }

   private void primitiveRORMutantGen(BinaryExpression exp, int op)
   {
      BinaryExpression mutant;
      
      /**
       * the traditional ROR implementation
       */
      
      if (op != BinaryExpression.GREATER)
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.GREATER);
         outputToFile(exp, mutant);
      }
      
      if (op != BinaryExpression.GREATEREQUAL)
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.GREATEREQUAL);
         outputToFile(exp, mutant);
      }
     
      if (op != BinaryExpression.LESS)
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.LESS);
         outputToFile(exp, mutant);
      }
      
      if (op != BinaryExpression.LESSEQUAL)
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.LESSEQUAL);
         outputToFile(exp, mutant);
      }
 
      if (op != BinaryExpression.EQUAL)
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.EQUAL);
         outputToFile(exp, mutant);
      }
       
      if (op != BinaryExpression.NOTEQUAL)
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.NOTEQUAL);
         outputToFile(exp, mutant);
      }
      
      //Complete the full implementation of ROR
      //Note here the mutant is a type of Literal not a binary expression
      //Updated by Nan Li
      //Dec 6 2011
      
      //Change the expression to true 
      outputToFile(exp, Literal.makeLiteral(true));
      //Change the expression to false
      outputToFile(exp, Literal.makeLiteral(false));
      
      
      /**
       * New implementation of ROR based on the fault hierarchies
       * fewer ROR mutants are generated
       * For details, see the paper "Better predicate testing" by Kaminski, Ammann, and Offutt at AST'11
       * This part is currently experimental, which means, users will not see this part during the new release
       */
     /* 
      if (op == BinaryExpression.GREATER)
      {
    	 //mutant >=
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.GREATEREQUAL);
         outputToFile(exp, mutant);
         
         //mutant !=
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.NOTEQUAL);
         outputToFile(exp, mutant);
         
    	 //mutant false
         outputToFile(exp, Literal.makeLiteral(false));
      }
      
      if (op == BinaryExpression.GREATEREQUAL)
      {
    	 //mutant true
         outputToFile(exp, Literal.makeLiteral(true));
         
         //mutant >
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.GREATER);
         outputToFile(exp, mutant);
         
         //mutant ==
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.EQUAL);
         outputToFile(exp, mutant);
      }
     
      if (op == BinaryExpression.LESS)
      {
     	 //mutant false
         outputToFile(exp, Literal.makeLiteral(false));
         
         //mutant <=
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.LESSEQUAL);
         outputToFile(exp, mutant);
         
         //mutant !=
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.NOTEQUAL);
         outputToFile(exp, mutant);
      
      }
      
      if (op == BinaryExpression.LESSEQUAL)
      {
     	 //mutant true
         outputToFile(exp, Literal.makeLiteral(true));
          
         //mutant <
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.LESS);
         outputToFile(exp, mutant);
         
         //mutant ==
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.EQUAL);
         outputToFile(exp, mutant);
      }
 
      if (op == BinaryExpression.EQUAL)
      {
      	 //mutant false
         outputToFile(exp, Literal.makeLiteral(false));
         
         //mutant <=
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.LESSEQUAL);
         outputToFile(exp, mutant);
         
         //mutant >=
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.GREATEREQUAL);
         outputToFile(exp, mutant);
      }
       
      if (op == BinaryExpression.NOTEQUAL)
      {
      	 //mutant false
         outputToFile(exp, Literal.makeLiteral(true));
         
         //mutant <
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.LESS);
         outputToFile(exp, mutant);
         
         //mutant >
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.GREATER);
         outputToFile(exp, mutant);
      }
      
      */
   }

   private void objectRORMutantGen(BinaryExpression exp, int op)
   {
      BinaryExpression mutant;
      if (op != BinaryExpression.EQUAL)
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.EQUAL);
         outputToFile(exp, mutant);
      }
      
      if (op != BinaryExpression.NOTEQUAL)
      {
         mutant = (BinaryExpression)(exp.makeRecursiveCopy());
         mutant.setOperator(BinaryExpression.NOTEQUAL);
         outputToFile(exp, mutant);
      }
   }

   /**
    * Output ROR mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(BinaryExpression original, BinaryExpression mutant)
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
		 writer.setMutant(original, mutant);
         writer.setMethodSignature(currentMethodSignature);
		 comp_unit.accept( writer );
		 out.flush();  
		 out.close();
      } catch ( IOException e ) 
      {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }
   
   /**
    * Output ROR mutants (true or false) to files
    * @param original
    * @param mutant
    */
   public void outputToFile(BinaryExpression original, Literal mutant)
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
		 writer.setMutant(original, mutant);
         writer.setMethodSignature(currentMethodSignature);
		 comp_unit.accept( writer );
		 out.flush();  
		 out.close();
      } catch ( IOException e ) 
      {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }
}
