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
 * <p>Output and log SDL mutants to files </p>
 * 
 * @author Lin Deng
 * @version 1.0
  */

public class SDL_Writer extends TraditionalMutantCodeWriter
{
	StatementList original;
	StatementList mutant;
	Literal mutantBoolean;
	TryStatement originalTry;
	TryStatement mutantTry;
	IfStatement originalIf;
	IfStatement mutantIf;
	WhileStatement originalWhile;
	WhileStatement mutantWhile;
	ForStatement originalFor;
	ForStatement mutantFor;
	ReturnStatement originalReturn;
	ReturnStatement mutantReturn;
	CaseGroupList originalCase;
	CaseGroupList mutantCase;
	
   public SDL_Writer( String file_name, PrintWriter out ) 
   {
      super(file_name, out);
   }

   /**
    * Set original source code and mutated code
    * @param exp1
    * @param exp2
    */
   public void setMutant(CaseGroupList exp1, CaseGroupList exp2)
   {
	   originalCase = exp1;
	   mutantCase = exp2;
   }
   
   public void setMutant(StatementList exp1, StatementList exp2)
   {
      original = exp1;
      mutant = exp2;
   }
   
   public void setMutant(TryStatement exp1, TryStatement exp2)
   {
	   originalTry = exp1;
	   mutantTry = exp2;
   }
   public void setMutant(IfStatement exp1, IfStatement exp2)
   {
	   originalIf = exp1;
	   mutantIf = exp2;
   	
   }
   public void setMutant(WhileStatement exp1, WhileStatement exp2)
   {
	   originalWhile = exp1;
	   mutantWhile = exp2;
   	
   }
   public void setMutant(ForStatement exp1, ForStatement exp2)
   {
	   originalFor = exp1;
	   mutantFor = exp2;
   	
   }
   
   /**
    * Set original source code and mutated code
    * @param exp1
    * @param exp2
    */
   public void setMutant(StatementList exp1, Literal exp2)
   {
      original = exp1;
      mutantBoolean = exp2;
   }
   
   public void setMutant(TryStatement exp1, Literal exp2)
   {
	   originalTry = exp1;
      mutantBoolean = exp2;
   }
   
   public void setMutant(IfStatement exp1, Literal exp2)
   {
	   originalIf = exp1;
      mutantBoolean = exp2;
   }
   public void setMutant(WhileStatement exp1, Literal exp2)
   {
	   originalWhile = exp1;
      mutantBoolean = exp2;
   }
   public void setMutant(ForStatement exp1, Literal exp2)
   {
	   originalFor = exp1;
      mutantBoolean = exp2;
   }
   public void setMutant(ReturnStatement exp1, ReturnStatement exp2)
   {
	   originalReturn = exp1;
	   mutantReturn = exp2;
   	
   }
   public void setMutant(ReturnStatement exp1, Literal exp2)
   {
	   originalReturn = exp1;
      mutantBoolean = exp2;
   }
   
   /**
    * Log mutated line
    */
   public void visit( StatementList p ) throws ParseTreeException
   {
	   
	   if(mutant != null){
	      if (isSameObject(p, original))
	      {
	         super.visit(mutant);
	         // -----------------------------------------------------------
	         mutated_line = line_num;
	         String log_str = p.toFlattenString()+ "  =>  " + mutant.toFlattenString();
	         writeLog(removeNewline(log_str));
	         // -------------------------------------------------------------
	      }
	      else
	      {
	         super.visit(p);
	      }
	   }
	   else{
		   if (isSameObject(p, original))
		      {
		         super.visit(mutantBoolean);
		         // -----------------------------------------------------------
		         mutated_line = line_num;
		         String log_str = p.toFlattenString()+ "  =>  " + mutantBoolean.toFlattenString();
		         writeLog(removeNewline(log_str));
		         // -------------------------------------------------------------
		      }
		      else
		      {
		         super.visit(p);
		      }
	   }
   }

   public void visit( TryStatement p ) throws ParseTreeException
   {
	   
	   if(mutantTry != null){
	      if (isSameObject(p, originalTry))
	      {
	         super.visit(mutantTry);
	         // -----------------------------------------------------------
	         mutated_line = line_num;
	         String log_str = p.toFlattenString()+ "  =>  " + mutantTry.toFlattenString();
	         writeLog(removeNewline(log_str));
	         // -------------------------------------------------------------
	      }
	      else
	      {
	         super.visit(p);
	      }
	   }
	   else{
		   if (isSameObject(p, originalTry))
		      {
		         super.visit(mutantBoolean);
		         // -----------------------------------------------------------
		         mutated_line = line_num;
		         String log_str = p.toFlattenString()+ "  =>  " + mutantBoolean.toFlattenString();
		         writeLog(removeNewline(log_str));
		         // -------------------------------------------------------------
		      }
		      else
		      {
		         super.visit(p);
		      }
	   }
   }

