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
import java.util.Stack;
import java.lang.reflect.*;
import openjava.mop.*;
import openjava.ptree.*;
import mujava.MutationSystem;

/**
 * <p>Generate IOR (Overriding method rename) mutants --
 *    rename the parent's versions of methods that are overridden in a 
 *    subclass so that the overriding does not affect the parents' method
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class IOR extends mujava.op.util.Mutator
{
   Stack s;
   Class parent_class = null;
   FileEnvironment parent_file_env = null;
   CompilationUnit parent_comp_unit = null;

   /**
    * Set parents's version of methods that are overridden in a subclass 
    * @param file_env
    * @param comp_unit
    */
   public void setParentEnv(FileEnvironment file_env, CompilationUnit comp_unit)
   {
      parent_file_env = file_env;
      parent_comp_unit = comp_unit;
   }

   public IOR (FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env,comp_unit );
      s = new Stack();
   }

   public void visit(ClassDeclaration p) throws ParseTreeException
   {
      s.push(p.getName());
      super.visit(p);
      s.pop();
   }

   public void visit(MethodDeclaration p) throws ParseTreeException 
   {
	  if (p.getName().equals("main")) 
		 return;
	  
	  if (isOverridingMethod(p))
	  {
         if (!isIOREquivalent(p)) 
        	outputToFile(parent_comp_unit, p);
      }
	  else
	  {
         return;
      }
   }

   boolean isIOREquivalent(MethodDeclaration p)
   {
      IOR_Equivalent obj = new IOR_Equivalent( parent_file_env, parent_comp_unit );
      String str = parent_class.getName();
      
      int index = str.lastIndexOf(".");
      if (index >= 0)
      {
         str = str.substring(index+1, str.length());
      }
      
      obj.setInformation(str, p);
      
      try 
      {
         parent_comp_unit.accept(obj);
         return (obj.isEquivalent());
      } catch (Exception e)
      {
         e.printStackTrace();
         return false;
      }
   }
 
   boolean isOverridingMethod(MethodDeclaration p)
   {
      String target_class = comp_unit.getPackage();
      for (int i=0; i<s.size(); i++)
      {
         if (i == 0)
         {
            if (target_class == null)
            {
               target_class = s.get(i).toString();
            }
            else
            {
               target_class = target_class + "." + s.get(i).toString();
            }
         }
         else
         {
            target_class = target_class + "$" + s.get(i).toString();
         }
      }
      
      try
      {
         parent_class = Class.forName(target_class).getSuperclass();
         if ((parent_class == null) || (parent_class.getName().equals("java.lang.Object"))) 
        	return false;
         
         Method[] parent_method = parent_class.getDeclaredMethods();
         if (parent_method == null) 
        	return false;
         
         for (int i=0; i<parent_method.length; i++)
         {
            // System.out.println(i + "  " + p.getName()+ " : " + parent_method[i].getName());
            if (Modifier.isAbstract(parent_method[i].getModifiers())) 
               continue;
            if ( !(parent_method[i].getName().equals(p.getName())) ) 
               continue;
            if ( !(parent_method[i].getReturnType().getName().equals(p.getReturnType().getName())) ) 
               continue;
        
            Class[] parent_par = parent_method[i].getParameterTypes();
            ParameterList child_parList = p.getParameters();
            int size = child_parList.size();
            
            if ( ((parent_par==null) || (parent_par.length==0)) && (size==0) )
            { 
               return true;
            }
        
            if (parent_par.length != size) 
               continue;
       
            boolean isFound = true;
            for (int j=0; j<size; j++)
            {
               // System.out.print(parent_par[j].getName()+" : " + child_parList.get(j).getTypeSpecifier().getName());
               if ( !(parent_par[j].getName().equals(child_parList.get(j).getTypeSpecifier().getName())) )
               {
                  isFound = false;
                  break;
               }
            }
            
            if (isFound) 
               return true;
         }
         return false;
      } catch (ClassNotFoundException e)
      {
         System.out.println(" [Exception at IOD mutant generator]");
         e.printStackTrace();
      }
      return false;
   }

   String getFileName()
   {
      // make directory for the mutant
      String dir_name = MutationSystem.MUTANT_PATH + "/" + getClassName() + "_" + this.num;
      File f = new File(dir_name);
      f.mkdir();
      String file_name = parent_class.getName();
      file_name = file_name.substring(file_name.lastIndexOf(".")+1, file_name.length());
      return(dir_name + "/" +  file_name + ".java");
   } 

   /**
    * Output IOR mutants to files
    * @param comp_unit
    * @param mutant
    */
   public void outputToFile(CompilationUnit comp_unit, MethodDeclaration mutant)
   {
      if (comp_unit == null) 
    	 return;

      String f_name;
      num++;
      f_name = getFileName();
      // Change the file name as the name of the child class
      String mutant_dir = getMuantID();

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 IOR_Writer writer = new IOR_Writer(parent_file_env, mutant_dir, out);
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
