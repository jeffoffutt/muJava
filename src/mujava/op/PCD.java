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
 * <p>Generate PCD (Type cast operator deletion) mutants --
 *    deletes type casting operator
 * </p>
 * <p><i>Example</i>: <br/>
 *   Child cRef; Parent pRef = cRef; ((Child)pRef).toString();  is mutated to <br> 
 *   Child cRef; Parent pRef = cRef; pRef.toString();   
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class PCD extends mujava.op.util.TypeCastMutator
{
   public PCD(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env, comp_unit );
   } 

   public void visit( CastExpression p )  throws ParseTreeException
   {
      String afterCastType = p.getTypeSpecifier().getName();
      String beforeCastType = getType(p.getExpression()).getName();
      
      if ((afterCastType == null) || (beforeCastType == null)) 
    	 return;
      
      if (afterCastType.equals(beforeCastType)) 
    	 return;
     
      if (currentMethodCall == null)
      {
         if (hasHidingVariableOrOverridingMethod(afterCastType, beforeCastType))
         {
            outputToFile(p);
         }
      } 
      else
      {
         try 
         {
            String method_name = currentMethodCall.getName();
            Class[] par_type = getParameterTypes(currentMethodCall);
            
            if ( isNonAbstractOverridingMethodCall(afterCastType, beforeCastType, method_name, par_type))
            {
               outputToFile(p);
            }
         } catch (Exception e)
         {
            // e.printStackTrace();
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
    * Write PCD mutants to files
    * @param original
    */
   public void outputToFile(CastExpression original)
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
		 PCD_Writer writer = new PCD_Writer( mutant_dir, out );
		 writer.setMutant(original);
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
