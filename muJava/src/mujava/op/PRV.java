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
import java.util.*;
import openjava.mop.*;
import openjava.ptree.*;
import mujava.MutationSystem;
import mujava.openjava.extension.ExtendedClosedEnvironment;


/**
 * <p>Generate PRV (Reference assignment with other compatible variable) mutants --
 *    change operands of a reference assignment to be assigned to objects 
 *    of subclasses
 * </p>
 * <p><i>Example</i>: Object obj; String s = "Hello"; Integer i = new Integer(4); obj = s; 
 *      is mutated to <br/> 
 *      Object obj; String s = "Hello"; Integer i = new Integer(4); obj = i;
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class PRV extends mujava.op.util.Mutator
{
   Vector field_list = new Vector();
   Vector var_list = new Vector();

   public PRV(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env, comp_unit );
   }

   /**
    * Retrieve variable to be mutated
    * @return variable's name, otherwise return null
    * @throws ParseTreeException
    */
   public String[] getAccessibleVariables() throws ParseTreeException 
   {
      HashSet var_set = new HashSet();

      if (env instanceof ClosedEnvironment)
      {
    	 ExtendedClosedEnvironment mujava_env = new ExtendedClosedEnvironment(env);
         String[] vars = mujava_env.getAccessibleVariables();
         
         if (vars != null)
         {
            for (int j=0; j<vars.length; j++)
            {
               var_set.add(vars[j]);
            }
         }
         mujava_env = null;
      }

      for (int i=(env_nest.size()-1); i>=0; i--)
      {
         Environment temp_env = (Environment)(env_nest.get(i));
         if (temp_env instanceof ClosedEnvironment)
         {
            ExtendedClosedEnvironment mujava_env = new ExtendedClosedEnvironment(temp_env);
            String[] vars = mujava_env.getAccessibleVariables();
            if (vars != null)
            {
               for (int j=0; j<vars.length; j++)
               {
                  var_set.add(vars[j]);
               }
            }
            mujava_env = null;
         }
      }

      OJClass clazz = getSelfType();
      OJField[] fs = clazz.getDeclaredFields();
      if (fs != null)
      {
         for (int k=0; k<fs.length; k++)
         {
            var_set.add(fs[k].getName());
         }
      }

      int num = var_set.size();
      if (num > 0)
      {
         String[] results = new String[num];
         Iterator iter = var_set.iterator();
         for (int k=0; k<num; k++)
         {
            results[k] = (String)iter.next();
         }
         return results;
      }
      return null;
   }

   public void visit( AssignmentExpression p ) throws ParseTreeException 
   {
      Expression lexp = p.getLeft();
      Expression rexp = p.getRight();

      if ( (lexp instanceof Variable || lexp instanceof FieldAccess) &&
           (rexp instanceof Variable || rexp instanceof FieldAccess) )
      {
         OJClass type = getType(lexp);
         String[] vars = getAccessibleVariables();
         if (vars != null)
         {
            OJClass var_type;
            for (int i=0; i<vars.length; i++)
            {
               if ( !(vars[i].equals(lexp.toString())) && !(vars[i].equals(rexp.toString())) )
               {
                  var_type = getType(new Variable(vars[i]));
                  if (compatibleAssigType(type, var_type))
                  {
					 outputToFile(p, vars[i]);
                     // v.add(lexp.toFlattenString()+ " = " + vars[i]+";");
                  }
               }
            } // end for
         } // end if
      } // end if
   }

   private boolean compatibleAssigType(OJClass p, OJClass c)
   {
      OJClass clazz;
      String name;

      if (p == null || c == null) 
    	 return false;
      if (MutationSystem.isPrimitive(p)) 
    	 return false;
      if (p.getName().equals(c.getName())) 
    	 return true;
      
      while (true)
      {
         clazz = c.getSuperclass();
         if (clazz != null)
         {
            name = clazz.getName();
            if ( name.equals("java.lang.Object"))
            {
               return false;
            }
            else if ( name.equals(p.getName()) )
            {
               return true;
            }
            c = clazz;
         } 
         else
         {
            return false;
         }
      }
   }

   /**
    * Output PRV mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(AssignmentExpression original, String mutant)
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
		 PRV_Writer writer = new PRV_Writer( mutant_dir,out );
		 writer.setMutant(original,mutant);
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
}


class BindInfo
{
   String name;
   OJClass type;

   public BindInfo(String str, OJClass clazz)
   {
	  name = str;
	  type = clazz;
   }

   public OJClass getType()
   {
	  return type;
   }
}
