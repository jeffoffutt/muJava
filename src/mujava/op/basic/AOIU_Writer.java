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
package mujava.op.basic;

import mujava.op.util.TraditionalMutantCodeWriter;
import openjava.ptree.*;
import java.io.*;

/**
 * <p>Output and log AOIU mutants to files </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class AOIU_Writer extends TraditionalMutantCodeWriter
{
   Variable original_var;
   FieldAccess original_field;

   public AOIU_Writer( String file_name, PrintWriter out ) 
   {
      super(file_name,out);
   }

   /**
    * Set original source code
    * @param exp1
    */
   public void setMutant(Variable exp1)
   {
      original_var = exp1;
   }

   /**
    * Set original source code
    * @param exp1
    */
   public void setMutant(FieldAccess exp1)
   {
      original_field = exp1;
   }

   /**
    * Log mutated line
    */
   public void visit( Variable p ) throws ParseTreeException
   {
      if (isSameObject(p, original_var))
      {
         out.print("-" + p.toString());
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toString() + " => " + "-" + p.toString();
         writeLog(removeNewline(log_str));
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
   public void visit( FieldAccess p ) throws ParseTreeException
   {
      if (isSameObject(p, original_field))
      {
         out.print("-"+p.toString());
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toString() + " => " + "-"+p.toString();
         writeLog(removeNewline(log_str));
         // -------------------------------------------------------------
      }
      else
      {
         super.visit(p);
      }
   }
}
