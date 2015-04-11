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
 * <p>Output and log JID mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class JID_Writer extends MutantCodeWriter
{
   FieldDeclaration mutant = null;

   /**
    * Set mutated code
    * @param mutant
    */
   public void setMutant(FieldDeclaration mutant)
   {
      this.mutant = mutant;
   }

   public JID_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name, out);
   }

   public void visit( FieldDeclaration p ) throws ParseTreeException
   {
      writeTab();
      
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

	  if (isSameObject(p, mutant))
	  {
         mutated_line = line_num;
         String temp = mutant.getModifiers().toString()
		               + " " + mutant.getTypeSpecifier().toString()
		               + " " + mutant.getVariable();
         String mutant_str = temp + p.getInitializer().toString();
	     String log_str = temp+";";
	     writeLog(removeNewline(mutant_str + " => " + log_str));

         // -------------------------------------------------------------
  	  }
	  else
	  {
         /*"=" VariableInitializer*/
         VariableInitializer initializer = p.getInitializer();
         if (initializer != null) 
         {
            out.print(" = ");
            initializer.accept(this);
         }
	  }
      /*";"*/
      out.print(";");

      out.println(); line_num++;
   }
}
