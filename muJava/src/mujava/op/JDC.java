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
import mujava.op.util.DeclAnalyzer;

/**
 * <p>Generate JDC (Java-supported default constructor creation) --
 *    delete each declaration of default constructor (with no 
 *    parameter)
 * </p>
 * @author Yu-Seung Ma
 * @update by Nan Li May 2012 fix a bug of JDC
 * @The bug is: when a constructor is deleted, the mutanted program shows // JDC_1(){...}; it supposes to show //class_name(){...}
 * @in other words, the mutanted progam shows the name of the mutant (JDC_1) instead of the name of the constructor (like VendingMachine)
 * @version 1.0
  */ 

public class JDC extends DeclAnalyzer
{
	// the class_name
   public static String class_name = null;
   public void translateDefinition(CompilationUnit comp_unit) throws openjava.mop.MOPException
   {
	  try
	  {
	     OJConstructor[] cons = getDeclaredConstructors();
	     OJConstructor base_const = getDeclaredConstructor(null);

         if (cons == null) 
        	return;

         if (cons.length == 1 && base_const != null)
         {
            StatementList stmts = base_const.getBody();
            if (!(stmts.isEmpty()))
            {
               ConstructorDeclaration original = base_const.getSourceCode();
               outputToFile(comp_unit, original);
            }
	     }
	  } catch(NoSuchMemberException e1)
	  {
   	    // default constructor does not exist
	    // No operation
	  } catch(Exception ex)
	  {
	     System.err.println("JDC : " + ex);
	  }
   }

   /**
    * Output JDC mutants to files
    * @param comp_unit
    * @param mutant
    */
   public void outputToFile(CompilationUnit comp_unit, ConstructorDeclaration mutant)
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
	     JDC_Writer writer = new JDC_Writer( mutant_dir, out );
	     
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

   public JDC( openjava.mop.Environment oj_param0, openjava.mop.OJClass oj_param1, openjava.ptree.ClassDeclaration oj_param2 )
   {
      super( oj_param0, oj_param1, oj_param2 );
      //initialize the class name
      class_name = oj_param2.getName();
   }

   public JDC( java.lang.Class oj_param0, openjava.mop.MetaInfo oj_param1 )
   {
      super( oj_param0, oj_param1 );
   }
}
