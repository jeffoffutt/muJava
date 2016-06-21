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
 * <p>Output and log IOD mutants</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class IOD_Writer extends MutantCodeWriter
{
   MethodDeclaration mutant = null;

   public void setMutant(MethodDeclaration mutant)
   {
      this.mutant = mutant;
   }

   public IOD_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name, out);
   }

   /**
    * Write and log mutated line to files
    */
   public void visit( MethodDeclaration p ) throws ParseTreeException
   {
      if (!(isSameObject(p, mutant)))
      {
         super.visit(p);
      }
      else
      {
         // -----------------------------------------------------
         mutated_line = line_num;
         String temp= mutant.getModifiers().toString() + " "
			  + mutant.getReturnType().getName()+ " "
		      + mutant.getName() +"("
			  + mutant.getParameters().toString()+")";
         writeLog(removeNewline(temp)+" is deleted.");
         // ----------------------------------------------------
         writeTab();
         out.print("// ");

         /*ModifierList*/
         ModifierList modifs = p.getModifiers();
         if (modifs != null) 
         {
            modifs.accept( this );
            if (! modifs.isEmptyAsRegular())  
               out.print( " " );
         }

         TypeName ts = p.getReturnType();
         ts.accept( this );

         out.print( " " );

         String name = p.getName();
         out.print( name );

         ParameterList params = p.getParameters();
         out.print( "(" );
         if (! params.isEmpty()) 
         {
            out.print( " " );  
            params.accept( this );  
            out.print( " " );
         } 
         else 
         {
            params.accept( this );
         }
         out.print( ")" );

         TypeName[] tnl = p.getThrows();
         if (tnl.length != 0) 
         {
            out.println(); 
            line_num++;
            writeTab();  
            writeTab();
            out.print( "// throws " );
            tnl[0].accept( this );
            for (int i = 1; i < tnl.length; ++i) 
            {
               out.print ( ", " );
               tnl[i].accept( this );
            }
         }
         out.println("{ ... }");  
         line_num++;
      }
   }
}
