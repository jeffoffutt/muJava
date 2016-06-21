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

import org.hamcrest.core.IsInstanceOf;

/**
 * <p>Generate CDL (Constants DeLetion) mutants
 * </p>
 * @author Lin Deng
 * @version 1.0
  */

public class CDL extends Arithmetic_OP
{
   public CDL(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }
 
   public void visit( BinaryExpression p ) throws ParseTreeException 
   {
      Expression left = p.getLeft();
      left.accept(this);

      Expression right = p.getRight();
      right.accept(this);
      
      aorMutantGen(p);
   }
   
   public void visit(UnaryExpression p) throws ParseTreeException
   {
//	   Expression mutant = p.getExpression();
//	   aor_outputToFile(p, mutant);
	   Expression expression = p.getExpression();
	   expression.accept(this);
	   aorMutantGen(p);
   }
   
//   public void visit(AssignmentExpression p) throws ParseTreeException
//   {
//	   Expression left = p.getLeft();
//	   left.accept(this);
//	   Expression right = p.getRight();
//	   right.accept(this);
//	   aorMutantGen(p);
//   }
   
  

//   private void aorMutantGen(AssignmentExpression exp) {	
//	   if(exp.getOperator()!=0)  // not = single
//	   {
//		   AssignmentExpression mutant = new AssignmentExpression(exp.getLeft(), exp.getOperator(), exp.getRight());
//			mutant.setOperator(0);  // rebuild a mutant and set the op to =
//			aor_outputToFile(exp, mutant);
//	   }
//
//	
//}

private void aorMutantGen(UnaryExpression exp) {
	      Expression mutant = exp.getExpression();
	      Variable mutant2 = new Variable(" ");
	     // System.out.println(exp+" => "+mutant2);
	      if(!(mutant instanceof Variable) 
	    		  && !(mutant instanceof UnaryExpression) 
	    		  && !(mutant instanceof BinaryExpression)
	    		  && !(mutant instanceof AssignmentExpression)
	    		  && !(mutant instanceof ArrayAccess)
	    		  && !(mutant instanceof MethodCall)
	    		  && !(mutant instanceof ConditionalExpression)
	    		  && !(mutant instanceof CastExpression)
	    		  && !(mutant instanceof AllocationExpression)
	    		  && !(mutant instanceof ArrayAllocationExpression)
	    		  && !(mutant instanceof FieldAccess))  // if it is constant
	      {
	    	  aor_outputToFile(exp, mutant2);
	      }
}
/*
*   UnaryExpression v
*   BinaryExpression v
*   ConditionalExpression v
*   AssignmentExpression v
*   CastExpression v
*   AllocationExpression v
*   ArrayAllocationExpression v
*   Variable v
*   MethodCall v
*   SpecialName
*   Literal
*   ClassLiteral
*   ArrayAccess v
*   FieldAccess v
*/

private void aorMutantGen(BinaryExpression exp)
   {
      Expression mutantLeft = exp.getLeft();
      Expression mutantRight = exp.getRight();
      if(!(mutantLeft instanceof Variable) 
    		  && !(mutantLeft instanceof UnaryExpression) 
    		  && !(mutantLeft instanceof BinaryExpression)
    		  && !(mutantLeft instanceof AssignmentExpression)
    		  && !(mutantLeft instanceof ArrayAccess)
    		  && !(mutantLeft instanceof MethodCall)
    		  && !(mutantLeft instanceof ConditionalExpression)
    		  && !(mutantLeft instanceof CastExpression)
    		  && !(mutantLeft instanceof AllocationExpression)
    		  && !(mutantLeft instanceof ArrayAllocationExpression)
    		  && !(mutantLeft instanceof FieldAccess)) // if left is not variable, it's constant???
      {
    	  aor_outputToFile(exp, mutantRight); // delete it, only left right
      }
      if(!(mutantRight instanceof Variable) 
    		  && !(mutantRight instanceof UnaryExpression) 
    		  && !(mutantRight instanceof BinaryExpression)
    		  && !(mutantRight instanceof AssignmentExpression)
    		  && !(mutantRight instanceof ArrayAccess)
    		  && !(mutantRight instanceof MethodCall)
    		  && !(mutantRight instanceof ConditionalExpression)
    		  && !(mutantRight instanceof CastExpression)
    		  && !(mutantRight instanceof AllocationExpression)
    		  && !(mutantRight instanceof ArrayAllocationExpression)
    		  && !(mutantRight instanceof FieldAccess)) // if right is not variable, it's constant???
      {
    	  aor_outputToFile(exp, mutantLeft);  // delete it, only left left 
      }
      
      
   }

