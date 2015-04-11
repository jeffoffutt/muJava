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
 * <p>Output and log PNC mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class PNC_Writer extends MutantCodeWriter
{
   AllocationExpression original = null;
   AllocationExpression mutant = null;

   /**
    * Set original source code and mutated code
    * @param original
    * @param mutant
    */
   public void setMutant(AllocationExpression original, AllocationExpression mutant)
   {
      this.original = original;
      this.mutant = mutant;
   }

   public PNC_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name, out);
   }

   public void visit( AllocationExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, original))
      {
         // -------------------------------------------------------------
	     mutated_line = line_num;
	     visit(mutant);
	     writeLog(removeNewline(original.toString() + " => " + mutant.toString()));
         // -------------------------------------------------------------
      }
      else
      {
         Expression encloser = p.getEncloser();
         if (encloser != null) 
         {
            encloser.accept( this );
            out.print( " . " );
         }

         out.print( "new " );

         TypeName tn = p.getClassType();
         tn.accept( this );

         ExpressionList args = p.getArguments();
         writeArguments( args );

         MemberDeclarationList mdlst = p.getClassBody();
         if (mdlst != null) 
         {
            out.println( "{" );
            pushNest();  
            mdlst.accept( this );  
            popNest();
            writeTab();  
            out.print( "}" );
         }
      }
   }
}
