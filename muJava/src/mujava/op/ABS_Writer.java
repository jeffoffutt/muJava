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
 * <p>Output and log ABS mutants to files </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class ABS_Writer extends MutantCodeWriter
{
   final int ZERO = 0;
   final int NEGATIVE = 1;

   BinaryExpression original_exp;
   Variable original_var;
   MethodCall original_call;
   int flag;

   public ABS_Writer( String file_name, PrintWriter out ) 
   {
      super(file_name, out);
   }

   /**
    * Set a zero flag to a given method call
    * @param exp
    */
   public void setZeroFlag(MethodCall exp)
   {
      flag = ZERO;
      original_call = exp;
   }

   /**
    * Set a negative flag to a given method call
    * @param exp
    */
   public void setNegativeFlag(MethodCall exp)
   {
      flag = NEGATIVE;
      original_call = exp;
   }

   /**
    * Set a zero flag to a given expression
    * @param exp
    */
   public void setZeroFlag(BinaryExpression exp)
   {
      flag = ZERO;
      original_exp = exp;
   }

   /**
    * Set a negative flag to a given expression
    * @param exp
    */
   public void setNegativeFlag(BinaryExpression exp)
   {
      flag = NEGATIVE;
      original_exp = exp;
   }

   /**
    * Set a zero flag to a given instance variable
    * @param var
    */
   public void setZeroFlag(Variable var)
   {
      flag = ZERO;
      original_var = var;
   }

   /**
    * Set a negative flag to a given instance variable
    * @param var
    */
   public void setNegativeFlag(Variable var)
   {
      flag = NEGATIVE;
      original_var = var;
   }

   /**
    * Log mutated line
    */
   public void visit( BinaryExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, original_exp))
      {
         if (flag == ZERO)
         {
            out.print(0);
            // -----------------------------------------------------------
            mutated_line = line_num;
            String log_str = p.toString()+ "  =>  0 ";
            writeLog(removeNewline(log_str));
            // -------------------------------------------------------------
         }
         else if (flag == NEGATIVE)
         {
            out.print("(-("+p.toString()+"))");
             // -----------------------------------------------------------
            mutated_line = line_num;
            String log_str = p.toString()+ "  =>  -("+p.toString()+")";
            writeLog(removeNewline(log_str));
            // -------------------------------------------------------------
         }
      }
      else
      {
         super.visit(p);
      }
   }

   /**
    * Log mutated line
    */
   public void visit( MethodCall p ) throws ParseTreeException
   {
      if (isSameObject(p, original_call))
      {
         if (flag == ZERO)
         {
            out.print(0);
            // -----------------------------------------------------------
            mutated_line = line_num;
            String log_str = p.toString()+ "  =>  0 ";
            writeLog(removeNewline(log_str));
            // -------------------------------------------------------------
         }
         else if (flag == NEGATIVE)
         {
            out.print("(-("+p.toString()+"))");
            // -----------------------------------------------------------
            mutated_line = line_num;
            String log_str = p.toString()+ "  =>  -("+p.toString()+")";
            writeLog(removeNewline(log_str));
            // -------------------------------------------------------------
         }
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
         if (flag == ZERO)
         {
            out.print(0);
            // -----------------------------------------------------------
            mutated_line = line_num;
            String log_str = p.toString()+ "  =>  0 ";
            writeLog(removeNewline(log_str));
            // -------------------------------------------------------------
         }
         else if (flag == NEGATIVE)
         {
            out.print("(-"+p.toString()+")");
            // -----------------------------------------------------------
            mutated_line = line_num;
            String log_str = p.toString()+ "  =>  -"+p.toString();
            writeLog(removeNewline(log_str));
            // -------------------------------------------------------------
         }
      }
      else
      {
         super.visit(p);
      }
   }
}
