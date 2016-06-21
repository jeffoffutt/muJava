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
 * <p>Output and log AORU mutants to files </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class AORU_Writer extends TraditionalMutantCodeWriter
{
   UnaryExpression unary_original;
   UnaryExpression unary_mutant;

   public AORU_Writer( String file_name, PrintWriter out ) 
   {
      super(file_name, out);
   }

   /**
    * Set original source code and mutated code
    * @param exp1
    * @param exp2
    */
   public void setMutant(UnaryExpression exp1, UnaryExpression exp2)
   {
      unary_original = exp1;
      unary_mutant = exp2;
   }

   /**
    * Log mutated code
    */
   public void visit( UnaryExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, unary_original))
      {
         super.visit(unary_mutant);
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toString() + " => " + unary_mutant.toString();
         writeLog(removeNewline(log_str));
         // -------------------------------------------------------------
      }
      else
      {
         super.visit(p);
      }
   }
}
