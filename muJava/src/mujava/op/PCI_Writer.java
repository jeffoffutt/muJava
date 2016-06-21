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
 * <p>Output and log PCI mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class PCI_Writer extends MutantCodeWriter
{
   Variable original = null;
   String type_name = null;

   /**
    * Set original source code and mutated code
    * @param original
    * @param name
    */
   public void setMutant(Variable original, String name)
   {
      this.original = original;
      this.type_name = name;
   }

   public PCI_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name,out);
   }

   public void visit( Variable p ) throws ParseTreeException
   {
      if (isSameObject(p, original))
      {
         String str = "((" + type_name + ")" + p.toString() + ")";
         out.print(str);
         // -------------------------------------------------------------
         mutated_line = line_num;
         writeLog(removeNewline(p.toString() + " => " + str));
         // -------------------------------------------------------------
      }
      else
      {
         super.visit(p);
      }
   }
}
