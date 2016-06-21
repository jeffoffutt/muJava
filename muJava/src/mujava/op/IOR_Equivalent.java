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

import openjava.mop.*;
import openjava.ptree.*;
/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class IOR_Equivalent  extends mujava.op.util.Mutator
{
   String declaredClassName = null;
   String target_method_name = null;
   TypeName target_return_type = null;
   ParameterList target_parList = null;
   boolean isEquivalent = true;

   public IOR_Equivalent(FileEnvironment file_env, CompilationUnit comp_unit)
   {
	  super( file_env, comp_unit );
      isEquivalent = true;
   }

   public void setInformation(String class_name, MethodDeclaration mutant)
   {
      this.declaredClassName = class_name;
      this.target_method_name = mutant.getName();
      this.target_return_type = mutant.getReturnType();
      this.target_parList = mutant.getParameters();
   }

   public void visit( ClassDeclaration p ) throws ParseTreeException 
   {
      if (p.getName().equals(declaredClassName))
      {
         super.visit(p);
      }
   }

   public void visit( MethodCall p ) throws ParseTreeException 
   {
      if ( !(p.getName().equals(target_method_name)) ) 
    	 return;
    
      ExpressionList argList = p.getArguments();
      if ((argList == null) || (argList.size() == 0))
      {
         if ( (target_parList == null) || (target_parList.size() == 0))
         { 
        	isEquivalent = false; 
         }
      }
      else
      {
         if (target_parList.size() == 0) 
        	return;
      }
      
      int argNum = argList.size();
      for (int i=0; i<argNum; i++)
      {
         Expression exp = argList.get(i);
         OJClass clazz = getType(exp);
         if (!(clazz.getName().equals(target_parList.get(i).getTypeSpecifier().getName()))) 
        	return;
      }
      
      isEquivalent = false;
   }

   public boolean isEquivalent()
   {
      return isEquivalent;
  }
}