   public void visit( IfStatement p ) throws ParseTreeException
   {
	   
	   if(mutantIf != null){
	      if (isSameObject(p, originalIf))
	      {
	         super.visit(mutantIf);
	         // -----------------------------------------------------------
	         mutated_line = line_num;
	         String log_str = p.toFlattenString()+ "  =>  " + mutantIf.toFlattenString();
	         writeLog(removeNewline(log_str));
	         // -------------------------------------------------------------
	      }
	      else
	      {
	         super.visit(p);
	      }
	   }
	   else{
		   if (isSameObject(p, originalIf))
		      {
		         super.visit(mutantBoolean);
		         // -----------------------------------------------------------
		         mutated_line = line_num;
		         String log_str = p.toFlattenString()+ "  =>  " + mutantBoolean.toFlattenString();
		         writeLog(removeNewline(log_str));
		         // -------------------------------------------------------------
		      }
		      else
		      {
		         super.visit(p);
		      }
	   }
   }
   public void visit( WhileStatement p ) throws ParseTreeException
   {
	   
	   if(mutantWhile != null){
	      if (isSameObject(p, originalWhile))
	      {
	         super.visit(mutantWhile);
	         // -----------------------------------------------------------
	         mutated_line = line_num;
	         String log_str = p.toFlattenString()+ "  =>  " + mutantWhile.toFlattenString();
	         writeLog(removeNewline(log_str));
	         // -------------------------------------------------------------
	      }
	      else
	      {
	         super.visit(p);
	      }
	   }
	   else{
		   if (isSameObject(p, originalWhile))
		      {
		         super.visit(mutantBoolean);
		         // -----------------------------------------------------------
		         mutated_line = line_num;
		         String log_str = p.toFlattenString()+ "  =>  " + mutantBoolean.toFlattenString();
		         writeLog(removeNewline(log_str));
		         // -------------------------------------------------------------
		      }
		      else
		      {
		         super.visit(p);
		      }
	   }
   }

   public void visit( ForStatement p ) throws ParseTreeException
   {
	   
	   if(mutantFor != null){
	      if (isSameObject(p, originalFor))
	      {
	         super.visit(mutantFor);
	         // -----------------------------------------------------------
	         mutated_line = line_num;
	         String log_str = p.toFlattenString()+ "  =>  " + mutantFor.toFlattenString();
	         writeLog(removeNewline(log_str));
	         // -------------------------------------------------------------
	      }
	      else
	      {
	         super.visit(p);
	      }
	   }
	   else{
		   if (isSameObject(p, originalFor))
		      {
		         super.visit(mutantBoolean);
		         // -----------------------------------------------------------
		         mutated_line = line_num;
		         String log_str = p.toFlattenString()+ "  =>  " + mutantBoolean.toFlattenString();
		         writeLog(removeNewline(log_str));
		         // -------------------------------------------------------------
		      }
		      else
		      {
		         super.visit(p);
		      }
	   }
   }
   public void visit( ReturnStatement p ) throws ParseTreeException
   {
	   
	   if(mutantReturn != null){
	      if (isSameObject(p, originalReturn))
	      {
	         super.visit(mutantReturn);
	         // -----------------------------------------------------------
	         mutated_line = line_num;
	         String log_str = p.toFlattenString()+ "  =>  " + mutantReturn.toFlattenString();
	         writeLog(removeNewline(log_str));
	         // -------------------------------------------------------------
	      }
	      else
	      {
	         super.visit(p);
	      }
	   }
	   else{
		   if (isSameObject(p, originalReturn))
		      {
		         super.visit(mutantBoolean);
		         // -----------------------------------------------------------
		         mutated_line = line_num;
		         String log_str = p.toFlattenString()+ "  =>  " + mutantBoolean.toFlattenString();
		         writeLog(removeNewline(log_str));
		         // -------------------------------------------------------------
		      }
		      else
		      {
		         super.visit(p);
		      }
	   }
   }
   public void visit( CaseGroupList p ) throws ParseTreeException
   {
	   
	   if(mutantCase != null){
	      if (isSameObject(p, originalCase))
	      {
	         super.visit(mutantCase);
	         // -----------------------------------------------------------
	         mutated_line = line_num;
	         String log_str = p.toFlattenString()+ "  =>  " + mutantCase.toFlattenString();
	         writeLog(removeNewline(log_str));
	         // -------------------------------------------------------------
	      }
	      else
	      {
	         super.visit(p);
	      }
	   }
	   else{
		   if (isSameObject(p, originalCase))
		      {
		         super.visit(mutantBoolean);
		         // -----------------------------------------------------------
		         mutated_line = line_num;
		         String log_str = p.toFlattenString()+ "  =>  " + mutantBoolean.toFlattenString();
		         writeLog(removeNewline(log_str));
		         // -------------------------------------------------------------
		      }
		      else
		      {
		         super.visit(p);
		      }
	   }
   }

}
