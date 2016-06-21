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
 * <p>Generate JID (Java-specific member variable initialization deletion) --
 *    remove initialization of each member variable
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class JID extends mujava.op.util.Mutator
{
   public JID(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env,comp_unit );
   }

   /**
    * If an instance variable is not final, delete its initialization
    */
   public void visit( FieldDeclaration p ) throws ParseTreeException 
   {
      VariableInitializer initializer = p.getInitializer();
      if (p.getModifiers().contains(ModifierList.FINAL)) 
    	 return;
      
      if (initializer != null) 
    	 outputToFile(p);
   }

   /**
    * Output JID mutants to files
    * @param original
    */
   public void outputToFile(FieldDeclaration original)
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
	     JID_Writer writer = new JID_Writer( mutant_dir, out );
	     writer.setMutant(original);
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
