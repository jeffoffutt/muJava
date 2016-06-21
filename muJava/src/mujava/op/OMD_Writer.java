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
import openjava.mop.*;
import mujava.op.util.MutantCodeWriter;

/**
 * <p>Output and log OMD mutants to files</p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class OMD_Writer extends MutantCodeWriter
{
   MethodDeclaration original = null;
   OJMethod mutant = null;
   OJClass[] mutant_pars = null;
   boolean flag = false;

   /**
    * Set original source code and mutated code
    * @param original
    * @param mutant
    */
   public void setMutant(MethodDeclaration original, OJMethod mutant) 
   {
      this.original = original;
      this.mutant = mutant;
      this.mutant_pars = mutant.getParameterTypes();
   }

   public OMD_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name,out);
   }

   public void visit( MethodDeclaration p ) throws ParseTreeException
   {
      if (!(isSameObject(p, original)))
      {
         super.visit(p);
      }
      else
      {
        // �޼ҵ带 �ٷ� ����� ������, ���� �����Ͱ� ������ �� �� original program�� �ñ׳��Ŀ� ���� ���ߵǾ�
        // �ݵ�� �����ؾ� �ϹǷ�, ������ �ʰ� redirection �Ѵ�.
         flag = true;
         super.visit(p);
         flag = false;
      }
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
         String temp_str = original.getName() + "(" + original.getParameters().toString() +")";
         writeLog(removeNewline(temp_str+" => "+mutant.signature()));
         //----------------------------------------------------------
         temp_str = original.getName() + "(";
         ParameterList pl = original.getParameters();
         
         for (int i=0; i<pl.size()-1; i++)
         {
            Parameter par = pl.get(i);
            String mutated_type = mutant_pars[i].getName();
            
            if (par.getTypeSpecifier().getName().equals(mutated_type))
            {
               temp_str = temp_str + par.getVariable() + ",";
            }
            else
            {
               temp_str = temp_str + "(" + mutated_type + ")" + par.getVariable() + ",";
            }
	     }
         
         String mutated_type = mutant_pars[pl.size()-1].getName();
         
         if (pl.get(pl.size()-1).getTypeSpecifier().getName().equals(mutated_type))
         {
            temp_str = temp_str+pl.get(pl.size()-1).getVariable()+")";
         }
         else
         {
            temp_str = temp_str + "(" + mutated_type + ")" + pl.get(pl.size()-1).getVariable() + ")";
         }
         
	     writeLog(removeNewline("Redirect to  => " + temp_str));
	     writeTab();
	     
         if (original.getReturnType().toString().equals("void"))
         {
            out.println(temp_str+";");
         }
         else
         {
            out.println("return " + temp_str+";");
         }
         line_num++;
      }
   }
}
