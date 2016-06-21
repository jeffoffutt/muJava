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
 * <p>Output and log EOC mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class EOC_Writer extends MutantCodeWriter
{
   MethodCall original_methodcall = null;
   BinaryExpression original_expression = null;
   String mutant = null;

   /**
    * Set original source code and mutated code
    * @param original
    * @param mutant
    */
   public void setMutant(BinaryExpression original, String mutant)
   {
      this.mutant = mutant;
      this.original_expression = original;
   }

   /**
    * Set original method call
    * @param original
    */
   public void setMutant(MethodCall original)
   {
      this.original_methodcall = original;
   }

   public EOC_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name, out);
   }

   public void visit( MethodCall p ) throws ParseTreeException
   {
      if (isSameObject(p, original_methodcall))
      {
         Expression expr = p.getReferenceExpr();
         TypeName reftype = p.getReferenceType();

         if (expr != null) 
         {
            if (expr instanceof Leaf  ||
                expr instanceof ArrayAccess  ||
                expr instanceof FieldAccess  ||
                expr instanceof MethodCall   ||
                expr instanceof Variable) 
            {
               expr.accept( this );
            } 
            else 
            {
		       writeParenthesis( expr );
            }
         } 
         else if (reftype != null) 
         {
            reftype.accept( this );
	     }

         out.print( " == " );
         // -------------------------------------------------------------
         mutated_line = line_num;
         out.print(mutant);
         writeLog(removeNewline(" .equal() =>  == "));
         // -------------------------------------------------------------
         ExpressionList args = p.getArguments();
         args.accept( this );

      } 
      else
      {
         super.visit(p);
      } 
   }

   public void visit( BinaryExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, original_expression))
      {
         // -------------------------------------------------------------
         mutated_line = line_num;
         out.print(mutant);
         writeLog(removeNewline(original_expression.toString()+" => "+mutant));
         // -------------------------------------------------------------
      }
      else
      {
         Expression lexpr = p.getLeft();
         if (isOperatorNeededLeftPar( p.getOperator(), lexpr )) 
         {
	        writeParenthesis( lexpr );
         } 
         else 
         {
            lexpr.accept( this );
         }

         String operator = p.operatorString();
         out.print( " " + operator + " " );

         Expression rexpr = p.getRight();
         if (isOperatorNeededRightPar( p.getOperator(), rexpr )) 
         {
	        writeParenthesis( rexpr );
         } 
         else 
         {
            rexpr.accept( this );
         }
      }
   }
}
