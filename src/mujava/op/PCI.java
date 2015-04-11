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


/**
 * <p>Generate PCI (Type cast operator insertion) mutants --
 *    change the actual type of an object reference to the parent
 *    or child of the original declared type 
 * </p>
 * <p><i>Example</i>: <br/>
 *    Child cRef; Parent pRef = cRef; pRef.toString(); is mutated to <br/>
 *    Child cRef; Parent pRef = cRef; ((Child)pRef).toString(); 
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class PCI extends mujava.op.util.TypeCastMutator
{
   String beforeCastType = "";
   boolean isNonEQ = false;

   public PCI(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env, comp_unit );
   }

   void generateUpMutant(Variable p, InheritanceINFO inf)
   {
      if (inf.getParent() != null)
      {
         String afterCastType = inf.getParent().getClassName();
         if (afterCastType.equals(beforeCastType)) 
        	return;
         
         if (hasHidingVariableOrOverridingMethod(beforeCastType, afterCastType))
         {
            outputToFile( p, afterCastType);
         }
         generateUpMutant(p, inf.getParent());
      }
   }

   // For method call
   void generateUpMutant2(Variable p, InheritanceINFO inf, String method_name, Class[] pars)
   {
      if (inf.getParent() != null)
      {
         String afterCastType = inf.getParent().getClassName();
         if (afterCastType.equals(beforeCastType)) 
        	return;
      
         if (isNonAbstractOverridingMethodCall(beforeCastType, afterCastType, method_name, pars))
         {
            outputToFile( p, afterCastType);
         }
         generateUpMutant(p, inf.getParent());
      } 
   }

   void generateDownMutant(Variable p, InheritanceINFO inf)
   {
      if (inf.getChilds().size() > 0)
      {
         for (int i=0; i<inf.getChilds().size(); i++)
         {
            String afterCastType = ((InheritanceINFO)inf.getChilds().get(i)).getClassName();
            if (afterCastType.equals(beforeCastType)) 
               return;
            
            if (hasHidingVariableOrOverridingMethod(beforeCastType, afterCastType))
            {
               outputToFile( p, afterCastType);
            }
            generateDownMutant(p, (InheritanceINFO)inf.getChilds().get(i));
         } 
      }
   }

   void generateDownMutant2(Variable p, InheritanceINFO inf, String method_name, Class[] pars)
   {
      if (inf.getChilds().size() > 0)
      {
         for (int i=0; i<inf.getChilds().size(); i++)
         {
            String afterCastType = ((InheritanceINFO)inf.getChilds().get(i)).getClassName();
            if (afterCastType.equals(beforeCastType)) 
               return;
            
            if (isNonAbstractOverridingMethodCall(beforeCastType, afterCastType, method_name, pars))
            {
               outputToFile( p, afterCastType);
            }
            generateDownMutant(p, (InheritanceINFO)inf.getChilds().get(i));
         }
      }
   }
  
   // ���� non-equivalent �Ѱ�..
   // [1] assignment �� �����ʿ� ��������
   // [2] method call������..

   public void visit( AssignmentExpression p ) throws ParseTreeException 
   {
      Expression left = p.getLeft();
	  if (! (left instanceof FieldAccess)) 
	  {
	     super.visit( p );
		 return;
      }
	 
	  FieldAccess fldac = (FieldAccess) left;
	  Expression refexpr = fldac.getReferenceExpr();
 	  TypeName reftype = fldac.getReferenceType();
	  Expression value = p.getRight();
	  /* custom version of  visit() skipping the field */
	  Expression newp;
	  newp = this.evaluateDown( p );
	  if (newp != p) 
	  {
		 p.replace( newp );
		 newp.accept( this );
		 return;
	  }

	  if (refexpr != null) 
	  {
	     refexpr.accept( this );
	  }
	  else if (reftype != null) 
	  {
	     reftype.accept( this );
	  }
      
	  isNonEQ = true;
 	  value.accept( this );
      isNonEQ = false;

	  newp = this.evaluateUp( p );
      if (newp != p)  
		 p.replace( newp );
   }

   public void visit( Variable p )  throws ParseTreeException
   {
      if (isNonEQ)
      {
         OJClass c = getType(p);
         InheritanceINFO inf = MutationSystem.getInheritanceInfo(c.getName());
        
         if (inf == null) 
        	return;
      
         beforeCastType = (getType(p)).getName();
         
         if (currentMethodCall == null)
         {
            generateUpMutant(p,inf);
            generateDownMutant(p,inf);
         }
         else
         {
            try
            {
               String method_name = currentMethodCall.getName();
               Class[] par_type = getParameterTypes(currentMethodCall);
               generateUpMutant2(p, inf, method_name, par_type);
               generateDownMutant2(p, inf, method_name, par_type);
            } catch (Exception e)
            {
               // do nothing
            }
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
    
      isNonEQ = true;
      Expression ref = p.getReferenceExpr();
      if (ref != null)
      {
         currentMethodCall = p;
         ref.accept(this);
         currentMethodCall = null;
      }
      
      isNonEQ = false;
      ExpressionList list = p.getArguments();
      if (list != null) 
    	 list.accept(this);
   }

   /**
    * Write PCI mutants to files
    * @param original
    * @param type_name
    */
   public void outputToFile(Variable original, String type_name)
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
		 PCI_Writer writer = new PCI_Writer( mutant_dir, out );
		 writer.setMutant(original,type_name);
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
