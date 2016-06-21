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
 * <p>Generate PPD (Parameter variable declaration with child class type) mutants --
 *    change the declared type of a parameter object reference to be that of the 
 *    parent of its original declared type. 
 * </p>
 * <p><i>Note</i>: The PPD operator is the same as the PMD, except that 
 *    it operates on parameters rather than instance and local variables.
 * </p>
 * <p><i>Example</i>: let class A be the parent of class B -- 
 *    boolean equals(B o) {...} is mutated to boolean equals (A o) {...} 
 * </p>  
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class PPD extends mujava.op.util.PolymorphicMutator
{
   MethodDeclaration currentMethod = null;

   public PPD(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env, comp_unit );
   }

   public void visit(MethodDeclaration p) throws ParseTreeException 
   {
	  if (p.getName().equals("main")) 
		 return;
	  
      currentMethod = p;
      if (p.getBody() != null)
         super.visit(p);
   }

   public void visit( ConstructorDeclaration p ) throws ParseTreeException
   {
      // do nothing
   }

   public void visit( Parameter p ) throws ParseTreeException 
   {
      this.evaluateDown( p );
      if (MutationSystem.isPrimitive(getType(p.getTypeSpecifier()))) 
    	 return;
      
      String original_class = p.getTypeSpecifier().getName();
      InheritanceINFO inf = MutationSystem.getInheritanceInfo(original_class);
      
      if (inf == null) 
    	 return;
    
      if (inf.getParent() != null)
      {
         generateMutant(p, inf.getParent());
      }
      else
      { 
         if (original_class.equals("java.lang.Object")) 
        	return;
         
         try 
         {
            Class super_class = Class.forName(original_class).getSuperclass();
            if (!((super_class == null) || (super_class.getName().equals("java.lang.Object"))))
               generateMutant(p, super_class.getName());
         } catch (Exception e)
         {
            return;
         }
      }
      this.evaluateUp( p );
   }

   /**
    * Generate PPD mutants
    * @param p
    * @param parent
    */
   public void generateMutant(Parameter p, String parent)
   {
      String declared_type = p.getTypeSpecifier().getName();
      if (hasHidingVariable(declared_type, parent))
      {
         Parameter mutant = (Parameter)p.makeRecursiveCopy();
         mutant.setTypeSpecifier(new TypeName(parent));
         outputToFile(p, mutant);
      }
   }

   /**
    * Generate PPD mutants 
    * @param p
    * @param parent
    */
   public void generateMutant(Parameter p, InheritanceINFO parent)
   {
      String declared_type = p.getTypeSpecifier().getName();
      String parent_type = parent.getClassName();
      if (hasHidingVariable(declared_type, parent_type))
      {
         Parameter mutant = (Parameter)p.makeRecursiveCopy();
         mutant.setTypeSpecifier(new TypeName(parent.getClassName()));
         outputToFile(p, mutant);
      }
    
      if (parent.getParent() != null)
      {
         generateMutant(p, parent.getParent());
      }
   }

   /**
    * Output PPD mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(Parameter original, Parameter mutant)
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
		 PPD_Writer writer = new PPD_Writer( mutant_dir, out );
		 writer.setMutant(currentMethod, original, mutant);
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
