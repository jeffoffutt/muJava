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
 * <p>Output and log ASRS mutants to files </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class ASRS_Writer extends TraditionalMutantCodeWriter
{
   AssignmentExpression assign_original;
   AssignmentExpression assign_mutant;

   public ASRS_Writer( String file_name, PrintWriter out ) 
   {
      super(file_name,out);
   }

   /**
    * Set original source code and mutated code
    * @param exp1
    * @param exp2
    */
   public void setMutant(AssignmentExpression exp1, AssignmentExpression exp2)
   {
      assign_original = exp1;
      assign_mutant = exp2;
   }

   /**
    * Log mutated line
    */
   public void visit( AssignmentExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, assign_original))
      {
         super.visit(assign_mutant);
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toString() + " => " + assign_mutant.toString();
         writeLog(removeNewline(log_str));
         // -------------------------------------------------------------
      }
      else
      {
         super.visit(p);
      }
   }
}
