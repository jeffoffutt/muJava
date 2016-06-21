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
 * <p>Output and log PCD mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class PCD_Writer extends MutantCodeWriter
{
   CastExpression original = null;

   /**
    * Set original source code 
    * @param original
    */
   public void setMutant(CastExpression original)
   {
      this.original = original;
   }

   public PCD_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name, out);
   }

   /**
    * Log mutated line
    */
   public void visit( CastExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, original))
      {
         Expression expr = p.getExpression();
         if (expr instanceof AssignmentExpression
           || expr instanceof ConditionalExpression
           || expr instanceof BinaryExpression
           || expr instanceof InstanceofExpression
           || expr instanceof UnaryExpression)
         {
            writeParenthesis( expr );
         } 
         else 
         {
	        expr.accept( this );
         }
         // -------------------------------------------------------------
         mutated_line = line_num;
         writeLog(removeNewline(original.toString() + " => " + expr.toString()));
         // -------------------------------------------------------------
      }
      else
      {
         super.visit(p);
      }
   }
}
