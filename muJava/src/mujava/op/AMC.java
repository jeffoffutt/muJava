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
 * <p>Generate AMC (Access modifier change) mutants -- change 
 *    the access level for instance variables and methods 
 *    to other access levels. The purpose is to guide testers 
 *    to generate test cases that ensure that accessibility 
 *    is correct. 
 * </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class AMC extends mujava.op.util.Mutator
{
   public AMC(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit)
   {
  	  super( file_env, comp_unit );
   }

   private void changeModifier(ModifierList original, int del_mod, int insert_mod)
   {
      ModifierList mutant;
      mutant = new ModifierList(ModifierList.EMPTY);
	  mutant = (ModifierList)original.makeRecursiveCopy();
	  	  
	  int mod = mutant.getRegular();
	  
	  if (del_mod >= 0)
	  {
         mod &= (~del_mod); 
         mutant.setRegular(mod);
	  }
	  if (insert_mod >= 0)
	  {
		 mutant.add(insert_mod);
	  }	  
	  outputToFile(original, mutant);
	  mutant = null;
   }
    
   /**
    * Generate AMC mutants by altering the access level for each
    * instance variable and method to other access levels (PRIVATE, 
    * PROTECTED, PUBLIC, PACKAGE or default)
    * @param mod
    */
   public void genMutants(ModifierList mod)
   {
      if (mod.contains(ModifierList.PRIVATE))
      {
         changeModifier(mod,ModifierList.PRIVATE, -1);
    	 changeModifier(mod,ModifierList.PRIVATE, ModifierList.PROTECTED);
    	 changeModifier(mod,ModifierList.PRIVATE, ModifierList.PUBLIC);
    	
      } 
      else if (mod.contains(ModifierList.PROTECTED))
      {
         changeModifier(mod,ModifierList.PROTECTED, -1);
      	 changeModifier(mod,ModifierList.PROTECTED, ModifierList.PRIVATE);
    	 changeModifier(mod,ModifierList.PROTECTED, ModifierList.PUBLIC);
      }
      else if (mod.contains(ModifierList.PUBLIC))
      {
    	 changeModifier(mod,ModifierList.PUBLIC, -1);
    	 changeModifier(mod,ModifierList.PUBLIC, ModifierList.PRIVATE);
    	 changeModifier(mod,ModifierList.PUBLIC, ModifierList.PROTECTED);
      }
      else
      { // Friendly
    	 changeModifier(mod, -1, ModifierList.PRIVATE);
    	 changeModifier(mod, -1, ModifierList.PROTECTED);
    	 changeModifier(mod, -1, ModifierList.PUBLIC);
      }
   }

   public void visit( MethodDeclaration p ) throws ParseTreeException
   {
      genMutants(p.getModifiers());
   }


   public void visit( FieldDeclaration p ) throws ParseTreeException
   {
      genMutants(p.getModifiers());
   }

   /**
    * Output AMC mutants to files
    * @param original
    * @param mutant
    */
   public void outputToFile(ModifierList original, ModifierList mutant)
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
         AMC_Writer writer = new AMC_Writer( mutant_dir, out );
         writer.setMutant(original, mutant);
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
}
