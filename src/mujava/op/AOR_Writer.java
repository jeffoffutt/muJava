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
 * <p>Output and log AOR mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class AOR_Writer extends MutantCodeWriter
{
   BinaryExpression original;
   int mutant_op;
 
   public AOR_Writer( String file_name, PrintWriter out ) 
   {
      super(file_name, out);
   }

   /**
    * Set original source code and mutated code
    * @param exp
    * @param op
    */
   public void setMutant(BinaryExpression exp, int op)
   {
      original = exp;
      mutant_op = op;
   }

   /**
    * Log mutated line
    */
   public void visit( BinaryExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, original))
      {
    	 BinaryExpression mutant_exp;
    	 mutant_exp = (BinaryExpression)original.makeRecursiveCopy();
    	 mutant_exp.setOperator(mutant_op);
    	 super.visit(mutant_exp);

     	 String operator = mutant_exp.operatorString();
    	 out.print( " " + operator + " " );
	     // -----------------------------------------------------------
	     mutated_line = line_num;
	     String log_str = p.operatorString()+ " => " + operator;
	     writeLog(removeNewline(log_str));
	     // -------------------------------------------------------------

     	 mutant_exp = null;
      }
      else
      {
         super.visit(p);
      }
   }
}
