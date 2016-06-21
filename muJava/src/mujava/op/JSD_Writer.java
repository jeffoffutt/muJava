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
 * <p>Output and log JSD mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class JSD_Writer extends MutantCodeWriter
{
   FieldDeclaration mutant = null;
   boolean isMutantTarget = false;

   /**
    * Set mutated code
    * @param f
    */
   public void setMutant(FieldDeclaration f)
   {
      mutant = f;
      isMutantTarget = false;
   }

   public JSD_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name,out);
   }

   public void visit( FieldDeclaration p ) throws ParseTreeException
   {
      if (isSameObject(p, mutant))
      {
         isMutantTarget = true;
         super.visit(p);
         isMutantTarget = false;
      }
      else
      {
         super.visit(p);
      }
   }

   public void visit( ModifierList p ) throws ParseTreeException
   {
      if (isMutantTarget)
      {
         ModifierList temp = (ModifierList)p.makeCopy();

         int mod = temp.getRegular();
         mod &= (~ModifierList.STATIC);
         temp.setRegular(mod);
         //temp.delete(ModifierList.STATIC);
         super.visit(temp);
         // -------------------------------------------------------------
         mutated_line = line_num;
         writeLog(removeNewline("static is deleted"));
         // -------------------------------------------------------------
      }
      else
      {
         super.visit(p);
      }
   }
}
