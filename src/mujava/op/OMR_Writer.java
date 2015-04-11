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
 * <p>Output and log OMR mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class OMR_Writer extends MutantCodeWriter
{
   MethodDeclaration original = null;
   String mutant = null;
   boolean flag = false;
   boolean isVoid = true;
   
   /**
    * Set original source code and mutated code
    * @param original
    * @param mutant
    */
   public void setMutant(MethodDeclaration original, String mutant)
   {
      this.mutant = mutant;
      this.original = original;
   }

   public OMR_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name,out);
   }

   public void visit( StatementList p ) throws ParseTreeException
   {
      if (!flag)
      {
		 super.visit(p);
      }
      else
      {
		 //-------------------------------------------------------
		 mutated_line = line_num;
		 String temp_str = original.getName() + "(";
		 ParameterList pl = original.getParameters();
		 
		 for (int i=0; i<pl.size()-1; i++)
		 {
			temp_str = temp_str + pl.get(i).getVariable() + ",";
		 }
		 temp_str = temp_str+pl.get(pl.size()-1).getVariable()+")";
		 writeLog(removeNewline(temp_str+" => "+mutant));
		 //----------------------------------------------------------
		 
		 writeTab();
		 if (isVoid)
		 {
		    out.println(mutant);
		 }
		 else
		 {
		    out.println("return " + mutant);
		 }
		 line_num++;
		 flag = false;
      }
   }

    public void visit( MethodDeclaration p ) throws ParseTreeException
    {
      if(isSameObject(p, original))
      {
		 flag = true;
		 if (!p.getReturnType().toString().equals("void")) 
			isVoid = false;
      }
      super.visit(p);
    }

}
