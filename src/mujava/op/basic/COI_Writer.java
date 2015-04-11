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
 * <p>Output and log COI mutants to files </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */

public class COI_Writer extends TraditionalMutantCodeWriter
{
   BinaryExpression original_binary;
   Variable original_var;
   FieldAccess original_field;

   public COI_Writer( String file_name, PrintWriter out ) 
   {
      super(file_name, out);
   }

   /**
    * Set original source code
    * @param p
    */
   public void setMutant(BinaryExpression p)
   {
      original_binary = p;
   }

   /**
    * Set original source code
    * @param p
    */
   public void setMutant(Variable p)
   {
      original_var = p;
   }

   /**
    * Set original source code
    * @param p
    */
   public void setMutant(FieldAccess p)
   {
      original_field = p;
   }

   /**
    * Log mutated line
    */
   public void visit( BinaryExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, original_binary))
      {
         out.print("!("+p.toString()+")");
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toFlattenString()+ "  =>  " +"!("+p.toString()+")";
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
   public void visit( Variable p ) throws ParseTreeException
   {
      if (isSameObject(p, original_var))
      {
         out.print("!"+p.toString());
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toFlattenString()+ "  =>  " +"!"+p.toString();
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
   public void visit( FieldAccess p ) throws ParseTreeException
   {
      if (isSameObject(p, original_field))
      {
         out.print("!"+p.toString());
         // -----------------------------------------------------------
         mutated_line = line_num;
         String log_str = p.toFlattenString()+ "  =>  " +"!"+p.toString();
         writeLog(removeNewline(log_str));
         // -------------------------------------------------------------
      }
      else
      {
         super.visit(p);
      }
   }
}
