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
 * <p>Output and log IHD mutants to files </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 


public class IHD_Writer extends MutantCodeWriter
{
   FieldDeclaration original = null;
   FieldDeclaration mutant = null;

   /**
    * Set original source code and mutated code
    * @param original
    * @param mutant
    */
   public void setMutant(FieldDeclaration original, FieldDeclaration mutant)
   {
      this.original = original;
      this.mutant = mutant;
   }

   public IHD_Writer( String file_name, PrintWriter out ) 
   {
      super(file_name, out);
   }

   public void visit( FieldDeclaration p ) throws ParseTreeException
   {
      if (!(isSameObject(p, original)))
      {
         super.visit(p);
      } 
      else
      {
         writeTab();
		 out.print("// ");

         /*ModifierList*/
         ModifierList modifs = p.getModifiers();
         if (modifs != null) 
         {
            modifs.accept( this );
            if (! modifs.isEmptyAsRegular())  
               out.print( " " );
         }

         /*TypeName*/
         TypeName ts = p.getTypeSpecifier();
         ts.accept(this);

         out.print(" ");

         /*Variable*/
         String variable = p.getVariable();
         out.print(variable);
 
         /*"=" VariableInitializer*/
         VariableInitializer initializer = p.getInitializer();
         if (initializer != null) 
         {
            out.print(" = ");
            initializer.accept(this);
         }
         /*";"*/
         out.print(";");

         // -------------------------
	     mutated_line = line_num;
	     writeLog(removeNewline(mutant.toString())+" is deleted.");
	     // -------------------------

         out.println(); line_num++;
      }
   }
}
