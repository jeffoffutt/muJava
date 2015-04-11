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
 * <p>Output and log JDC mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class JDC_Writer extends MutantCodeWriter
{
   ConstructorDeclaration mutant = null;

   /**
    * Set mutated code
    * @param mutant
    */
   public void setMutant(ConstructorDeclaration mutant)
   {
      this.mutant = mutant;
   }

   public JDC_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name, out);
   }

   public void visit( ConstructorDeclaration p ) throws ParseTreeException
   {
      if (!(isSameObject(p, mutant)))
      {
	     super.visit(p);
      }
      else
      {
         mutated_line = line_num;
         //call JDC.class_name instread of super.class_name
	     String log_str = p.getModifiers().toString() + " "
	                 + JDC.class_name + "(" + p.getParameters().toString() + ")";
	     
	     writeLog(removeNewline(log_str) + " is deleted");

	     writeTab();
	     out.println("// " + log_str + " { ... } ");
	     line_num++;
      }
   }
}