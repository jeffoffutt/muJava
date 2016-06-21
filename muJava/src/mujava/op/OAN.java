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
 * <p>Generate OAN (Arguments of overloading method call change) --
 *    change the number of the argument in method invocations,
 *    but only if there is an overloading method that can accept
 *    the new argument list
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class OAN extends mujava.op.util.Mutator
{
   Environment env = null;
   OverloadingHelper oM_helper = new OverloadingHelper();
   MethodCall method_call= null;

   public OAN(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env, comp_unit );
	  this.env = getEnvironment();
   }

   public OJClass bindedType(String name)
   {
	  Environment env = getEnvironment();
	  OJClass bindedtype = env.lookupBind( name );
	  return bindedtype;
   }

   private static final String getFirst( String qname ) 
   {
	  if (qname == null)  
		 return null;
	  
	  int dot = qname.indexOf( '.' );
	  if (dot == -1)  
		 return qname;
	
	  return qname.substring( 0, dot );
   }

   /**
    * Generate OAN mutants
    * @param p
    * @param self
    * @param target
    */ 
   public void generateMutant(MethodCall p, OJMethod self, OJMethod target)
   {
      int i, j;
      int num;
      int[][] compatibleIndex;

      compatibleIndex = oM_helper.genCompatibleLocations(self, target);
      ExpressionList argList;
      Expression arg_exp;
      String mutant = null;
      String args = null;
    
      if (compatibleIndex == null)
      {
         mutant = genMutantCode(p, null);
         outputToFile(p, mutant);
      }
      else
      {
         try
         {
            num = compatibleIndex.length;
            for (i=0; i<num; i++)
            {
               for (j=0; j<target.getParameterTypes().length; j++)
               {
                  argList = p.getArguments();
                  arg_exp = argList.get(compatibleIndex[i][j]);
                  
                  if (args == null) 
                	 args = arg_exp.toString();
                  else 
                	 args = args + arg_exp.toString();
                  
                  if (j != target.getParameterTypes().length-1)
                  {
                     args = args + ",";
                  }
               }
               mutant = genMutantCode(p, args);
               outputToFile(p, mutant);
               args = null;
            }
         } catch(Exception e)
         {
	        System.out.println("Error " + e);
         }
      }
   }

   /**
    * Prepare a string of method call with the number of arguments changed
    * @param p
    * @param args
    * @return mutated line
    */
   public String genMutantCode(MethodCall p, String args)
   {
      String mutant = p.toString();
      int index = mutant.lastIndexOf("(");
      mutant = mutant.substring(0, index);
      
      if (args == null)
      {
         mutant = mutant + "()";
      }
      else
      {
         mutant = mutant + "(" + args + ")";
      }
   
      return mutant;
   }

   /**
    * Retrieve the methods of the same number of arguments 
    * @param c
    * @param name
    * @return list of methods
    */
   public OJMethod[] getMethods(OJClass c, String name)
   {
      int i;
      int num = 0;
      OJMethod[] m = c.getDeclaredMethods();
      boolean[] same = new boolean[m.length];
      
      for (i=0; i<m.length; i++)
      {
		 if (name.equals(m[i].getName()))
		 {
		    same[i] = true;
		    num++;
		 }
		 else
		 {
			same[i] = false;
		 }
      }

      OJMethod[] result = new OJMethod[num];
      num = 0;
      for (i=0; i<m.length; i++)
      {
		 if (same[i]) 
		 {
			result[num] = m[i];
			num++;
		 }
      }
      return result;
   }


   public int findIndex(OJMethod[] m, ExpressionList args)
   {
      OJClass type = null;
      OJClass[] pList = null;
      boolean find_flag = false;
	
      if (m != null)
      {
         try
         {
			for ( int i=0; i<m.length; i++)
			{
 			   pList = m[i].getParameterTypes();
			   if (pList.length == args.size())
			   {
	 			  find_flag = true;
				  for (int j=0; j<pList.length; j++)
				  {
					 Expression one_parameter = args.get(j);
				 	 type = getType(one_parameter);

                     // We did not consider polymorphism,
                     // Only consider exact type.
					 if ( !(type.equals(pList[j])) )
					 {
						find_flag = false;
						break;
				 	 }
				  }
				  if (find_flag)
				  {
				 	 return i;
				  }
			   }
		    }
		 } catch (Exception e)
		 {
		    System.err.print(" -_-  OAN Error  " + e);
		 }
	  }
      return -1;
   }


   /**
    * If the number of arguments is 0 or 1, do not generate mutant.
    * Otherwise, determine if the method is an overloading method that
    * can accept the new list of argument, generate OAN mutants.
    */
   public void visit( MethodCall p ) throws ParseTreeException 
   {
      int i;
      Expression ref = p.getReferenceExpr();
      String method_name = p.getName();
      OJClass bindedC = null;

      // If the number of arguments is 0 or 1, it is needless
      if (p.getArguments().size() < 2) 
    	 return;

      if (ref == null)
      {
         Environment env = getEnvironment();
         bindedC = env.lookupClass(env.currentClassName());
      } 
      else
      {
         String first = getFirst(ref.toString());
         bindedC = bindedType(first);
      }

      if (bindedC != null)
      {
         // find methods whose method name is "method_name"
         OJMethod[] oM = getMethods(bindedC, method_name);
         // if overloading method exist
         if (oM.length > 1)
         {
            // find index of p among the "method_name" methods
            int index = findIndex(oM, p.getArguments());

            if (index >= 0)
            {
               for ( i=0; i<oM.length ; i++ )
               {
                  if ( i != index && oM_helper.sameReturnType(oM[index], oM[i]) &&
                       oM_helper.compatibleParameter(oM[index], oM[i]) )
                  {
                     generateMutant(p, oM[index], oM[i]);
                  }
               }
            }
         }
      }
   }

   /**
    * Output OAN mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(MethodCall original, String mutant)
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
         OAN_Writer writer = new OAN_Writer( mutant_dir, out );
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
}
