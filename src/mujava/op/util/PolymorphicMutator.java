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
package mujava.op.util;

import openjava.mop.*;
import openjava.ptree.*;
import java.lang.reflect.*;

/**
 * <p>Mutant Generator for the mutation operator that are related with polymorphism. </p>
 * @author Yu-Seung Ma
 * @version 1.0
*/

public class PolymorphicMutator extends Mutator
{
   protected boolean hasHidingVariable(String childClass, String parentClass)
   {
      try
      {
         Class child_class = Class.forName(childClass);
         Class parent_class = Class.forName(parentClass);
         Field[] child_fs = child_class.getDeclaredFields();
         Field[] parent_fs = parent_class.getDeclaredFields();
        
         for (int i=0; i<child_fs.length; i++)
         {
            for (int j=0; j<parent_fs.length; j++)
            {
               if (sameSignature(child_fs[i], parent_fs[j])) 
            	  return true;
            }
         }
      } catch (ClassNotFoundException e)
      {
         return false;
      }
      return false;
   }

   protected boolean sameSignature(Field f1, Field f2)
   {
      if (!(f1.getName().equals(f2.getName()))) 
    	 return false;
    
      if (!(f1.getType().getName().equals(f2.getType().getName()))) 
    	 return false;
    
      return true;
   }

   protected boolean sameSignature(Method m1, Method m2)
   {
      if (!(m1.getName().equals(m2.getName()))) 
    	 return false;
    
      if (!m1.getReturnType().getName().equals(m2.getReturnType().getName())) 
    	 return false;
    
      Class[] par1 = m1.getParameterTypes();
      Class[] par2 = m2.getParameterTypes();
      
      if ((par1 == null) && (par2 == null)) 
    	 return true;
    
      if ( ((par1 == null) && (par2 != null)) || ((par1 != null) && (par2 == null)) ) 
    	 return false;
    
      if (par1.length != par2.length) 
    	 return false;
    
      for (int i=0; i<par1.length; i++)
      {
         if (!(par1[i].getName().equals(par2[i].getName()))) 
        	return false;
      }
      return true;
   }

   public PolymorphicMutator( Environment env , CompilationUnit comp_unit ) 
   {
      super( env, comp_unit );
   }
}