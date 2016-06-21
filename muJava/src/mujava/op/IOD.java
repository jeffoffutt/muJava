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
 * <p>Generate IOD (Overriding method deletion) mutants --
 *    delete an entire declaration of an overriding method
 *    in a subclass so that references to the method uses
 *    the parent's version.    
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class IOD extends DeclAnalyzer
{
   public void translateDefinition(CompilationUnit comp_unit) throws openjava.mop.MOPException
   {
      generate(comp_unit, this);
   }

   /**
    * If a method is not private or final, generate IOD mutants  
    * @param comp_unit
    * @param clazz
    * @throws openjava.mop.MOPException
    */
   public void generate(CompilationUnit comp_unit, OJClass clazz) throws openjava.mop.MOPException
   {
      OJMethod[] d_methods = clazz.getDeclaredMethods();
      OJMethod[] i_methods = clazz.getInheritedMethods();
      if ( d_methods.length == 0 ) 
    	 return;

      for (int i=0; i<d_methods.length; ++i)
      {
    	 // private or final method do not have any hiding side-effect
         if (d_methods[i].getModifiers().isPrivate()) 
        	continue;
         if (d_methods[i].getModifiers().isFinal()) 
        	continue;

         for (int j=0; j<i_methods.length; j++)
         {
            if (isSameNameAndSignature(d_methods[i], i_methods[j]))
            {
               MethodDeclaration original = d_methods[i].getSourceCode();
               outputToFile(comp_unit, original);
               break;
            }
         }
      }

      OJClass[] inner_clazz = clazz.getAllClasses();
      for (int i=0; i<inner_clazz.length; i++)
      {
         // OpenJava did not handle inner class..
         // Therefore, below method do not work properly.
         generate(comp_unit,inner_clazz[i]);
      }
   }


   boolean isSameNameAndSignature(OJMethod m1, OJMethod m2)
   {
      if (!m1.getName().equals(m2.getName())) 
    	 return false;
      
      if (!m1.getReturnType().getName().equals(m1.getReturnType().getName())) 
    	 return false;
      
      if (!m1.getModifiers().toString().equals(m2.getModifiers().toString())) 
    	 return false;
      
      OJClass[] p1 = m1.getParameterTypes();
      OJClass[] p2 = m2.getParameterTypes();
      
      if (p1.length == 0 && p2.length == 0) 
    	 return true;
      
      if (p1.length != p2.length) 
    	 return false;
      
      for (int i=0; i<p1.length; i++)
      {
         if (!p1[i].getName().equals(p2[i].getName())) 
        	return false;
      }
      return true;
   }

   /**
    * Output IOD mutants to files
    * @param comp_unit
    * @param target
    */
   public void outputToFile(CompilationUnit comp_unit, MethodDeclaration target)
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
         IOD_Writer writer = new IOD_Writer( mutant_dir, out );
         writer.setMutant(target);
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

   public IOD( openjava.mop.Environment oj_param0, openjava.mop.OJClass oj_param1, openjava.ptree.ClassDeclaration oj_param2 )
   {
      super( oj_param0, oj_param1, oj_param2 );
   }

   public IOD( java.lang.Class oj_param0, openjava.mop.MetaInfo oj_param1 )
   {
      super( oj_param0, oj_param1 );
   }
}

