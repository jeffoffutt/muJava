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
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class LCR_Writer extends MutantCodeWriter{

  BinaryExpression binary_original;
  UnaryExpression unary_original;
  int mutant_op;

  public LCR_Writer( String file_name, PrintWriter out ) {
    super(file_name,out);
  }

  public void setMutant(BinaryExpression exp, int op){
    binary_original = exp;
    mutant_op = op;
  }

  public void setMutant(UnaryExpression exp){
    unary_original = exp;
  }

  public void visit( BinaryExpression p )
  throws ParseTreeException
  {
    if(isSameObject(p,binary_original)){
    	BinaryExpression mutant_exp;
    	mutant_exp = (BinaryExpression)p.makeRecursiveCopy(); 
    	mutant_exp.setOperator(mutant_op);
    	super.visit(mutant_exp);

    	String operator = mutant_exp.operatorString();
    	out.print( " " + operator + " " );
	    // -----------------------------------------------------------
	    mutated_line = line_num;
	    String log_str = p.operatorString()+ " => " + operator;
	    writeLog(removeNewline(log_str));
	    // -------------------------------------------------------------

    	mutant_exp = null;
    }else{
      super.visit(p);
    }
  }

  public void visit( UnaryExpression p )
  throws ParseTreeException
  {
    if(isSameObject(p,unary_original)){
      if (p.isPrefix()) {
        // -----------------------------------------------------------
        mutated_line = line_num;
        String log_str = p.toString()+ " => " + p.toString().substring(1);
        writeLog(removeNewline(log_str));
        // -------------------------------------------------------------

      }else{
        out.print("!");
        // -----------------------------------------------------------
        mutated_line = line_num;
        String log_str = p.toString()+ " => !" + p.toString();
        writeLog(removeNewline(log_str));
        // -------------------------------------------------------------

      }

      Expression expr = p.getExpression();
        if (expr instanceof AssignmentExpression
	    || expr instanceof ConditionalExpression
	    || expr instanceof BinaryExpression
	    || expr instanceof InstanceofExpression
	    || expr instanceof CastExpression
	    || expr instanceof UnaryExpression){
	    writeParenthesis( expr );
        } else {
	    expr.accept( this );
        }

        if (p.isPostfix()) {
	    String operator = p.operatorString();
	    out.print( operator );


        }
    }else{
      super.visit(p);
    }
  }
}
