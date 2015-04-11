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
 * <p>Generate AOIS (Arithmetic Operator Insertion (Short-cut)) mutants --
 *    insert unary operators (increment ++, decrement --) before and after
 *    each variable of an arithmetic type  
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class AOIS extends Arithmetic_OP
{
   boolean isPrePostEQ = true;

   public AOIS(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }

   public void visit( UnaryExpression p ) throws ParseTreeException
   {
      // NO OPERATION
   }

   public void visit( Variable p) throws ParseTreeException
   {
      if (isArithmeticType(p))
      {
         if (isPrePostEQ)
         {
            outputToFile(p, p.toString() + "++" );
            outputToFile(p, p.toString() + "--" );
         }
         else
         {
            outputToFile(p, "++" + p.toString() );
            outputToFile(p, "--" + p.toString() );
            outputToFile(p, p.toString() + "++" );
            outputToFile(p, p.toString()+"--" );
         }
      }
   }

   public void visit( FieldAccess p ) throws ParseTreeException
   {
      if (isArithmeticType(p))
      {
         if (isPrePostEQ)
         {
            outputToFile(p, p.toString() + "++" );
            outputToFile(p, p.toString() + "--" );
         }
         else
         {
            outputToFile(p, "++" + p.toString() );
            outputToFile(p, "--" + p.toString() );
            outputToFile(p, p.toString() + "++" );
            outputToFile(p, p.toString() + "--" );
         }
      }
   }

   public void visit( BinaryExpression p ) throws ParseTreeException 
   {
      isPrePostEQ = false;
      super.visit(p);
      isPrePostEQ = true;
   }

   public void visit( AssignmentExpression p ) throws ParseTreeException
   {
      isPrePostEQ = false;
      Expression rexp = p.getRight();
      rexp.accept( this );
      isPrePostEQ = true;
   }

   /**
    * Write AOIS mutants to files
    * @param original_field
    * @param mutant
    */
   public void outputToFile(FieldAccess original_field, String mutant)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName("AOIS");
      String mutant_dir = getMuantID("AOIS");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 AOIS_Writer writer = new AOIS_Writer(mutant_dir, out);
		 writer.setMutant(original_field, mutant);
         writer.setMethodSignature(currentMethodSignature);
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
    * Write AOIS mutants to files
    * @param original_var
    * @param mutant
    */
   public void outputToFile(Variable original_var, String mutant)
   {
      if (comp_unit == null) 
    	 return;
      
      String f_name;
      num++;
      f_name = getSourceName("AOIS");
      String mutant_dir = getMuantID("AOIS");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 AOIS_Writer writer = new AOIS_Writer(mutant_dir, out);
		 writer.setMutant(original_var, mutant);
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
