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
 * <p>Generate LOI (Logical Operator Insertion) mutants --
 *    insert bitwise logical operators (bitwise and-&, bitwise or-|,
 *    exclusive or-^)
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class LOI extends Arithmetic_OP
{
   public LOI(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }

   public void visit( Variable p) throws ParseTreeException
   {
      if (isArithmeticType(p))
      {
         outputToFile(p);
      }
   }

   public void visit( FieldAccess p ) throws ParseTreeException
   {
      if (isArithmeticType(p))
      {
         outputToFile(p);
      }
   }

   public void visit( AssignmentExpression p ) throws ParseTreeException
   {
      Expression lexpr = p.getLeft();

      if ((lexpr instanceof Variable) || (lexpr instanceof FieldAccess))
      {
    	 // do nothing
      }
      else
      {
	     lexpr.accept( this );
      }
      
      Expression rexp = p.getRight();
      rexp.accept( this );
   }

   /**
    * Output LOI mutants to files
    * @param original_field
    */
   public void outputToFile(FieldAccess original_field)
   {
      if (comp_unit == null) 
    	 return;
      
      String f_name;
      num++;
      f_name = getSourceName("LOI");
      String mutant_dir = getMuantID("LOI");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 LOI_Writer writer = new LOI_Writer(mutant_dir, out);
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
    * Output LOI mutants to files
    * @param original_var
    */
   public void outputToFile(Variable original_var)
   {
      if (comp_unit == null) 
    	 return;
      
      String f_name;
      num++;
      f_name = getSourceName("LOI");
      String mutant_dir = getMuantID("LOI");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 LOI_Writer writer = new LOI_Writer(mutant_dir, out);
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
