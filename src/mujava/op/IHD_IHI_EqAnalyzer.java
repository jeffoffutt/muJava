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
 * <p>Description: Examine if insertion or deletion of a certain 
 *    field change semantic of a class 
 * </p>
 * <b> Principle </b> : If <i> field_name </i> is used anywhere in a class, 
 *     there is high possibility of non-equivalent <br>
 *     In contrast, <i> field_name </i> not used any and 
 *     is not declared with public access level,
 *     it is an equivalent mutant.
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class IHD_IHI_EqAnalyzer extends mujava.op.util.Mutator
{
   /** name of a filed that we are to insert or delete for a mutant */
   String field_name = "";

   boolean eqFlag=true;

   public IHD_IHI_EqAnalyzer(Environment env , CompilationUnit comp_unit, String name ) 
   {
      super( env, comp_unit );
      field_name = name;
   }

   /** Return if an insertion or deletion of <i> field_name</i> field makes equivalent mutant */
   public boolean isEquivalent()
   {
      return eqFlag;
   }

   public void visit( Variable p ) throws ParseTreeException 
   {
      if (field_name.equals(p.toString()))
      {
         eqFlag = false;
      }
   }

   public void visit( FieldAccess p ) throws ParseTreeException 
   {
      Expression ref_exp = p.getReferenceExpr();
      if (ref_exp instanceof SelfAccess)
      {
         if (((SelfAccess)ref_exp).getAccessType() == SelfAccess.THIS)
         {
            if (field_name.equals(p.getName()))
            {
               eqFlag = false;
            }
         }
      }
   }

   /** If parameter use the same name with a field, the field is hidden within a method or a constructor
    *  where the parameter is used.  */
   private boolean hiddenByParameter(ParameterList plist)
   {
      int num = plist.size();
      if (num != 0)
      {
         for (int i=0; i<num; i++)
         {
            Parameter par = plist.get(i);
            String name = par.getVariable();
            if (field_name.equals(name)) 
               return true;
         }
      }
      return false;
   }

   public void visit(MethodDeclaration p) throws ParseTreeException 
   {
	  if (p.getName().equals("main")) 
		 return;

      // If a parameter use the same name with 'field_name' field.
      // 'field_name' field is hidden insider the 'p' method by scoping rule.
      if (!hiddenByParameter(p.getParameters()))
      {
         super.visit(p);
      }
   }

   public void visit(ConstructorDeclaration p) throws ParseTreeException 
   {
      // If a parameter use the same name with 'field_name' field.
      // 'field_name' field is hidden insider the 'p' method by scoping rule.
      if (!hiddenByParameter(p.getParameters()))
      {
         super.visit(p);
      }
   }

    // ?????????????????????????????????????
    // -.-? I don't know why
   public void visit( AssignmentExpression p ) throws ParseTreeException 
   {
      Expression left = p.getLeft();
      left.accept(this);
      Expression right = p.getRight();
      Expression newp;
      newp = this.evaluateDown( p );
      right.accept( this );
      newp = this.evaluateUp( p );
      if (newp != p)  
    	 p.replace( newp );
   }
}
