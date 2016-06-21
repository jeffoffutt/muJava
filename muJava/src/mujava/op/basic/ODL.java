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
 * <p>Generate ODL (Arithmetic Operator Replacement (Binary)) mutants -- 
 *    replace an arithmetic operator by each of the other operators  
 *    (*, /, %, +, -)
 * </p>
 * @author Lin Deng
 * @version 1.0
  */

public class ODL extends Arithmetic_OP
{
   public ODL(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }
 
   // visit Binary Exp
   public void visit( BinaryExpression p ) throws ParseTreeException 
   {
      Expression left = p.getLeft();
      left.accept(this);

      Expression right = p.getRight();
      right.accept(this);
      
      aorMutantGen(p);
   }
   
   // visit Unary Exp
   public void visit(UnaryExpression p) throws ParseTreeException
   {
//	   Expression mutant = p.getExpression();
//	   aor_outputToFile(p, mutant);
	   Expression expression = p.getExpression();
	   expression.accept(this);
	   aorMutantGen(p);
   }
   
   // visit assignment
   public void visit(AssignmentExpression p) throws ParseTreeException
   {
	   Expression left = p.getLeft();
	   left.accept(this);
	   Expression right = p.getRight();
	   right.accept(this);
	   aorMutantGen(p);
   }
   
  
   // for assignment, remove += -= *= ... with =
   private void aorMutantGen(AssignmentExpression exp) {	
	   if(exp.getOperator()!=0)
	   {
		   AssignmentExpression mutant = new AssignmentExpression(exp.getLeft(), exp.getOperator(), exp.getRight());
			mutant.setOperator(0);
			
			
			
			aor_outputToFile(exp, mutant);
	   }

	
}

private void aor_outputToFile(AssignmentExpression original, AssignmentExpression mutant) {
    if (comp_unit == null) 
        return;

     String f_name;
     num++;
     f_name = getSourceName("ODL");
     String mutant_dir = getMuantID("ODL");

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

// 
private void aorMutantGen(UnaryExpression exp) {
	      Expression mutant = exp.getExpression();
	      
	     //System.out.println(exp+" => "+mutant);
	      
	      aor_outputToFile(exp, mutant);
	
}

private void aorMutantGen(BinaryExpression exp)
   {
      Expression mutantLeft = exp.getLeft();
      Expression mutantRight = exp.getRight();
      
//      if(! (exp.getLeft() instanceof Variable))
      {
//    	  System.out.println(exp.getLeft() + " is variable");
//    	  System.out.println(exp +" "+mutantLeft);
    	  aor_outputToFile(exp, mutantLeft);
      }
      
//      if(! (exp.getRight() instanceof Variable))
      {
//    	  System.out.println(exp.getRight() + " is variable");
//    	  System.out.println(exp +" "+mutantRight);
    	  aor_outputToFile(exp, mutantRight);
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
      f_name = getSourceName("ODL");
      String mutant_dir = getMuantID("ODL");

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
	       f_name = getSourceName("ODL");
	       String mutant_dir = getMuantID("ODL");

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
	       f_name = getSourceName("ODL");
	       String mutant_dir = getMuantID("ODL");

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
