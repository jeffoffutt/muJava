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
package mujava.op.exception;

import java.io.*;
import openjava.ptree.*;
import mujava.op.util.MutantCodeWriter;

/**
 * <p>Description: </p>
 * @author Yu-Seung Ma
 * @version 1.0
  */ 

public class EFD_Writer extends MutantCodeWriter
{
  TryStatement mutant = null;

  public EFD_Writer( String file_name,PrintWriter out ) {
	super(file_name,out);
  }

  public void setMutant(TryStatement p){
    mutant = p;
  }

    public void visit( TryStatement p )
	throws ParseTreeException
    {
      if(!(isSameObject(p,mutant))){
        super.visit(p);
      }else{
        writeTab();
        out.print( "try " );
        StatementList stmts = p.getBody();
	    writeStatementsBlock( stmts );
        CatchList catchlist = p.getCatchList();
        if (! catchlist.isEmpty()) {
	    catchlist.accept( this );
        }
        // -------------------------
        mutated_line = line_num;
        writeLog(" finally block is deleted.");
        // -------------------------
        out.println(); line_num++;
      }
    }
}
