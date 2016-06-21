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
import mujava.MutationSystem;
import mujava.util.InheritanceINFO;
import java.util.*;

/**
 * <p>Generate PCC (Cast type change) mutants --
 *    change the type that a variable is to be cast into 
 * </p>
 * <p><i>Example</i>: <br/>
 *    ((Parent)ref).toString(); is mutated to ((Child)ref).toString();
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class PCC extends mujava.op.util.TypeCastMutator
{
   String afterCastType = "";
   String beforeCastType = "";

   public PCC(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env, comp_unit );
   }

   void generateUpMutants(CastExpression p, InheritanceINFO info)
   {
      InheritanceINFO temp = info.getParent();
      if (temp != null)
      {
   	     if (beforeCastType.equals(temp.getClassName())) 
   	    	return;
      
   	     if (hasHidingVariableOrOverridingMethod(temp.getClassName(), beforeCastType))
   	     {
            outputToFile(p, temp.getClassName());
         }
         generateUpMutants(p, temp);
      }
   }

   void generateDownMutants(CastExpression p, InheritanceINFO info)
   {
      Vector v = info.getChilds();
      for (int i=0; i<v.size(); i++)
      {
         InheritanceINFO temp = (InheritanceINFO)v.get(i);
         if (afterCastType.equals(temp.getClassName())) 
        	return;
      
         if (beforeCastType.equals(temp.getClassName())) 
        	return;
      
         if (hasHidingVariableOrOverridingMethod(temp.getClassName(), beforeCastType))
         {
            outputToFile(p, temp.getClassName());
         }
         generateDownMutants(p, temp);
      }
   }

   void generateUpMutants2(CastExpression p, InheritanceINFO info, String method_name, Class[] pars)
   {
      InheritanceINFO temp = info.getParent();
      if (temp != null)
      {
         if (beforeCastType.equals(temp.getClassName())) 
        	return;
      
         if (isNonAbstractOverridingMethodCall(temp.getClassName(), beforeCastType, method_name, pars))
         {
            outputToFile(p, temp.getClassName());
         }
         generateUpMutants(p, temp);
      }
   }

   void generateDownMutants2(CastExpression p, InheritanceINFO info, String method_name, Class[] pars)
   {
      Vector v = info.getChilds();
      for (int i=0; i<v.size(); i++)
      {
         InheritanceINFO temp = (InheritanceINFO)v.get(i);
         if (afterCastType.equals(temp.getClassName())) 
        	return;
      
         if (beforeCastType.equals(temp.getClassName())) 
        	return;
       
         if (isNonAbstractOverridingMethodCall(temp.getClassName(), beforeCastType, method_name, pars))
         {
            outputToFile(p, temp.getClassName());
         }
         generateDownMutants(p, temp);
      }
   }

   void generateMutants(CastExpression p, InheritanceINFO info)
   {
      if (hasHidingVariableOrOverridingMethod(info.getClassName(), beforeCastType))
      {
         outputToFile(p, info.getClassName());
      }
      generateUpMutants(p, info);
      generateDownMutants(p, info);
   }

   public void visit( CastExpression p )  throws ParseTreeException
   {
      afterCastType = p.getTypeSpecifier().getName();
      beforeCastType = getType(p.getExpression()).getName();
      
      if (afterCastType.equals(beforeCastType)) 
    	 return;

      InheritanceINFO inf = MutationSystem.getInheritanceInfo(beforeCastType);
      if (inf != null) 
      {
         if (currentMethodCall != null)
         {
            try
            {
               String method_name = currentMethodCall.getName();
               Class[] par_type = getParameterTypes(currentMethodCall);
   
               generateUpMutants2(p, inf, method_name, par_type);
               generateDownMutants2(p, inf, method_name, par_type);
            } catch (Exception e)
            {
               // do nothing
            }
         }
         else
         {
            generateUpMutants(p, inf);
            generateDownMutants(p, inf);
         }
      }
   }

   public void visit( MethodCall p ) throws ParseTreeException 
   {
      Expression newp = this.evaluateDown( p );
	  if (newp != p) 
	  {
	     p.replace( newp );
	     return;
	  }
      
	  Expression ref = p.getReferenceExpr();
      if (ref != null)
      {
         currentMethodCall = p;
         ref.accept(this);
         currentMethodCall = null;
      }
    
      ExpressionList list = p.getArguments();
      if (list != null) 
    	 list.accept(this);
   }

   /**
    * Write PCC mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(CastExpression original, String mutant)
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
		 PCC_Writer writer = new PCC_Writer( mutant_dir, out );
		 writer.setMutant(original, mutant);
		 comp_unit.accept( writer );
		 out.flush();  out.close();
      } catch ( IOException e ) {
		 System.err.println( "fails to create " + f_name );
      } catch ( ParseTreeException e ) {
		 System.err.println( "errors during printing " + f_name );
		 e.printStackTrace();
      }
   }
}
