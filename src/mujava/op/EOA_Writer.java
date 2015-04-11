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
 * <p>Output and log EOA mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class EOA_Writer extends MutantCodeWriter
{
   MethodCall original_methodcall = null;
   AssignmentExpression original = null;
   String mutant = null;

   ExpressionStatement original_stmt = null;

   /**
    * Set original source code and mutated code
    * @param original
    * @param mutant
    */
   public void setMutant(AssignmentExpression original, String mutant)
   {
      this.mutant = mutant;
      this.original = original;
   }

   /**
    * Set original source code and mutated code
    * @param original
    * @param mutant
    */
   public void setMutant(ExpressionStatement original, String mutant)
   {
      this.mutant = mutant;
      this.original_stmt = original;
   }

   /**
    * Set original method call
    * @param original
    */
   public void setMutant(MethodCall original)
   {
      this.original_methodcall = original;
   }

   public EOA_Writer( String file_name, PrintWriter out ) 
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
            if (expr instanceof Leaf ||
                expr instanceof ArrayAccess ||
                expr instanceof FieldAccess ||
                expr instanceof MethodCall ||
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

         // -------------------------------------------------------------
         mutated_line = line_num;
         out.print(mutant);
         writeLog(removeNewline(p.toString()+" =>  " + p.toString().substring(0,p.toString().length()-".clone()".length())));
         // -------------------------------------------------------------

      }
      else
      {
         super.visit(p);
      }
   }
	 
   public void visit( ExpressionStatement p ) throws ParseTreeException
   {
      if (isSameObject(p, original_stmt))
      {
		 // -------------------------------------------------------------
		 writeTab(); out.println("try{");
		 line_num++;
		 mutated_line = line_num;
		 pushNest();
		 writeTab(); 
		 out.println(mutant+";");
		 popNest();
		 writeLog(removeNewline(original_stmt.toString() + " => " + mutant));
		 writeTab(); 
		 out.println("}catch(CloneNotSupportedException cnse){");
		 line_num++;
		 pushNest();
		 writeTab(); 
		 out.println("System.err.println(cnse);");
		 popNest();
		 line_num++;
		 writeTab(); 
		 out.println("}");
		 line_num++;
		 // -------------------------------------------------------------
      }
      else
      {
		 super.visit(p);
      }
   }

   public void visit( AssignmentExpression p ) throws ParseTreeException
   {
      if (isSameObject(p, original))
      {
		 // -------------------------------------------------------------
		 mutated_line = line_num;
		 out.print(mutant);
		 writeLog(removeNewline(original.toString() + " => " + mutant));
		 // -------------------------------------------------------------
      }
      else
      {
		 super.visit(p);
      }
   }

}
