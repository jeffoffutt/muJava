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
 * <p>Output and log PRV mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class PRV_Writer extends MutantCodeWriter
{
   AssignmentExpression original = null;
   String mutant = null;

  /** 
   * Set original source code and mutated code
   * @param original
   * @param mutant
   */
   public void setMutant(AssignmentExpression original, String mutant)
   {
      this.original = original;
      this.mutant = mutant;
   }

   public PRV_Writer( String file_name, PrintWriter out ) 
   {
      super(file_name, out);
   }

   public void visit( AssignmentExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, original))
      {
         Expression lexpr = p.getLeft();

         if (lexpr instanceof AssignmentExpression) 
         {
		 	writeParenthesis( lexpr );
         } 
         else 
         {
		    lexpr.accept( this );
         }

		 String operator = p.operatorString();
         out.print( " " + operator + " " );

         // -------------------------------------------------------------
	     mutated_line = line_num;
		 out.print(mutant);
		 writeLog(removeNewline(original.toString()+";  =>  "+ lexpr.toString() + " = " + mutant+";"));
		 // -------------------------------------------------------------

      }
      else
      {
         Expression lexpr = p.getLeft();

         if (lexpr instanceof AssignmentExpression) 
         {
	        writeParenthesis( lexpr );
         } 
         else 
         {
	        lexpr.accept( this );
         }

	     String operator = p.operatorString();
         out.print( " " + operator + " " );

         Expression rexp = p.getRight();
         rexp.accept( this );
      }
   }
}
