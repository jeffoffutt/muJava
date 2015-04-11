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
 * <p>Output and log IPC mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class IPC_Writer extends MutantCodeWriter
{
   ConstructorInvocation mutant = null;

   /**
    * Set mutated code
    * @param mutant
    */
   public void setMutant(ConstructorInvocation mutant)
   {
      this.mutant = mutant;
   }

   public IPC_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name, out);
   }

   /**
    * Write and log mutants to files
    */
   public void visit( ConstructorInvocation p ) throws ParseTreeException
   {
      if (isSameObject(p, mutant))
      {
	     mutated_line = line_num;
         writeTab();
	     out.println("// " + p.toString());
	     line_num++;
         writeLog(removeNewline(p.toString()+" is deleted"));
      }
      else
      {
         writeTab();
         if (p.isSelfInvocation()) 
         {
            out.print( "this" );
         } 
         else 
         {
            Expression enclosing = p.getEnclosing();
            if (enclosing != null) 
            {
               enclosing.accept( this );
               out.print( " . " );
            }
            out.print( "super" );
         }

         ExpressionList exprs = p.getArguments();
         writeArguments( exprs );

	     out.print( ";" );
         out.println(); 
         line_num++;
      }
   }
}
