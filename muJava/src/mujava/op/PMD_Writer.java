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
import openjava.ptree.*;
import mujava.op.util.MutantCodeWriter;

/**
 * <p>Output and log PMD mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class PMD_Writer extends MutantCodeWriter
{
   FieldDeclaration original_field = null;
   FieldDeclaration mutant_field = null;

   VariableDeclaration original_var = null;
   VariableDeclaration mutant_var = null;

   /**
    * Set original source code and mutated code
    * @param original
    * @param mutant
    */
   public void setMutant(FieldDeclaration original, FieldDeclaration mutant)
   {
      this.original_field = original;
      this.mutant_field = mutant;
   }

   public void setMutant(VariableDeclaration original, VariableDeclaration mutant)
   {
      this.original_var = original;
      this.mutant_var = mutant;
   }

   public PMD_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name, out);
   }

   /**
    * Log mutated line
    */
   public void visit( FieldDeclaration p ) throws ParseTreeException
   {
      if (isSameObject(p, original_field))
      {
         // -------------------------------------------------------------
         mutated_line = line_num;
         visit(mutant_field);
         writeLog(removeNewline(original_field.toString() + " => " + mutant_field.toString()));
         // -------------------------------------------------------------
      }
      else
      {
         super.visit(p);
      }
   }

   /**
    * Log mutated line
    */
   public void visit( VariableDeclaration p ) throws ParseTreeException
   {
      if (isSameObject(p, original_var))
      {
		 // -------------------------------------------------------------
		 mutated_line = line_num;
		 visit(mutant_var);
		 writeLog(removeNewline(original_var.toString() + " => " + mutant_var.toString()));
         // -------------------------------------------------------------
      }
      else
      {
		 super.visit(p);
      }
   }
}