   /**
    * Output ODL mutants to file
    * @param original
    * @param mutant
    */
   public void aor_outputToFile(BinaryExpression original, BinaryExpression mutant)
   {
      if (comp_unit == null) 
         return;

      String f_name;
      num++;
      f_name = getSourceName("CDL");
      String mutant_dir = getMuantID("CDL");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 ODL_Writer writer = new ODL_Writer(mutant_dir, out);
		 writer.setMutant(original, mutant);
         writer.setMethodSignature(currentMethodSignature);
		 comp_unit.accept( writer );
		 out.flush();  out.close();
      } catch ( IOException e ) 
      {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) 
      {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }
   
   public void aor_outputToFile(BinaryExpression original, Expression mutant)
   {
	      if (comp_unit == null) 
	          return;

	       String f_name;
	       num++;
	       f_name = getSourceName("CDL");
	       String mutant_dir = getMuantID("CDL");

	       try 
	       {
	 		 PrintWriter out = getPrintWriter(f_name);
	 		 ODL_Writer writer = new ODL_Writer(mutant_dir, out);
	 		 writer.setMutant(original, mutant);
	          writer.setMethodSignature(currentMethodSignature);
	 		 comp_unit.accept( writer );
	 		 out.flush();  out.close();
	       } catch ( IOException e ) 
	       {
	 		 System.err.println( "fails to create " + f_name );
	       } catch ( ParseTreeException e ) 
	       {
	 		 System.err.println( "errors during printing " + f_name );
	 		 e.printStackTrace();
	       }
   }

private void aor_outputToFile(UnaryExpression original, Expression mutant) {
	   if (comp_unit == null) 
	          return;

	       String f_name;
	       num++;
	       f_name = getSourceName("CDL");
	       String mutant_dir = getMuantID("CDL");

	       try 
	       {
	 		 PrintWriter out = getPrintWriter(f_name);
	 		 ODL_Writer writer = new ODL_Writer(mutant_dir, out);
	 		 writer.setMutant(original, mutant);
	          writer.setMethodSignature(currentMethodSignature);
	 		 comp_unit.accept( writer );
	 		 out.flush();  out.close();
	       } catch ( IOException e ) 
	       {
	 		 System.err.println( "fails to create " + f_name );
	       } catch ( ParseTreeException e ) 
	       {
	 		 System.err.println( "errors during printing " + f_name );
	 		 e.printStackTrace();
	       }
	
}

private void aor_outputToFile(AssignmentExpression original, AssignmentExpression mutant) {
    if (comp_unit == null) 
        return;

     String f_name;
     num++;
     f_name = getSourceName("CDL");
     String mutant_dir = getMuantID("CDL");

     try 
     {
		 PrintWriter out = getPrintWriter(f_name);
		 ODL_Writer writer = new ODL_Writer(mutant_dir, out);
		 writer.setMutant(original, mutant);
        writer.setMethodSignature(currentMethodSignature);
		 comp_unit.accept( writer );
		 out.flush();  out.close();
     } catch ( IOException e ) 
     {
		 System.err.println( "fails to create " + f_name );
     } catch ( ParseTreeException e ) 
     {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
     }
	
}

}
