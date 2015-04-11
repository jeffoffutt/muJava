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
 * <p>Generate PMD (Member variable declaration with parent class type) mutants ---
 *    change the declared type of an object reference to the parent of the original 
 *    declared type        
 * </p>
 * <p><i>Example</i>: let class A be the parent of class B --
 *       B b; b = new B(); is mutated to A b; b = new B();
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class PMD extends mujava.op.util.PolymorphicMutator
{
   public PMD(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
	  super( file_env, comp_unit );
   } 

   public void visit(VariableDeclaration p) throws ParseTreeException
   {
      this.evaluateDown(p);
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
   }

   /**
    * Generate PMD mutant and write output to file
    * @param p
    * @param parent
    */
   public void generateMutant(VariableDeclaration p, String parent)
   {
      String declared_type = p.getTypeSpecifier().getName();
      
      if (hasHidingVariable(declared_type, parent))
      {
         VariableDeclaration mutant = (VariableDeclaration)p.makeRecursiveCopy();
         mutant.setTypeSpecifier(new TypeName(parent));
         outputToFile(p, mutant);
      }
   }

   /**
    * Generate PMD mutant and write output to file
    * @param p
    * @param parent
    */
   public void generateMutant(VariableDeclaration p, InheritanceINFO parent)
   {
      String declared_type = p.getTypeSpecifier().getName();
      String parent_type = parent.getClassName();
      
      if (hasHidingVariable(declared_type, parent_type))
      {
         VariableDeclaration mutant = (VariableDeclaration)p.makeRecursiveCopy();
         mutant.setTypeSpecifier(new TypeName(parent_type));
         outputToFile(p, mutant);
      }
      
      if (parent.getParent() != null)
      {
         generateMutant(p,parent.getParent());
      }
   }

   /**
    * Generate PMD mutant and write output to file
    * @param p
    * @param parent
    */
   public void generateMutant(FieldDeclaration p, String parent)
   {
      String declared_type = p.getTypeSpecifier().getName();
    
      if (hasHidingVariable(declared_type, parent))
      {
         FieldDeclaration mutant = (FieldDeclaration)p.makeRecursiveCopy();
         mutant.setTypeSpecifier(new TypeName(parent));
         outputToFile(p, mutant);
      }
   }

   /**
    * Generate PMD mutant and write output to file
    * @param p
    * @param parent
    */
   public void generateMutant(FieldDeclaration p, InheritanceINFO parent)
   {
      String declared_type = p.getTypeSpecifier().getName();
      String parent_type = parent.getClassName();
      
      if (hasHidingVariable(declared_type, parent_type))
      {
         FieldDeclaration mutant = (FieldDeclaration)p.makeRecursiveCopy();
         mutant.setTypeSpecifier(new TypeName(parent.getClassName()));
         outputToFile(p, mutant);
      }
      
      if (parent.getParent() != null)
      {
         generateMutant(p, parent.getParent());
      }
   }

   public void visit( FieldDeclaration p ) throws ParseTreeException 
   {
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
               generateMutant(p,super_class.getName());
         } catch(Exception e)
         {
            return;
         }
      }
   }

   /**
    * Output PMD mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(FieldDeclaration original, FieldDeclaration mutant)
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
		 PMD_Writer writer = new PMD_Writer( mutant_dir,out );
		 writer.setMutant(original,mutant);
		 //writer.setDebugLevel( 0 );
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

   /**
    * Output PMD mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(VariableDeclaration original, VariableDeclaration mutant)
   {
      if (comp_unit == null) return;

      String f_name;
      num++;
      f_name = getSourceName(this);
      String mutant_dir = getMuantID();

      try 
      {
		 PrintWriter out = getPrintWriter(f_name);
		 PMD_Writer writer = new PMD_Writer( mutant_dir,out );
		 writer.setMutant(original,mutant);
		 //writer.setDebugLevel( 0 );
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
