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

import mujava.op.util.MutantCodeWriter;
import openjava.ptree.*;
import java.io.*;

/**
 * <p>Output and log UOI mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class UOI_Writer extends MutantCodeWriter
{
   Variable original;
   MethodCall original_call;
   int mutant_op;

   private static final String unary_op_string[] = 
                            { "++", "--", "++", "--", "~", "!", "+", "-" };

   public UOI_Writer( String file_name, PrintWriter out ) 
   {
      super(file_name, out);
   }

   /**
    * Set original source code and mutated code (variable)
    * @param exp
    * @param op
    */
   public void setMutant(Variable exp, int op)
   {
      original = exp;
      mutant_op = op;
   }

   /**
    * Set original source code and mutated code (method call)
    * @param exp
    * @param op
    */
   public void setMutant(MethodCall exp, int op)
   {
      original_call = exp;
      mutant_op = op;
   }
 
   /**
    * Log mutated line
    */
   public void visit( Variable p ) throws ParseTreeException
   {
      if (isSameObject(p, original))
      {
         String mutated_str = "";
         if (mutant_op == UnaryExpression.PRE_DECREMENT || mutant_op == UnaryExpression.PRE_INCREMENT)
         {
            mutated_str = "(" + unary_op_string[mutant_op] + p.toString() + ")";
         }
         else
         {
            mutated_str = "(" + p.toString() + unary_op_string[mutant_op] + ")";
         }
         out.print(mutated_str);
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toString()+ " =>  " + mutated_str;
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
   public void visit( MethodCall p ) throws ParseTreeException
   {
      if (isSameObject(p, original_call))
      {
         String mutated_str = "(" + unary_op_string[mutant_op] + p.toString() + ")";
         out.print(mutated_str);
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toString() + " =>  " + mutated_str;
         writeLog(removeNewline(log_str));
         // -------------------------------------------------------------
      }
      else
      {
         super.visit(p);
      }
   }
}
