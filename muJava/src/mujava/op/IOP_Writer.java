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
 * <p>Output and log IOP mutants to file </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class IOP_Writer extends MutantCodeWriter implements IOP_Helper
{
   int index,mod;
   boolean flag = false;
   MethodDeclaration mutant_method = null;
   StatementList mutant_stmt_list = null;
   Statement mutant_stmt = null;

   /**
    * Set mutated code
    * @param stmt_list
    * @param index
    * @param mod
    */
   public void setMutant(StatementList stmt_list, int index, int mod)
   {
      //this.mutant_method = method;
      this.mutant_stmt_list = stmt_list;
      this.index = index;
      this.mod = mod;
   }

   public IOP_Writer( String file_name, PrintWriter out ) 
   {
	  super(file_name,out);
   }

   /**
    * Moves the calls to overridden methods to the first and
    * last statements of the method and up and down one statement
    * and log mutated line 
    */
   public void visit( StatementList p ) throws ParseTreeException
   {
      if (isSameObject(p, mutant_stmt_list))
      {
         for (int i=0; i<p.size(); i++)
         {
            switch (mod)
            {
               case FIRST: mutated_line = line_num;
                           writeLog("Overridden method call at first line.");
                           visit(p.get(index));
                           for (i=0; i<p.size(); i++)
                           {
                              if (i != index) 
                            	 visit(p.get(i));
                           }
                           break;

               case LAST:  for (i=0; i<p.size(); i++)
                           {
                              if (i != index) 
                            	 visit(p.get(i));
                           }
                           mutated_line = line_num;
                           writeLog("Overridden method call at last line.");
                           visit(p.get(index));
                           break;

               case UP:
                           for (i=0; i<p.size(); i++)
                           {
                              if (i == (index-1))
                              {
                                 mutated_line = line_num;
                                 writeLog("Overridden method call at one line up.");
                                 visit(p.get(index));
                              }
                              else if (i == index)
                              {
                                 visit(p.get(index-1));
                              }
                              else
                              {
                                 visit(p.get(i));
                              }
                           }
                           break;

               case DOWN:  mutated_line = line_num + index+1;
                           writeLog("Overridden method call at one line down.");
                           for (i=0; i<p.size(); i++)
                           {
                              if (i == (index))
                              {
                                 visit(p.get(index+1));
                              }
                              else if (i == index+1)
                              {
                                 mutated_line = line_num;
                                 writeLog("Overridden method call at one line up.");
                                 visit(p.get(index));
                              }
                              else
                              {
                                 visit(p.get(i));
                              }
                           }
                           break;
            }
         }
      }
      else
      {
         super.visit(p);
      }
   }
}
