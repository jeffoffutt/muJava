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
 * <p>Generate LOD (Logical Operator Deletion) mutants --
 *    delete each occurrence of bitwise logical operators 
 *    (bitwise and-&, bitwise or-|, exclusive or-^)
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class LOD extends MethodLevelMutator
{
   public LOD(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
      super( file_env, comp_unit );
   }

   public void visit( UnaryExpression p ) throws ParseTreeException
   {
      int op = p.getOperator();
      if ( op == UnaryExpression.BIT_NOT)
      {
         outputToFile(p);
      }
   }

   /**
    * Output LOD mutants to files
    * @param original
    */
   public void outputToFile(UnaryExpression original)
   {
      if (comp_unit == null) 
    	 return;
      
      String f_name;
      num++;
      f_name = getSourceName("LOD");
      String mutant_dir = getMuantID("LOD");

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 LOD_Writer writer = new LOD_Writer(mutant_dir,out);
		 writer.setMutant(original);
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
}
