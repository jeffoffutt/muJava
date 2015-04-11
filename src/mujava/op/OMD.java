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
 * <p>Generate OMD (Overloading method deletion) mutants --
 *    delete each overloaded method declaration, one at a time
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class OMD extends DeclAnalyzer
{
   private final String[] primitives = { "char","short","int","long",
                                        "unsigned","unsigned long","float",
                                        "double","lond double"};


   public void translateDefinition(CompilationUnit comp_unit)
        throws openjava.mop.MOPException
   {
      int i, j;

      OJMethod[] m = getDeclaredMethods();

      boolean[] checked = new boolean[m.length];
      int[] compatable_method = new int[m.length];
      for (i=0; i<m.length; i++)
      {
         checked[i] = false;
         compatable_method[i] = -1;
      }

      // Check if overloading methods exist
      for (i=0; i< m.length; i++)
      {
         for (j=0; j<m.length; j++)
         {
            if (j != i)
            {
               if (omdPossible(m[i], m[j]))
               {
                  checked[i] = true;
                  compatable_method[i] = j;
                  break;
               }
            }
         }
      }

      for (i=0; i<m.length; i++)
      {
         if (checked[i])
         {
            try 
            {
               MethodDeclaration original = m[i].getSourceCode();
               outputToFile(comp_unit, original, m[compatable_method[i]]);
               // outputToFile(comp_unit,original,mutant_pars);
            } catch (CannotAlterException e)
            {
               System.err.println("[Error] " + e);
               e.printStackTrace();
            }
         }
      }
   }

   private boolean omdPossible(OJMethod m1, OJMethod m2)
   {
  	  // step 1: it should be same name
 	  if ( !(m1.getName().equals(m2.getName())) ) 
 		 return false;

	  // step 2: it should have compatible index
	  if ( m1.getParameterTypes().length == 0 ) 
		 return false;
	 
	  if ( m1.getParameterTypes().length != m2.getParameterTypes().length ) 
		 return false;

	  int[] related_index = new int[m1.getParameterTypes().length];

      related_index = getCompatibleParameterIndex(m1.getParameterTypes(),m2.getParameterTypes());
      if (related_index != null)
      {
		 return true;
      }
	  return false;
   }

   private boolean same(OJClass[] s1, OJClass[] s2)
   {
      if (s1 == null && s2 == null) 
    	 return true;
      else if (s1 == null || s2 == null) 
    	 return false;
    
      if (s1.length != s2.length) 
    	 return false;
    
      for (int i=0; i<s1.length; i++)
      {
         if ( !(s1[i].getName().equals(s2[i].getName())) ) 
        	return false;
      }
      return true;
   }

   private int getPrimitiveIndex(String s)
   {
      for (int i=0; i<primitives.length; i++)
      {
         if (s.equals(primitives[i])) 
        	return (i+1);
      }
      return 0;
   }

   private int relatedDifferentIndex(String small, String big)
   {
      int small_index = getPrimitiveIndex(small);
      int big_index = getPrimitiveIndex(big);

      if (small_index > 0)
      {
         if (big_index > 2)
         { // from int-> long -> ... ...
            if (big_index > small_index) 
               return (big_index-small_index);
         } 
      } 
      else
      {
         try
         {
            String name = small;
            OJClass clazz;
            int temp = 0;
            while (true)
            {
               temp++;
               clazz = OJClass.forName(name).getSuperclass();
               if ((clazz == null) || (clazz.getName().equals("java.lang.Object"))) 
            	  return 0;
               
               name = clazz.getName();
               if (name.equals("java.lang.Object")) 
            	  return 0;
               
               if (big.equals(name)) 
            	  return temp;
            }
         } catch (OJClassNotFoundException e)
         {
            return 0;
       //}catch(CannotAlterException cae){
         // return false;
         }
      }
      return 0;
   }

   private int[] getCompatibleParameterIndex(OJClass[] smallP, OJClass[] bigP)
   {
      if ( smallP == null || bigP == null ) 
    	 return null;
      
      if ( smallP.length != bigP.length ) 
    	 return null;
      
      if ( same(smallP, bigP)) 
    	 return null;

      int[] results = new int[smallP.length];
 
      boolean diff = false;
      for (int i=0; i<smallP.length; i++)
      {
         results[i] = relatedDifferentIndex(smallP[i].getName(), bigP[i].getName());
         if (results[i] != 0) 
        	 diff = true;
      }
      
      if (!diff) 
    	 return null;
      else 
    	 return results;
   }

   /**
    * Output OMD mutants to files
    * @param comp_unit
    * @param original
    * @param mutant
    */
  //public void outputToFile(CompilationUnit comp_unit,MethodDeclaration original,OJClass[] mutant_pars){
   public void outputToFile(CompilationUnit comp_unit, MethodDeclaration original, OJMethod mutant)
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
         OMD_Writer writer = new OMD_Writer( mutant_dir, out );
         writer.setMutant(original,mutant);
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

   public OMD( openjava.mop.Environment oj_param0, openjava.mop.OJClass oj_param1, openjava.ptree.ClassDeclaration oj_param2 )
   {
      super( oj_param0, oj_param1, oj_param2 );
   }

   public OMD( java.lang.Class oj_param0, openjava.mop.MetaInfo oj_param1 )
   {
      super( oj_param0, oj_param1 );
   }

}
