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
 * <p>Output and log AMC mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class AMC_Writer extends MutantCodeWriter
{
   ModifierList original;
   ModifierList mutant;

   public AMC_Writer( String file_name, PrintWriter out ) 
   {
      super(file_name, out);
   }

   /**
    * Set original source code and mutated code
    * @param original
    * @param mutant
    */
   public void setMutant(ModifierList original, ModifierList mutant)
   {
      this.original = original;
      this.mutant = mutant;
   }

   /**
    * Log mutated line
    */
   public void visit( ModifierList p )throws ParseTreeException
   {
      if (isSameObject(p, original))
      {
         super.visit(mutant);
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toFlattenString()+ " => " + mutant.toFlattenString();
         writeLog(removeNewline(log_str));
         // -------------------------------------------------------------
      }
      else
      {
         super.visit(p);
      }
   }
}
