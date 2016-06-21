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

import java.util.Vector;
import openjava.mop.*;
import openjava.ptree.*;
import java.io.*;
import mujava.MutationSystem;
import mujava.util.InheritanceINFO;
import java.lang.reflect.*;

/**
 * <p>Generate PNC (New method call with child class type) mutants --
 *    change the instantiated type of an object reference to cause the
 *    object reference to refer to an object of a type that is different 
 *    from the declared type      
 * </p>
 * <p><i>Example</i>: let class A be the parent of class B -- 
 *        A a; a = new A(); is mutated to A a; a = new B(); 
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class PNC extends mujava.op.util.Mutator
{
   public PNC(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env, comp_unit );
   }

   /**
    * Generate PNC mutant and write output to file
    * @param type
    * @param p
    * @param child
    */
   public void generateMutants(OJClass type, AllocationExpression p, Vector child)
   {
      // Does not care types of argument
      for (int i=0; i<child.size(); i++)
      {
         InheritanceINFO info = (InheritanceINFO)child.get(i);
         // Examine equivalent -- by examine if child class has overriding method.
         if (hasOverridingMethod(type, info))
         {
            AllocationExpression mutant = (AllocationExpression)p.makeRecursiveCopy();
            mutant.setClassType(new TypeName(info.getClassName()));
            outputToFile(p, mutant);
         }
         generateMutants(type, p, info.getChilds());
      }
   }

   /** 
    * Determine whether the child class has an overriding method
    * @param clazz
    * @param child
    * @return true, otherwise return false
    */
   public boolean hasOverridingMethod(OJClass clazz, InheritanceINFO child)
   {
      try
      {
         Class child_class = Class.forName(child.getClassName());
         Method[] child_methods = child_class.getDeclaredMethods();
         OJMethod[] parent_methods = clazz.getDeclaredMethods();
         if ( ((child_methods == null) || (child_methods.length == 0)) ||
              ((parent_methods == null) || (parent_methods.length == 0)) ) 
        	return false;

         for (int i=0; i<parent_methods.length; i++)
         {
            for (int j=0; j<child_methods.length; j++)
            {
               if (isOverridingMethod(parent_methods[i], child_methods[j])) 
            	  return true;
            }
         }
         return false;
      } catch (Exception e)
      {
         e.printStackTrace();
         return false;
      }
   }

   boolean isOverridingMethod(OJMethod parent, Method child)
   {
      if (parent.getModifiers().isAbstract()) 
    	 return false;
      if ( !(parent.getName().equals(child.getName()))) 
    	 return false;
      if ( !(parent.getReturnType().getName().equals(child.getReturnType().getName()))) 
    	 return false;
    
      OJClass[] parent_pars = parent.getParameterTypes();
      Class[] child_pars = child.getParameterTypes();
      
      if ( ((parent_pars == null) || (parent_pars.length == 0)) &&
           ((child_pars == null) || (child_pars.length == 0)) ) 
    	 return true;
    
      if (parent_pars.length != child_pars.length) 
    	 return false;
      
      for (int i=0; i<parent_pars.length; i++)
      {
         if ( !(parent_pars[i].getName().equals(child_pars[i].getName()))) 
        	return false;
      }
      return true;
   }

   public void visit( AllocationExpression p ) throws ParseTreeException 
   {
      String original_name = p.getClassType().getName();
      OJClass type = getType(p.getClassType());
      if (MutationSystem.isPrimitive(type))
      {
         super.visit(p);
      }
      else
      {
         InheritanceINFO inf = MutationSystem.getInheritanceInfo(original_name);
         if (inf != null) 
        	generateMutants(type, p, inf.getChilds());
      }
   }

   /**
    * Output PNC mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(AllocationExpression original, AllocationExpression mutant)
   {
      String f_name;
      num++;
      f_name = getSourceName(this);
      String mutant_dir = getMuantID();

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 PNC_Writer writer = new PNC_Writer(  mutant_dir, out  );
		 writer.setMutant(original, mutant);
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


   /* --- Old Version
   // ---------------------------------
  private boolean pncPossible(String p, String c,ExpressionList arg_list){
    try{
      OJClass clazz;
      OJClass parent_class;
      clazz = OJClass.forName(c);
      if(clazz.getName().equals(p)) return false;
      while( !(clazz.getName().equals("java.lang.Object")) ){
        parent_class = clazz.getSuperclass();
        if(parent_class.getName().equals(p)){
          int arg_num = arg_list.size();
          if(arg_num>0){
            OJClass[] types = new OJClass[arg_num];
            for(int j=0;j<arg_num;j++){
              types[j] = getType(arg_list.get(j));
            }
            if(clazz.getDeclaredConstructor(types)!=null) return true;
          }else{
            // default constructor : There is no constructor declared
            if(clazz.getConstructors().length==0) return true;
            // There is one constrcutor which has no arguments
            if(clazz.getConstructor(null)!=null) return true;
          }
        }
        clazz = parent_class;
      }
      return false;
    }catch(Exception e){
      return false;
    }catch(Error er){
      return false;
    }
  }*/

}
