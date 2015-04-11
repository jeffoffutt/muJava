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
 * <p>Output and log JTI mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class JTI_Writer  extends MutantCodeWriter
{
   Variable target = null;

   /**
    * Set mutated code
    * @param f
    */
   public void setMutant(Variable f)
   {
      target = f;
   }

   public JTI_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name, out);
   }

   public void visit( Variable p ) throws ParseTreeException
   {
      if (isSameObject(p, target))
      {
         out.print("this."+p.toString());
         // -------------------------------------------------------------
         mutated_line = line_num;
         writeLog(removeNewline(p.toString()+"  -->  this." + p.toString()));
         // -------------------------------------------------------------
      }
      else
      {
         super.visit(p);
      }
   }
}
