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
package mujava.op;

import java.io.*;
import openjava.mop.*;
import openjava.ptree.*;

/**
 * <p>Generate IPC (Explicit call to parent's constructor deletion) mutants --
 *    delete <i>super</i> constructor calls, causing the default constructor
 *    of the parent class to be called 
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class IPC extends mujava.op.util.Mutator
{
   // ClassDeclaration my_class;
   public IPC(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env, comp_unit );
	  //my_class = cdecl;
   }

   public void visit( ConstructorInvocation p ) throws ParseTreeException
   {
      if (!p.isSelfInvocation())
      {
         if (p.getArguments().size() > 0)
         {
            outputToFile(p);
         }
      }
   }

   /**
    * Output IPC mutants to files
    * @param mutant
    */
   public void outputToFile(ConstructorInvocation mutant)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getSourceName(this);
      String mutant_dir = getMuantID();

      try 
      {
	     PrintWriter out = getPrintWriter(f_name);
	     IPC_Writer writer = new IPC_Writer( mutant_dir, out );
	     writer.setMutant(mutant);
	     comp_unit.accept( writer );
	     out.flush();  
	     out.close();
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
