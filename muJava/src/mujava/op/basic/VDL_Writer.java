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
 * <p>Output and log VDL mutants to files </p>
 * @author Lin Deng
 * @version 1.0
  */

public class VDL_Writer extends TraditionalMutantCodeWriter
{
   Expression original;
   Expression mutant;

   public VDL_Writer( String file_name, PrintWriter out ) 
   {
      super(file_name, out);
   }

   /**
    * Set original source code and mutated code
    * @param exp1 - original 
    * @param exp2 - mutant
    */
   public void setMutant(BinaryExpression exp1, Expression exp2)
   {
      original = exp1;
      mutant = exp2;
   }

   /**
    * Log mutated line 
    */
   public void visit( BinaryExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, original))
      {
         super.visit(mutant);
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toString() + " => " + mutant.toString();
         writeLog(removeNewline(log_str));
         // -------------------------------------------------------------
      } 
      else
      {
         super.visit(p);
      }
   }
   
   public void visit( UnaryExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, original))
      {
         super.visit(mutant);
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toString() + " => " + mutant.toString();
         writeLog(removeNewline(log_str));
         // -------------------------------------------------------------
      } 
      else
      {
         super.visit(p);
      }
   }
   
   public void visit( AssignmentExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, original))
      {
         super.visit(mutant);
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toString() + " => " + mutant.toString();
         writeLog(removeNewline(log_str));
         // -------------------------------------------------------------
      } 
      else
      {
         super.visit(p);
      }
   }

public void setMutant(UnaryExpression exp1, Expression exp2) {
    original = exp1;
    mutant = exp2;
	
}

public void setMutant(AssignmentExpression exp1, Expression exp2) {
    original = exp1;
    mutant = exp2;
	
}
}
