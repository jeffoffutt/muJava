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
import java.util.Vector;
import openjava.mop.*;
import openjava.ptree.*;

/**
 * <p>Generate JSI (Java-specific static modifier insertion) --
 *    add the <i>static</i> modifier to instance variables
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class JSI extends mujava.op.util.Mutator
{
   Vector nonStaticFields;
   boolean isField;

   public JSI(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env,comp_unit );
      nonStaticFields = new Vector();
      isField = false;
   }

   public void visit( FieldDeclaration p ) throws ParseTreeException 
   {
      if (!(p.getModifiers().contains(ModifierList.STATIC)))
      {
         // nonStaticFields.add(p);
         outputToFile(p);
      }
   }

   /**
    * Output JSI mutants to files
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
	 	 JSI_Writer writer = new JSI_Writer( mutant_dir, out );
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
