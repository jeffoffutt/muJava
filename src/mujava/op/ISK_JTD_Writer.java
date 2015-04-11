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
 * <p>Output and log ISD, ISI, and JTD mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class ISK_JTD_Writer extends MutantCodeWriter
{
   FieldAccess mutant_field = null;
   FieldAccess original_field = null;

   /**
    * Set original source code and mutated code
    * @param original
    * @param mutant
    */
   public void setMutant(FieldAccess original, FieldAccess mutant)
   {
      this.original_field = original;
      this.mutant_field = mutant;
   }

   MethodCall mutant_method = null;
   MethodCall original_method = null;

   /**
    * Set original source code and mutated code
    * @param original
    * @param mutant
    */
   public void setMutant(MethodCall original, MethodCall mutant)
   {
      this.original_method = original;
      this.mutant_method = mutant;
   }

   public ISK_JTD_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name,out);
   }

   /**
    * Log mutated line
    */
   public void visit( FieldAccess p ) throws ParseTreeException
   {
      if (!(isSameObject(p, original_field)))
      {
		 super.visit(p);
      }
      else
      {
		 // -----------------------------------------------------------
  		 mutated_line = line_num;
		 String log_str = original_field.toString()+ " => " + mutant_field.toString();
		 writeLog(removeNewline(log_str));

		 // -------------------------------------------------------------
		 visit(mutant_field);
      }
   }

   /**
    * Log mutated line
    */
   public void visit( MethodCall p ) throws ParseTreeException
   {
      if (!(isSameObject(p, original_method)))
      {
		 super.visit(p);
      }
      else
      {
		 // --------------------------------------------------------------
		 mutated_line = line_num;
		 String log_str = original_method.toString()+ " => " + mutant_method.toString();
		 writeLog(removeNewline(log_str));
		 // --------------------------------------------------------------
		 visit(mutant_method);
      }
   }
}

