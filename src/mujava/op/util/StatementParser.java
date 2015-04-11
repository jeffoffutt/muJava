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


/**
 * <p>Description: </p>
 * @author Jeff Offutt and Yu-Seung Ma
 * @version 1.0
  */ 

package mujava.op.util;

import java.io.*;
import java.util.*;
import java.lang.Object;
import openjava.ptree.*;

public class StatementParser 
{
   public StatementParser () {} 
   
   public StatementParser (String s)
   {
	   str = s;        
   }
   
   /**
    * Read and extract a given string to form an openjava.ptree.IfStatement object.
    * The IfStatement object will be returned and used by if-statement deletion 
    * operator in order to mutate nested if-statement.
    * @param s
    * @param keyword
    * @return ifstmt 
    * @throws ParseTreeException
    */
   public IfStatement readString(String s, String keyword) throws ParseTreeException
   {
      IfStatement ifstmt = null;

      String str_line = new String();
      String temp = new String();
      Boolean is_expr = false;
      
      int token_cnt = 0;
      StringTokenizer str_token = new StringTokenizer(s);
      while (str_token.hasMoreTokens()) 
      {
         token_cnt++;
         if ( (str_token.toString()).equalsIgnoreCase("if") )
            is_expr = true;
         
         System.out.println("str_token[" + token_cnt + "] =" + str_token.nextToken());
         
      }
      System.out.println("done tokenizer");
      
      
      
      
      BinaryExpression bi_expr = null;     
      for (int i=0; i<s.length(); i++)
      {
    	 // If the current character is line feed, from a statement and 
    	 if (s.charAt(i) == 10) 
    	 {
            str_line = temp;
            System.out.println("str_line = " + str_line);
                        
            // (06/02/2009) need to loop this? check repeatedly?? 
            // If the current statement is an expression, set 
            // an expression of openjava.ptree.IfStatement object.
            // Otherwise, check if the statement contains "else"
            if (is_expr == true)
            {
//               Expression expr;
//               ifstmt.setExpression(str_line);
      
              
               is_expr = false;
               ifstmt.setExpression(bi_expr);
               bi_expr = null;       // reset expression of IfStatement
            }
            else 
            {
               StatementList stmtlist = new StatementList();
               Statement stmt;
               Expression expr; 
              
               
//               stmtlist.add(str_line);
               
//               ifstmt.setStatements(arg0)
            }
            temp = "";
            
    	 }
    	 
    	 // If the current character is space and if the previous token
    	 // is "if" keyword, set flag to indicate an expression
    	 if (s.charAt(i) == 32)
    	 {
            if (is_expr == true)            
            {            
               // (06/02/2009) expression format: (left op right)
               // currently, only simple expression is considered
                
            }
            
            if (temp.equalsIgnoreCase("if") == true)
                is_expr = true;

            
         }
    	 
         temp = temp + s.substring(i, i+1);

      }
      
      
      
      
      
      StatementList stmtlist = new StatementList();
      ifstmt.setStatements(stmtlist);
      return ifstmt;         
   }
   
   
   // Check if end of line
   private boolean is_eol()
   {

      return true;
   }
   
   
   String str = new String();    // string to be parsed
   
}   
